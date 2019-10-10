package com.incentives.piggyback.offers.adapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.incentives.piggyback.offers.dto.*;
import com.incentives.piggyback.offers.entity.OfferEntity;
import com.incentives.piggyback.offers.utils.CommonUtility;
import com.incentives.piggyback.offers.utils.constants.Constant;
import com.incentives.piggyback.offers.utils.constants.OfferStatus;

public class ObjectAdapter {

	public static OfferEntity generateOfferEntity(PartnerOrderDTO partnerOrderDTO) {
		OfferEntity offerEntity = new OfferEntity();
		offerEntity.setOfferId(UUID.randomUUID().getMostSignificantBits());
		offerEntity.setOfferCode(generateOfferCode());
		offerEntity.setOrderId(partnerOrderDTO.getOrderId());
		offerEntity.setOrderType(partnerOrderDTO.getOrderType());
		offerEntity.setOrderLocation(partnerOrderDTO.getOrderLocation());
		offerEntity.setInitiatorUserId(partnerOrderDTO.getInitiatorUserId());
		offerEntity.setPartnerId(partnerOrderDTO.getPartnerId());
		offerEntity.setMaxOptimizations(partnerOrderDTO.getMaxOptimizations());
		offerEntity.setOfferStatus(OfferStatus.ACTIVE.name());
		offerEntity.setPartnerAppUrl(partnerOrderDTO.getPartnerRedirectUrl());
		offerEntity.setPartnerWebHookAddress(partnerOrderDTO.getPartnerWebHookAddress());
		offerEntity.setOptimizationRadius(partnerOrderDTO.getOptimizationRadius());
		offerEntity.setPartnerName(partnerOrderDTO.getPartnerDisplayName());
		offerEntity.setOfferDescription(partnerOrderDTO.getOrderType());
		offerEntity.setCreatedDate(Calendar.getInstance().getTime());
		offerEntity.setLastModifiedDate(Calendar.getInstance().getTime());
		offerEntity.setExpiryDate(generateExpiryDate(partnerOrderDTO.getOptimizationDuration()));
		return offerEntity;
	}

	public static OfferEntity updateOfferEntity(OfferEntity offerEntity, PartnerOrderDTO partnerOrderDTO) {
		if (!CommonUtility.isNullObject(partnerOrderDTO.getOrderLocation()))
			offerEntity.setOrderLocation(partnerOrderDTO.getOrderLocation());
		if (CommonUtility.isValidLong(partnerOrderDTO.getInitiatorUserId()))
			offerEntity.setInitiatorUserId(partnerOrderDTO.getInitiatorUserId());
		if (CommonUtility.isValidString(partnerOrderDTO.getPartnerId()))
			offerEntity.setPartnerId(partnerOrderDTO.getPartnerId());
		offerEntity.setMaxOptimizations(partnerOrderDTO.getMaxOptimizations());
		if (offerEntity.getExpiryDate().before(Calendar.getInstance().getTime())
				|| offerEntity.getMaxOptimizations() < 1) {
			offerEntity.setOfferStatus(OfferStatus.INACTIVE.name());
		} else {
			offerEntity.setOfferStatus(OfferStatus.ACTIVE.name());
		}
		offerEntity.setLastModifiedDate(Calendar.getInstance().getTime());
		return offerEntity;
	}

	public static BroadcastRequest generateBroadCastRequest(List<UserData> users, OfferEntity offer) {
		BroadcastRequest broadcastRequest = new BroadcastRequest();
		List<EmailRequest> emailList = new ArrayList<EmailRequest>();
		List<ReceipientInfo> recepients = new ArrayList<ReceipientInfo>();
		users.forEach(user -> {
			EmailRequest email = new EmailRequest();
			ReceipientInfo receipientInfo = new ReceipientInfo();
			email.setEmailId(user.getEmail());
			email.setCouponCode(offer.getOfferCode());
			email.setVendorDisplayName(offer.getPartnerName());
			email.setRedirectUrl(offer.getPartnerAppUrl());
			emailList.add(email);
			receipientInfo.setDevice_id(user.getDevice_id());
			receipientInfo.setUser_rsa(user.getUser_rsa());
			recepients.add(receipientInfo);
		});
		PushNotificationRequest pushNotificationRequest = new PushNotificationRequest();
		PushNotificationPayload pushNotificationPayload = new PushNotificationPayload();
		pushNotificationPayload.setVoucher_code(offer.getOfferCode());
		pushNotificationPayload.setBody(offer.getOfferDescription());
		pushNotificationPayload.setPartner_url(offer.getPartnerAppUrl());
		pushNotificationPayload.setTitle(Constant.EMAIL_TITLE);
		pushNotificationRequest.setRecepients(recepients);
		pushNotificationRequest.setPushNotificationPayload(pushNotificationPayload);
		broadcastRequest.setPushNotificationRequest(pushNotificationRequest);
		broadcastRequest.setEmailRequestList(emailList);
		return broadcastRequest;
	}


	public static Date generateExpiryDate(Integer optimizationDuration) {
		Calendar expiryCal = Calendar.getInstance();
		expiryCal.add(Calendar.SECOND, optimizationDuration);
		return expiryCal.getTime();
	}

	public static String generateOfferCode() {
		return "PIGGY-INCENTIVES-VOUCHER-CODE"+UUID.randomUUID().toString().subSequence(5, 8);
	}
}