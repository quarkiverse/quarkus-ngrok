package io.quarkiverse.ngrok.runtime.devui;

public class NgrokJsonRPCService {
    private NgrokInfoSupplier ngrokInfoSupplier = new NgrokInfoSupplier();

    public String getPublicUrl() {
        return ngrokInfoSupplier.get().getPublicURL();
    }

    public String getWebInterfaceUrl() {
        return ngrokInfoSupplier.get().getWebInterfaceURL();
    }
}
