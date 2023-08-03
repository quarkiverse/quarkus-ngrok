package io.quarkiverse.ngrok.deployment;

import io.quarkiverse.ngrok.runtime.devui.NgrokJsonRPCService;
import io.quarkus.deployment.IsDevelopment;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.devui.spi.JsonRPCProvidersBuildItem;
import io.quarkus.devui.spi.page.CardPageBuildItem;
import io.quarkus.devui.spi.page.Page;

public class NgrokProcessor {

    @BuildStep(onlyIf = IsDevelopment.class)
    public CardPageBuildItem devUiCard() {

        CardPageBuildItem cardPageBuildItem = new CardPageBuildItem();

        cardPageBuildItem.addPage(Page.externalPageBuilder("Public URL")
                .dynamicUrlJsonRPCMethodName("getPublicUrl")
                .doNotEmbed()
                .icon("font-awesome-solid:bullhorn"));

        cardPageBuildItem.addPage(Page.externalPageBuilder("Web interface")
                .dynamicUrlJsonRPCMethodName("getWebInterfaceUrl")
                .doNotEmbed()
                .icon("font-awesome-solid:globe"));

        return cardPageBuildItem;
    }

    @BuildStep(onlyIf = IsDevelopment.class)
    JsonRPCProvidersBuildItem createJsonRPCServiceForCache() {
        return new JsonRPCProvidersBuildItem(NgrokJsonRPCService.class);
    }
}
