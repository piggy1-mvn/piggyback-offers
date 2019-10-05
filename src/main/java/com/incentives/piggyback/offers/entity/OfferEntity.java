package com.incentives.piggyback.offers.entity;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.incentives.piggyback.offers.dto.Location;

import lombok.Data;

@Document(collection = "offer")
@Data
public class OfferEntity {

	@Id
	private Long offerId;
	private String orderId;
	private String partnerId;
	private String partnerName;
	private String partnerAppUrl;
	private String partnerWebHookAddress;
	private String offerCode;
	private Location orderLocation;
	private String orderType;
	private Long initiatorUserId;
	private Integer maxOptimizations;
	private String offerStatus;
	private String offerDescription;
	private Date createdDate;
	private Date lastModifiedDate;
	private Date expiryDate;
	private Integer optimizationRadius;
}