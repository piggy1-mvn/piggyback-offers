package com.incentives.piggyback.offers.entity;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document(collection = "offer")
@Data
public class OfferEntity {

	@Id
	private Long offerId;
	private Long orderId;
	private Long partnerId;
	private String offerCode;
	private Integer offerQuota;
	private String offerStatus;
	private String benefit;
	private String offerDescription;
	private Date createdDate;
	private Date expiryDate;
}