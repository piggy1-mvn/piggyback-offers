package com.incentives.piggyback.offers.service;

import com.incentives.piggyback.offers.dto.LocationDTO;
import com.incentives.piggyback.offers.dto.OfferDTO;
import com.incentives.piggyback.offers.exception.InvalidRequestException;

import java.util.List;

import org.springframework.http.ResponseEntity;

public interface OfferService {

    ResponseEntity<OfferDTO> updateOfferStatus(Long id, OfferDTO offers) throws InvalidRequestException;

	List<LocationDTO> getNearbyUsers(Long userId, Double latitude, Double longitude, Integer page);
}