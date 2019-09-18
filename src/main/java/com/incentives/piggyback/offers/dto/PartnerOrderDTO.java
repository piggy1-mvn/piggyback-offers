package com.incentives.piggyback.offers.dto;

import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class PartnerOrderDTO {

	private String orderId;
	private String partnerId;
	private String orderType;
	private String orderStatus;
	private List<String> interestCategories;
	private double optimizationDuration;
	private Location orderLocation;
	private int maxOptimizations;
	private double optimizationRadius;
	private Long initiatorUserId;
	private Date createdDate;
	private Date lastModifiedDate;
	private Integer isActive;
	private String vendorDisplayName;
	private String vendorRedirectUrl;
}