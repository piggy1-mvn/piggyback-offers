package com.incentives.piggyback.offers.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.incentives.piggyback.offers.dto.OfferDTO;
import com.incentives.piggyback.offers.entity.OfferEntity;
import com.incentives.piggyback.offers.service.OfferService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class OfferController {

    @Autowired
    private OfferService offerService;


    @PutMapping("/offers")
    public ResponseEntity<OfferDTO> updateUser(
    		@RequestBody OfferDTO offer) {
        log.debug("Offer Service: Received PUT request for updating offer");
        return offerService.updateOfferStatus(offer);
    }
    
    @PostMapping("/partner/order")
    public ResponseEntity<OfferEntity> offerForPartnerOrder(@RequestBody OfferDTO offer) {
        log.debug("Offer Service: Received POST request with offer data for partner {}", offer);
        return offerService.offerForPartnerOrder(offer);
    }
}