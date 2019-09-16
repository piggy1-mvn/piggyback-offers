package com.incentives.piggyback.offers.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.incentives.piggyback.offers.dto.OfferDTO;
import com.incentives.piggyback.offers.exception.InvalidRequestException;

public interface OfferService {

    ResponseEntity<OfferDTO> updateOfferStatus(Long id, OfferDTO offers) throws InvalidRequestException;

	List<String> getNearbyUsers(Long userId, Double latitude, Double longitude);

}