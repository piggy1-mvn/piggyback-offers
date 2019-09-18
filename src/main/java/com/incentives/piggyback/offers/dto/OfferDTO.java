package com.incentives.piggyback.offers.dto;

import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class OfferDTO {

	private Long offerId;
	private String orderId;
	private String partnerId;
	private String offerCode;
	private Location orderLocation;
	private Long initiatorUserId;
	private double optimizationRadius;
	private List<String> interestCategories;
	private Integer offerQuota;
	private String offerStatus;
	private String benefit;
	private String offerDescription;
	private Date createdDate;
	private Date lastModifiedDate;
	private Date expiryDate;	
}