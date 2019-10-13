package com.incentives.piggyback.offers.utils.constants;

public interface Constant {
    String OFFER_PUBLISHER_TOPIC = "offer.publisher.topic";
    String OFFER_SERVICE_SUBSCRIBER = "offer.order.subscriber";
    String OFFER_SOURCE_ID = "5";

    String KAFKA_BOOTSTRAP_ADDRESS = "kafka.bootstrap.address";

    String OFFER_CREATED_EVENT = "Offers Events Created";
    String OFFER_UPDATED_EVENT = "Offers Events Updated";
    String OFFER_DEACTIVATED_EVENT = "Offers Events Deactivated";
    String EMAIL_TITLE = "Here's your exclusive offer code!";
    String ORDER_CREATED_EVENT = "Order Events Created";
    String ORDER_UPDATED_EVENT = "Order Events Updated";
}