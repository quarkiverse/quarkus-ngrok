package io.quarkiverse.ngrok.deployment;

import java.time.Duration;
import java.util.Optional;

import org.jboss.logging.Logger;

import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.RequestOptions;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.core.buffer.Buffer;
import io.vertx.mutiny.core.http.HttpClient;
import io.vertx.mutiny.core.http.HttpClientResponse;

public class VertxNgrokClient implements NgrokClient {

    private static final Logger log = Logger.getLogger(VertxNgrokClient.class);

    private final HttpClient httpClient;
    private final Vertx vertx;

    public VertxNgrokClient(String ngrokHost, int quarkusPort) {
        this.vertx = new Vertx(io.vertx.core.Vertx.vertx());
        this.httpClient = vertx.createHttpClient(new HttpClientOptions().setDefaultHost(ngrokHost).setDefaultPort(quarkusPort));
    }

    @Override
    public boolean isRunning() {
        try {
            HttpClientResponse response = httpClient.request(HttpMethod.GET, "/status").onItem()
                    .transformToUni(req -> req.send())
                    .await()
                    .atMost(Duration.ofSeconds(2));
            return (response.statusCode() == 200);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String getPublicURL() {
        JsonObject jsonResponse = httpClient
                .request(new RequestOptions().setMethod(HttpMethod.GET).setURI("/api/tunnels").addHeader("Content-Type",
                        "application/json"))
                .onItem().transformToUni(req -> req.send())
                .onItem().invoke(res -> {
                    if (res.statusCode() != 200) {
                        throw new RuntimeException("Invalid HTTP response");
                    }
                })
                .onItem().transformToUni(HttpClientResponse::body)
                .onItem().transform(Buffer::toJsonObject)
                .await()
                .atMost(Duration.ofSeconds(5));
        JsonArray tunnels = jsonResponse.getJsonArray("tunnels");
        Optional<String> publicURLEntry = tunnels.stream().filter(o -> o instanceof JsonObject).map(o -> (JsonObject) o)
                .filter(jo -> "http".equals(jo.getString("proto"))).map(jo -> jo.getString("public_url")).findFirst();
        if (publicURLEntry.isEmpty()) {
            throw new RuntimeException("Unable to determine public URL");
        }
        return publicURLEntry.get();
    }

    @Override
    public void close() {
        try {
            httpClient.close().onItem().call(vertx::close).await().atMost(Duration.ofSeconds(2));
        } catch (Exception e) {
            log.warn("Unable to close client", e);
        }
    }
}
