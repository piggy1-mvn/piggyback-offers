package com.incentives.piggyback.offers.dto;

import java.util.Date;

import lombok.Data;

@Data
public class OfferDTO {

	private Long offerId;
	private Long orderId;
	private Long partnerId;
	private String locationId;
	private String offerCode;
	private Date offerValidTill;
	private Date timestamp;
	private String offerStatus;
	private String benefit;
	private String offerDescription;
}
