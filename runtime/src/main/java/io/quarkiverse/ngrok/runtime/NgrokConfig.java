package io.quarkiverse.ngrok.runtime;

import java.util.Optional;

import io.quarkus.runtime.annotations.ConfigItem;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;

/**
 * Configuration related to <a href="https://ngrok.com">ngrok</a>.
 *
 * These configuration values only apply to dev-mode.
 */
@ConfigRoot(phase = ConfigPhase.RUN_TIME)
public class NgrokConfig {

    /**
     * Whether ngrok should be started when Quarkus dev-mode is launched
     */
    @ConfigItem(defaultValue = "true")
    public boolean enabled;

    /**
     * The directory into which to save the ngrok binary if it doesn't exist
     */
    @ConfigItem(defaultValue = "${user.home}/.quarkus/ngrok")
    public String directory;

    /**
     * Specify a download URL where the ngrok distribution will be obtained from.
     *
     * If not set, the platform default will be used
     */
    @ConfigItem
    public Optional<String> downloadURL;

    /**
     * The authentication token used to authenticate this client when it connects to the ngrok.com service
     */
    @ConfigItem
    public Optional<String> authToken;

    /**
     * The region where the ngrok agent will connect to host its tunnels
     */
    @ConfigItem
    public Optional<Region> region;

    /**
     * The port where ngrok will be serving the local web interface and api
     */
    public Optional<Integer> port;

    public enum Region {
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
