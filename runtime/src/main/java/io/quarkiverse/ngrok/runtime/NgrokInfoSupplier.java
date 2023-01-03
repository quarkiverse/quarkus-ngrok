package io.quarkiverse.ngrok.runtime;

import java.util.function.Supplier;

public class NgrokInfoSupplier implements Supplier<NgrokInfo> {

    private static String publicURL;
    private static String webInterfaceURL;

    @SuppressWarnings("unused")
    public static void setPublicURL(String publicURL) {
        NgrokInfoSupplier.publicURL = publicURL;
    }

    @SuppressWarnings("unused")
    public static void setWebInterfaceURL(String webInterfaceURL) {
        NgrokInfoSupplier.webInterfaceURL = webInterfaceURL;
    }

    @Override
    public NgrokInfo get() {
        return new NgrokInfo() {
            @Override
            public String getPublicURL() {
                return publicURL;
            }

            @Override
            public String getWebInterfaceURL() {
                return webInterfaceURL;
            }
        };
    }
}
