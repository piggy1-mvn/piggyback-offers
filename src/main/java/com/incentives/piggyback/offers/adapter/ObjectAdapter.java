package com.incentives.piggyback.offers.adapter;

import java.util.Calendar;
import java.util.UUID;

import com.incentives.piggyback.offers.dto.OfferDTO;
import com.incentives.piggyback.offers.entity.OfferEntity;
import com.incentives.piggyback.offers.utils.constants.OfferStatus;

public class ObjectAdapter {

	public static OfferEntity getOfferEntity(OfferDTO offerDTO) {
		OfferEntity offerEntity = new OfferEntity();
		offerEntity.setOfferId(UUID.randomUUID().getMostSignificantBits());
		offerEntity.setBenefit(offerDTO.getBenefit());
		offerEntity.setOrderId(offerDTO.getOrderId());
		offerEntity.setPartnerId(offerDTO.getPartnerId());
		offerEntity.setOfferCode(offerDTO.getOfferCode());
		offerEntity.setOfferStatus(OfferStatus.ACTIVE.name());
		offerEntity.setBenefit(offerDTO.getBenefit());
		offerEntity.setOfferDescription(offerDTO.getOfferDescription());
		offerEntity.setCreatedDate(offerDTO.getCreatedDate());
		offerEntity.setExpiryDate(offerDTO.getExpiryDate());
		return offerEntity;
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