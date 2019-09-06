package com.incentives.piggyback.offers.controller;

import com.incentives.piggyback.offers.publisher.OffersEventPublisher;
import com.incentives.piggyback.offers.utils.CommonUtility;
import com.incentives.piggyback.offers.utils.constants.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Calendar;

@RestController
public class OffersController {

    @Autowired
    private OffersEventPublisher.PubsubOutboundGateway messagingGateway;

    @PostMapping("/offers")
    public void publishMessage(@RequestParam("message") String message) {
        //PUSHING MESSAGES TO GCP
        messagingGateway.sendToPubsub(
                CommonUtility.stringifyEventForPublish(
                        message,
                        Constant.OFFER_CREATED_EVENT,
                        Calendar.getInstance().getTime().toString(),
                        "",
                        Constant.OFFER_SOURCE_ID
                ));
    }
}
