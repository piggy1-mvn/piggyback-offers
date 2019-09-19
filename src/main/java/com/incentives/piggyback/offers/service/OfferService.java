package com.incentives.piggyback.offers.service;

import java.util.List;

import com.incentives.piggyback.offers.dto.BroadcastRequest;
import com.incentives.piggyback.offers.dto.PartnerOrderDTO;
import com.incentives.piggyback.offers.dto.UserData;

public interface OfferService {

	List<Long> getNearbyUsers(Long userId, double latitude, double longitude);

	String sendNotification(BroadcastRequest broadcastRequest);

	void offerForPartnerOrder(PartnerOrderDTO partnerOrderDTO);

	void updateOfferStatus(PartnerOrderDTO partnerOrderData);

	List<UserData> getUsersWithInterest(List<Long> users, String interest);
}