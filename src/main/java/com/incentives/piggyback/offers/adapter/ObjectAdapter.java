package com.incentives.piggyback.offers.adapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.incentives.piggyback.offers.dto.BroadcastRequest;
import com.incentives.piggyback.offers.dto.EmailRequest;
import com.incentives.piggyback.offers.dto.OfferDTO;
import com.incentives.piggyback.offers.dto.PartnerOrderDTO;
import com.incentives.piggyback.offers.dto.PushNotificationPayload;
import com.incentives.piggyback.offers.dto.PushNotificationRequest;
import com.incentives.piggyback.offers.dto.UserData;
import com.incentives.piggyback.offers.entity.OfferEntity;
import com.incentives.piggyback.offers.utils.constants.Constant;
import com.incentives.piggyback.offers.utils.constants.OfferStatus;

public class ObjectAdapter {

	public static OfferEntity generateOfferEntity(PartnerOrderDTO partnerOrderDTO) {
		OfferEntity offerEntity = new OfferEntity();
		offerEntity.setOfferId(UUID.randomUUID().getMostSignificantBits());
		offerEntity.setOfferCode(generateOfferCode());
		offerEntity.setOrderId(partnerOrderDTO.getOrderId());
		offerEntity.setOrderLocation(partnerOrderDTO.getOrderLocation());
		offerEntity.setInitiatorUserId(partnerOrderDTO.getInitiatorUserId());
		offerEntity.setPartnerId(partnerOrderDTO.getPartnerId());
		offerEntity.setOptimizationRadius(partnerOrderDTO.getOptimizationRadius());
		offerEntity.setOfferQuota(partnerOrderDTO.getMaxOptimizations());
		offerEntity.setOfferStatus(OfferStatus.ACTIVE.name());
		offerEntity.setVendorAppUrl(partnerOrderDTO.getVendorRedirectUrl());
		offerEntity.setVendorName(partnerOrderDTO.getVendorDisplayName());
		offerEntity.setInterestCategories(partnerOrderDTO.getInterestCategories());
		offerEntity.setOfferDescription(partnerOrderDTO.getOrderType());
		offerEntity.setCreatedDate(Calendar.getInstance().getTime());
		offerEntity.setLastModifiedDate(Calendar.getInstance().getTime());
		offerEntity.setExpiryDate(generateExpiryDate(partnerOrderDTO.getOptimizationDuration()));
		return offerEntity;
	}
	
	public static BroadcastRequest generateBroadCastRequest(List<UserData> users, OfferEntity offer) {
		BroadcastRequest broadcastRequest = new BroadcastRequest();
		List<EmailRequest> emailList = new ArrayList<EmailRequest>();
		List<String> recepients = new ArrayList<String>();
		users.forEach(user -> {
			EmailRequest email = new EmailRequest();
			email.setEmailId(user.getEmail());
			email.setCouponCode(offer.getOfferCode());
			email.setVendorDisplayName(offer.getVendorName());
			email.setRedirectUrl(offer.getVendorAppUrl());
			emailList.add(email);
			recepients.add(user.getDevice_id());
		});
		PushNotificationRequest pushNotificationRequest = new PushNotificationRequest();
		PushNotificationPayload pushNotificationPayload = new PushNotificationPayload();
		pushNotificationPayload.setVoucher_code(offer.getOfferCode());
		pushNotificationPayload.setBody(offer.getOfferDescription());
		pushNotificationPayload.setPartner_url(offer.getVendorAppUrl());
		pushNotificationPayload.setTitle(Constant.EMAIL_TITLE);
		pushNotificationRequest.setRecepients(recepients);
		pushNotificationRequest.setPushNotificationPayload(pushNotificationPayload);
		broadcastRequest.setPushNotificationRequest(pushNotificationRequest);
		broadcastRequest.setEmailRequestList(emailList);
		return broadcastRequest;
	}
	
	
	public static Date generateExpiryDate(double optimizationDuration) {
		Calendar expiryCal = Calendar.getInstance();
		expiryCal.add(Calendar.MILLISECOND, Integer.parseInt(""+optimizationDuration));
		return expiryCal.getTime();
	}
	
	public static String generateOfferCode() {
		return "Pig-inc-"+UUID.randomUUID().toString().subSequence(5, 10);
	}

	public static OfferEntity updateOfferEntity(OfferEntity offerEntity, OfferDTO offer) {
		offerEntity.setBenefit(offer.getBenefit());
		offerEntity.setOrderId(offer.getOrderId());
		offerEntity.setPartnerId(offer.getPartnerId());
		offerEntity.setOfferCode(offer.getOfferCode());
		offerEntity.setExpiryDate(offer.getExpiryDate());
		if (offerEntity.getExpiryDate().before(Calendar.getInstance().getTime())) {
			offerEntity.setOfferStatus(OfferStatus.INACTIVE.name());
		} else {
			offerEntity.setOfferStatus(OfferStatus.ACTIVE.name());
		}
		offerEntity.setBenefit(offer.getBenefit());
		offerEntity.setOfferDescription(offer.getOfferDescription());
		return offerEntity;
	}
}