package com.incentives.piggyback.offers.service;

import java.util.List;

import com.incentives.piggyback.offers.dto.BroadcastRequest;
import com.incentives.piggyback.offers.dto.PartnerOrderDTO;
import com.incentives.piggyback.offers.dto.UserData;
import com.incentives.piggyback.offers.entity.OfferEntity;

public interface OfferService {

	String sendNotification(BroadcastRequest broadcastRequest);

	OfferEntity offerForPartnerOrder(PartnerOrderDTO partnerOrderDTO);

	void updateOfferStatus(PartnerOrderDTO partnerOrderData);

	List<UserData> getUsersWithInterest(List<Long> users, String interest);

	List<Long> getNearbyUsers(Long userId, double latitude, double longitude, double optimizedRadius);
}