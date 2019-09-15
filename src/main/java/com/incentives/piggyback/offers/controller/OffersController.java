package com.incentives.piggyback.offers.controller;

import com.incentives.piggyback.offers.dto.OfferDTO;
import com.incentives.piggyback.offers.service.OfferService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
public class OffersController {

    @Autowired
    private OfferService offerService;


    @PutMapping("/offers/{id}")
    public ResponseEntity<OfferDTO> updateUser(@PathVariable Long id, @RequestBody OfferDTO offers) {
        log.debug("Offer Service: Received PUT request for updating user with userid."+ id);
        return offerService.updateOfferStatus(id,offers);
    }
}
