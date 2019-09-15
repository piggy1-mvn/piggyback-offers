package com.incentives.piggyback.offers.serviceImpl;

import com.incentives.piggyback.offers.dto.OfferDTO;
import com.incentives.piggyback.offers.exception.InvalidRequestException;
import com.incentives.piggyback.offers.publisher.OffersEventPublisher;
import com.incentives.piggyback.offers.service.OfferService;
import com.incentives.piggyback.offers.utils.CommonUtility;
import com.incentives.piggyback.offers.utils.constants.Constant;
import com.incentives.piggyback.offers.utils.constants.OfferStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Calendar;

@Service
public class OfferServiceImpl implements OfferService {

    @Autowired
    private OffersEventPublisher.PubsubOutboundGateway messagingGateway;

    @Override
    public ResponseEntity<OfferDTO> updateOfferStatus(Long id, OfferDTO offers)  {
            updateOffer(offers);
            //PUSHING MESSAGES TO GCP
            messagingGateway.sendToPubsub(
                    CommonUtility.stringifyEventForPublish(
                            offers.toString(),
                            Constant.OFFER_CREATED_EVENT,
                            Calendar.getInstance().getTime().toString(),
                            "",
                            Constant.OFFER_SOURCE_ID
                    ));
            return ResponseEntity.ok(offers);
    }

    private OfferDTO updateOffer(OfferDTO offer) {
       // as these are mandatory fields and should be present for update
        if(offer.getOfferId()!=null && offer.getPartnerId()!=null && offer.getOfferCode()!=null){

                if (OfferStatus.getAllStatus().contains(offer.getOfferStatus()))
                    offer.setOfferStatus(offer.getOfferStatus());
                else
                    throw new InvalidRequestException("Invalid Status");

                //date need to be updated to activate the offer
                if(null!=offer.getOfferValidTill())
                    offer.setOfferValidTill(offer.getOfferValidTill());
                else
                    throw new InvalidRequestException("Offer Validity date is not passed");

            return offer;
        }else
        {
            throw new InvalidRequestException("Offer Id or Order Id or OfferCode cannot be null");
        }
    }
}
