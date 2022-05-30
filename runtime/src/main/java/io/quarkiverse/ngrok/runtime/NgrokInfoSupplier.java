package io.quarkiverse.ngrok.runtime;

import java.util.function.Supplier;

public class NgrokInfoSupplier implements Supplier<NgrokInfo> {

    private static String publicURL;

    @SuppressWarnings("unused")
    public static void setPublicURL(String publicURL) {
        NgrokInfoSupplier.publicURL = publicURL;
    }

    @Override
    public NgrokInfo get() {
        return new NgrokInfo() {
            @Override
            public String getPublicURL() {
                return publicURL;
            }
        };
    }
}
