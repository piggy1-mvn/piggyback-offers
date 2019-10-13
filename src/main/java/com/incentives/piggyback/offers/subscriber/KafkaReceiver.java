package com.incentives.piggyback.offers.subscriber;

import com.google.gson.Gson;
import com.incentives.piggyback.offers.dto.PartnerOrderDTO;
import com.incentives.piggyback.offers.service.OfferService;
import com.incentives.piggyback.offers.utils.constants.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class KafkaReceiver {

    private static final Logger LOG = LoggerFactory.getLogger(KafkaReceiver.class);

    Gson gson = new Gson();

    @Autowired
    private OfferService offerService;

    @KafkaListener(topics = {"orderEvents"})
    public void listen(@Payload String message) {
        LOG.info("received message='{}'", message);

        List<String> eventList = Arrays.asList(message.split(";"));
        PartnerOrderDTO partnerOrderData = null;
        try {
            partnerOrderData = gson.fromJson(eventList.get(0), PartnerOrderDTO.class);
            if (eventList.get(1).equalsIgnoreCase(Constant.ORDER_CREATED_EVENT)) {
                offerService.offerForPartnerOrder(partnerOrderData);
            } else if (eventList.get(1).equalsIgnoreCase(Constant.ORDER_UPDATED_EVENT)) {
                offerService.updateOfferStatus(partnerOrderData);
            }
        } catch (Exception e) {
            LOG.error("messageReceiverForOrder: failed error {}", e);
        }
    }

}