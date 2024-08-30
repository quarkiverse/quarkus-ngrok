package io.quarkiverse.ngrok.runtime;

import java.util.Optional;

import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;

/**
 * Configuration related to <a href="https://ngrok.com">ngrok</a>.
 *
 * These configuration values only apply to dev-mode.
 */
@ConfigMapping(prefix = "quarkus.ngrok")
@ConfigRoot(phase = ConfigPhase.RUN_TIME)
public interface NgrokConfig {

    /**
     * Whether ngrok should be started when Quarkus dev-mode is launched.
     */
    @WithDefault("true")
    boolean enabled();

    /**
     * The directory into which to save the ngrok binary if it doesn't exist.
     */
    @WithDefault("${user.home}/.quarkus/ngrok")
    String directory();

    /**
     * Specify a download URL where the ngrok distribution will be obtained from.
     *
     * If not set, the platform default will be used.
     */
    Optional<String> downloadURL();

    /**
     * The authentication token used to authenticate this client when it connects to the ngrok.com service.
     */
    Optional<String> authToken();

    /**
     * The region where the ngrok agent will connect to host its tunnels.
     */
    Optional<Region> region();

    /**
     * The port where ngrok will be serving the local web interface and API.
     */
    Optional<Integer> port();

    /**
     * The domain or hostname to use instead of the one ngrok generates.
     * See https://ngrok.com/blog-post/free-static-domains-ngrok-users
     */
    Optional<String> domain();

    /**
     * Name of the tunnel to start from ngrok default config instead of Quarkus configuration. Useful to use extended ngrok
     * configuration.
     */
    Optional<String> tunnelName();

    /**
     * API key for ngrok if you need to perform any API operations such as delete-certificate-management-policy.
     */
    Optional<String> apiKey();

    /**
     * Reserved domain unique identifier for revoking certificate upon startup. Example: 'rd_2hrGw0rqFLOm9pcXf4dlbdNfrus'
     * If this value is not null the certificate will be revoked upon dev service startup.
     */
    Optional<String> deleteCertificateDomainId();

    enum Region {
        United_States("us"),
        Europe("eu"),
        Asia_Pacific("ap"),
        Australia("au"),
        South_America("sa"),
        Japan("jp"),
        India("in");

        private final String name;

        Region(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
