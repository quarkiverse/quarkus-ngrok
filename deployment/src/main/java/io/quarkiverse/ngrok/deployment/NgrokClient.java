package io.quarkiverse.ngrok.deployment;

public interface NgrokClient {

    boolean isRunning();

    String getPublicURL();

    void close();
}
