package com.incentives.piggyback.offers.subscriber;

import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.incentives.piggyback.offers.OffersApplication;
import com.incentives.piggyback.offers.service.OfferService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.gcp.pubsub.core.PubSubTemplate;
import org.springframework.cloud.gcp.pubsub.integration.AckMode;
import org.springframework.cloud.gcp.pubsub.integration.inbound.PubSubInboundChannelAdapter;
import org.springframework.cloud.gcp.pubsub.support.GcpPubSubHeaders;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.stereotype.Service;

import static com.incentives.piggyback.offers.utils.constants.Constant.OFFER_SERVICE_PARTNER_SUBSCRIBER;

@Service
public class OfferSubscriber {

    private static final Log LOGGER = LogFactory.getLog(OffersApplication.class);

    @Autowired
    private OfferService offerService;

    @Bean
    public MessageChannel pubsubInputChannelForPartner() {
        return new DirectChannel();
    }

    @Bean
    public PubSubInboundChannelAdapter messageChannelAdapterForPartner(
            @Qualifier("pubsubInputChannelForPartnerToOffer") MessageChannel inputChannel, PubSubTemplate pubSubTemplate) {

        PubSubInboundChannelAdapter adapter =
                new PubSubInboundChannelAdapter(pubSubTemplate, OFFER_SERVICE_PARTNER_SUBSCRIBER);
        adapter.setOutputChannel(inputChannel);
        adapter.setAckMode(AckMode.MANUAL);
        return adapter;
    }

    @Bean
    @ServiceActivator(inputChannel = "pubsubInputChannelForPartnerToOffer")
    public MessageHandler messageReceiverForPartner() {
        return message -> {
            LOGGER.info(OFFER_SERVICE_PARTNER_SUBSCRIBER + ": Payload: " + new String((byte[]) message.getPayload()));
            AckReplyConsumer consumer =
                    (AckReplyConsumer) message.getHeaders().get(GcpPubSubHeaders.ACKNOWLEDGEMENT);
            consumer.ack();
        };
    }

}