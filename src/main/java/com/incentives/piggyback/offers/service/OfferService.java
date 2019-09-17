package com.incentives.piggyback.offers.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.incentives.piggyback.offers.dto.BroadcastRequest;
import com.incentives.piggyback.offers.dto.OfferDTO;
import com.incentives.piggyback.offers.dto.UserData;
import com.incentives.piggyback.offers.entity.OfferEntity;

public interface OfferService {

	List<String> getNearbyUsers(Long userId, Double latitude, Double longitude);

	String sendNotification(BroadcastRequest broadcastRequest);

	List<UserData> getUsersWithInterest(List<Long> users, List<String> interests);

	ResponseEntity<OfferEntity> offerForPartnerOrder(OfferDTO offer);

	ResponseEntity<OfferDTO> updateOfferStatus(OfferDTO offer);
}