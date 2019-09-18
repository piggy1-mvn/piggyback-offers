package com.incentives.piggyback.offers.entity;

import java.util.Date;
import java.util.List;

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
	private String vendorName;
	private String vendorAppUrl;
	private String offerCode;
	private Location orderLocation;
	private List<String> interestCategories;
	private Long initiatorUserId;
	private double optimizationRadius;
	private Integer offerQuota;
	private String offerStatus;
	private String benefit;
	private String offerDescription;
	private Date createdDate;
	private Date lastModifiedDate;
	private Date expiryDate;
}