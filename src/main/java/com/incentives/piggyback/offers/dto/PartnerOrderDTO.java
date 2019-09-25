package com.incentives.piggyback.offers.dto;

import java.util.Date;

import lombok.Data;

@Data
public class PartnerOrderDTO {

	private String orderId;
	private String partnerId;
	private String orderType;
	private String orderStatus;
	private Integer optimizationDuration;
	private Location orderLocation;
	private int maxOptimizations;
	private Long initiatorUserId;
	private Date createdDate;
	private Date lastModifiedDate;
	private Integer isActive;
	private String partnerDisplayName;
	private String partnerRedirectUrl;
	private Integer optimizationRadius;
	private String partnerWebHookAddress;
}