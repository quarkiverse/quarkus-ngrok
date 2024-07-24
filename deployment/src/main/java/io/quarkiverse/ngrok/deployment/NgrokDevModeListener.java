package io.quarkiverse.ngrok.deployment;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.jboss.logging.Logger;

import io.quarkiverse.ngrok.runtime.NgrokConfig;
import io.quarkus.bootstrap.app.RunningQuarkusApplication;
import io.quarkus.deployment.dev.DevModeListener;
import io.quarkus.fs.util.ZipUtils;
import io.quarkus.utilities.OS;

public class NgrokDevModeListener implements DevModeListener {

    private static final Logger log = Logger.getLogger(NgrokDevModeListener.class);

    private static final String VERSION_STRING = "bNyj1mQVY4c";

    private Process ngrokProcess;

    @Override
    public void afterFirstStart(RunningQuarkusApplication application) {
        try {
            startNgrokIfNecessary(application);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void beforeShutdown() {
        if ((ngrokProcess != null) && ngrokProcess.isAlive()) {
            ngrokProcess.destroy();
        }
    }

    private void startNgrokIfNecessary(RunningQuarkusApplication runner) throws IOException {
        var ngrokEnabledOpt = runner.getConfigValue("quarkus.ngrok.enabled", Boolean.class);
        if (ngrokEnabledOpt.isEmpty() || !ngrokEnabledOpt.get()) {
            log.debugf("ngrok will not be started because 'quarkus.ngrok.enabled' is specified and not set to 'true'");
            return;
        }

        var ngrokDirOpt = runner.getConfigValue("quarkus.ngrok.directory", String.class);
        if (ngrokDirOpt.isEmpty()) {
            // should not happen
            return;
        }
        var ngrokDir = Paths.get(ngrokDirOpt.get());
        if (!Files.exists(ngrokDir)) {
            Files.createDirectories(ngrokDir);
        }

        var os = OS.determineOS();
        var architecture = OS.getArchitecture();
        var downloadURL = determineDownloadURL(runner.getConfigValue("quarkus.ngrok.download-url", String.class), os,
                architecture);

        Runnable r = () -> {
            Path ngrokBinary = toNgrokBinaryPath(ngrokDir, os);
            if (!Files.exists(ngrokBinary)) {
                try {
                    ngrokBinary = downloadNgrok(downloadURL, ngrokDir, os);
                } catch (IOException e) {
                    log.warn("Unable to download ngrok", e);
                    return;
                }
            }
            if (!deleteCertificatePolicyIfNecessary(ngrokBinary, runner)) {
                log.debug("Failed to delete certificate policy");
            }
            if (!startNgrok(ngrokBinary, runner)) {
                return;
            }

            // TODO: this may have to be configurable - to do so we would need to generate an ngrok YAML config file
            // and pass its path on the command line
            String ngrokHost = "localhost";
            int ngrokPort = runner.getConfigValue("quarkus.ngrok.port", Integer.class).orElse(4040);

            var ngrokClient = new VertxNgrokClient(ngrokHost, ngrokPort);
            var isRunning = ngrokClient.isRunning();
            if (!isRunning) {
                log.warn("Unable to determine ngrok status");
                ngrokClient.close();
                return;
            }
            String webInterfaceURL = "http://" + ngrokHost + ":" + ngrokPort;
            log.infof("ngrok is running and its web interface can be accessed at: '%s'", webInterfaceURL);
            try {
                String publicURL = ngrokClient.getPublicURL();
                log.infof("The application can be accessed publicly over the internet using: '%s'", publicURL);
                setDevUIInfo(runner, publicURL, webInterfaceURL);
            } catch (Exception e) {
                log.error(e);
            } finally {
                ngrokClient.close();
            }
        };
        var ngrokThread = new Thread(r);
        ngrokThread.setName("ngrok starter thread");
        ngrokThread.setDaemon(true);
        ngrokThread.start();
    }

    // needs to be done via reflection as NgrokInfoSupplier is invoked by Quarkus using the runtime classloader
    private void setDevUIInfo(RunningQuarkusApplication runner, String publicURL, String webInterfaceURL) {
        try {
            Class<?> ngrokInfoSupplierClass = runner.getClassLoader().loadClass(
                    "io.quarkiverse.ngrok.runtime.devui.NgrokInfoSupplier");
            Method setPublicURLMethod = ngrokInfoSupplierClass.getMethod("setPublicURL", String.class);
            setPublicURLMethod.invoke(null, publicURL);

            Method setWebInterfaceURLMethod = ngrokInfoSupplierClass.getMethod("setWebInterfaceURL", String.class);
            setWebInterfaceURLMethod.invoke(null, webInterfaceURL);
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            log.warn("Unable to setup the DevUI with ngrok info", e);
        }
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private String determineDownloadURL(Optional<String> customURL, OS os, String architecture) {
        return customURL.orElseGet(() -> createDownloadURL(os, architecture));

    }

    private String createDownloadURL(OS os, String architecture) {
        return String.format("https://bin.equinox.io/c/%s/ngrok-stable-%s-%s.zip", VERSION_STRING,
                determineOSString(os), determineArchString(architecture));
    }

    private String determineOSString(OS os) {
        return switch (os) {
            case LINUX -> "linux";
            case MAC -> "darwin";
            case WINDOWS -> "windows";
            default -> throw new RuntimeException("Unsupported OS '" + os + "'");
        };
    }

    // based on https://ngrok.com/docs/agent/config/#default-locations
    private Path determineDefaultConfigLocation(OS os) {
        return switch (os) {
            case LINUX -> Paths.get(System.getProperty("user.home"), ".config/ngrok/ngrok.yml");
            case MAC -> Paths.get(System.getProperty("user.home"), "Library/Application Support/ngrok/ngrok.yml");
            case WINDOWS -> Paths.get(System.getProperty("user.home"), "AppData/Local/ngrok/ngrok.yml");
            default -> throw new RuntimeException("Unsupported OS '" + os + "'");
        };
    }

    private String determineArchString(String architecture) {
        return switch (architecture) {
            case "x86_64" -> "amd64";
            case "x86_32" -> "386";
            case "aarch_64" -> "arm64";
            default -> throw new RuntimeException("Unsupported architecture '" + architecture + "'");
        };
    }

    private String determineBinaryName(OS os) {
        if (os == OS.WINDOWS) {
            return "ngrok.exe";
        }
        return "ngrok";
    }

    private Path downloadNgrok(String downloadURL, Path ngrokDir, OS os) throws IOException {
        log.info("Attempting to download ngrok from " + downloadURL);
        var ngrokZip = Files.createTempFile("ngrok", "zip");
        try (BufferedInputStream in = new BufferedInputStream(new URL(downloadURL).openStream());
                FileOutputStream fileOutputStream = new FileOutputStream(ngrokZip.toFile())) {
            byte[] dataBuffer = new byte[10240];
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer, 0, 10240)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
            }
        }
        ZipUtils.unzip(ngrokZip, ngrokDir);
        log.info("ngrok downloaded and extracted to " + ngrokDir);
        Path ngrokBinary = toNgrokBinaryPath(ngrokDir, os);
        if (os != OS.WINDOWS) {
            try {
                log.debug("Attempting to make ngrok an executable");
                new ProcessBuilder("chmod", "+x", ngrokBinary.toAbsolutePath().toString())
                        .redirectError(ProcessBuilder.Redirect.DISCARD.file())
                        .redirectOutput(ProcessBuilder.Redirect.DISCARD.file()).start().waitFor(1, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                throw new RuntimeException("Unable to make ngrok an executable");
            }
        }
        return ngrokBinary;
    }

    private boolean deleteCertificatePolicyIfNecessary(Path ngrokBinary, RunningQuarkusApplication runner) {
        Optional<String> apiKey = runner.getConfigValue("quarkus.ngrok.api-key", String.class);
        Optional<String> domainId = runner.getConfigValue("quarkus.ngrok.delete-certificate-domain-id", String.class);
        if (apiKey.isEmpty() || domainId.isEmpty()) {
            return true;
        }

        String command = String.format("%s api --api-key %s reserved-domains delete-certificate-management-policy %s",
                ngrokBinary.toAbsolutePath(), apiKey.get(), domainId.get());
        log.infof("Attempting to delete certificates: '%s'", command);

        try {
            // Execute the command
            Process process = Runtime.getRuntime().exec(command);
            String stderr = IOUtils.toString(process.getErrorStream(), StandardCharsets.UTF_8);
            String stdout = IOUtils.toString(process.getInputStream(), StandardCharsets.UTF_8);
            if (StringUtils.isNotBlank(stdout)) {
                log.info(stdout);
            }
            if (StringUtils.isNotBlank(stderr)) {
                log.error(stderr);
            }

            // Wait for the process to finish and check the exit value
            int exitCode = process.waitFor();
            log.debugf("Delete Certificate Exit Code: %d", exitCode);
            return exitCode == 0;
        } catch (IOException | InterruptedException e) {
            log.warn("Unable to call ngrok api", e);
            return false;
        }
    }

    private boolean startNgrok(Path ngrokBinary, RunningQuarkusApplication runner) {
        Integer quarkusHttpPort = runner.getConfigValue("quarkus.http.port", Integer.class).orElse(8080);

        List<String> command = new ArrayList<>();

        Optional<String> authToken = runner.getConfigValue("quarkus.ngrok.auth-token", String.class);
        if (authToken.isEmpty()) {
            log.warn("The 'quarkus.ngrok.auth-token' property is likely required by ngrok");
        }
        Optional<NgrokConfig.Region> region = runner.getConfigValue("quarkus.ngrok.region", NgrokConfig.Region.class);
        Optional<Integer> ngrokHttpPort = runner.getConfigValue("quarkus.ngrok.port", Integer.class);
        Optional<String> ngrokDomain = runner.getConfigValue("quarkus.ngrok.domain", String.class);
        Optional<String> ngrokTunnel = runner.getConfigValue("quarkus.ngrok.tunnel-name", String.class);

        String configs = null;
        if (authToken.isPresent() || region.isPresent() || ngrokHttpPort.isPresent()) {
            StringBuilder sb = new StringBuilder();
            sb.append("version: 2").append(System.lineSeparator());
            authToken.ifPresent(s -> sb.append("authtoken: ").append(s).append(System.lineSeparator()));
            region.ifPresent(value -> sb.append("region: ").append(value.getName()).append(System.lineSeparator()));
            ngrokHttpPort.ifPresent(
                    integer -> sb.append("web_addr: ").append("127.0.0.1:").append(integer).append(System.lineSeparator()));
            try {
                Path configFile = Files.createTempFile("ngrok", ".yml");
                Files.writeString(configFile, sb.toString());

                configs = configFile.toAbsolutePath().toString();
                Path defaultConfig = determineDefaultConfigLocation(OS.determineOS());
                if (Files.exists(defaultConfig)) {
                    // last one wins
                    configs = defaultConfig.toAbsolutePath() + "," + configs;
                }
            } catch (IOException e) {
                log.warn("Unable to create ngrok configuration file", e);
                return false;
            }
        }

        command.add(ngrokBinary.toAbsolutePath().toString());
        if (ngrokTunnel.isPresent()) {
            command.add("start");
            if (configs != null)
                command.add("--config=" + configs);
            command.add(ngrokTunnel.get());
        } else {
            command.add("http");
            if (configs != null)
                command.add("--config=" + configs);
            ngrokDomain.ifPresent(s -> command.add("--domain=" + s));
            command.add(quarkusHttpPort.toString());
        }

        log.debugf("Attempting to start ngrok using: '%s'", String.join(" ", command));
        ProcessBuilder processBuilder = new ProcessBuilder(command)
                .directory(ngrokBinary.getParent().toFile());

        if (log.isDebugEnabled()) {
            processBuilder.inheritIO();
        } else {
            processBuilder.redirectError(ProcessBuilder.Redirect.DISCARD.file())
                    .redirectOutput(ProcessBuilder.Redirect.DISCARD.file());

        }

        try {
            ngrokProcess = processBuilder.start();
            try {
                Thread.sleep(2_000);
            } catch (InterruptedException ignored) {
            }
            if (!ngrokProcess.isAlive()) {
                log.warn("Unable to start ngrok using: '" + String.join(" ", command) + "'");
                return false;
            }
            return true;
        } catch (IOException e) {
            log.warn("Unable to start ngrok process", e);
            return false;
        }
    }

    private Path toNgrokBinaryPath(Path ngrokDir, OS os) {
        return ngrokDir.resolve(determineBinaryName(os));
    }
}