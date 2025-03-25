package io.quarkiverse.ngrok.deployment.devui;

import io.quarkiverse.ngrok.runtime.devui.NgrokJsonRPCService;
import io.quarkus.deployment.IsDevelopment;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.devui.spi.JsonRPCProvidersBuildItem;
import io.quarkus.devui.spi.page.CardPageBuildItem;
import io.quarkus.devui.spi.page.Page;

public class NgrokDevUIProcessor {

    @BuildStep(onlyIf = IsDevelopment.class)
    public void devUiCard(BuildProducer<CardPageBuildItem> cardPageBuildItemBuildProducer) {

        CardPageBuildItem card = new CardPageBuildItem();

        card.addPage(Page.externalPageBuilder("Public URL")
                .dynamicUrlJsonRPCMethodName("getPublicUrl")
                .doNotEmbed()
                .icon("font-awesome-solid:bullhorn"));

        card.addPage(Page.externalPageBuilder("Web interface")
                .dynamicUrlJsonRPCMethodName("getWebInterfaceUrl")
                .doNotEmbed()
                .icon("font-awesome-solid:globe"));

        card.setCustomCard("qwc-ngrok-card.js");
        cardPageBuildItemBuildProducer.produce(card);
    }

    @BuildStep
    JsonRPCProvidersBuildItem createJsonRPCServiceForCache() {
        return new JsonRPCProvidersBuildItem(NgrokJsonRPCService.class);
    }
}