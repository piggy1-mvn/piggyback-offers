package com.incentives.piggyback.offers.subscriber;

import static com.incentives.piggyback.offers.utils.constants.Constant.OFFER_SERVICE_PARTNER_SUBSCRIBER;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.gcp.pubsub.core.PubSubTemplate;
import org.springframework.cloud.gcp.pubsub.integration.AckMode;
import org.springframework.cloud.gcp.pubsub.integration.inbound.PubSubInboundChannelAdapter;
import org.springframework.cloud.gcp.pubsub.support.GcpPubSubHeaders;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.stereotype.Service;

import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.gson.Gson;
import com.incentives.piggyback.offers.OffersApplication;
import com.incentives.piggyback.offers.dto.PartnerOrderDTO;
import com.incentives.piggyback.offers.service.OfferService;
import com.incentives.piggyback.offers.utils.constants.Constant;

@Service
public class OfferSubscriber {

	private static final Log LOGGER = LogFactory.getLog(OffersApplication.class);

	@Autowired
	private OfferService offerService;
	
	@Autowired
	private Environment env;

	Gson gson = new Gson();

	@Bean
	public MessageChannel pubsubInputChannelForPartner() {
		return new DirectChannel();
	}

	@Bean
	public PubSubInboundChannelAdapter messageChannelAdapterForPartner(
			@Qualifier("pubsubInputChannelForPartnerToOffer") MessageChannel inputChannel, PubSubTemplate pubSubTemplate) {

		PubSubInboundChannelAdapter adapter =
				new PubSubInboundChannelAdapter(pubSubTemplate, env.getProperty(OFFER_SERVICE_PARTNER_SUBSCRIBER));
		adapter.setOutputChannel(inputChannel);
		adapter.setAckMode(AckMode.MANUAL);
		return adapter;
	}

	@Bean
	@ServiceActivator(inputChannel = "pubsubInputChannelForPartnerToOffer")
	public MessageHandler messageReceiverForPartner() {
		return message -> {
			LOGGER.info(env.getProperty(OFFER_SERVICE_PARTNER_SUBSCRIBER) + ": Payload: " + new String((byte[]) message.getPayload()));
			String messagePayload = new String((byte[]) message.getPayload());
			List<String> eventList = Arrays.asList(messagePayload.split(";"));
			PartnerOrderDTO partnerOrderData = gson.fromJson(eventList.get(0), PartnerOrderDTO.class);
			if (eventList.get(1).equalsIgnoreCase(Constant.PARTNER_ORDER_CREATED_EVENT)) {
				offerService.offerForPartnerOrder(partnerOrderData);
			} else if (eventList.get(1).equalsIgnoreCase(Constant.PARTNER_ORDER_UPDATED_EVENT)) {
				offerService.updateOfferStatus(partnerOrderData);
			}
			AckReplyConsumer consumer =
					(AckReplyConsumer) message.getHeaders().get(GcpPubSubHeaders.ACKNOWLEDGEMENT);
			consumer.ack();
		};
	}
}