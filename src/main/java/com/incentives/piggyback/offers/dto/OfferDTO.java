package com.incentives.piggyback.offers.dto;

import java.util.Calendar;
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


	//to mock the input for testing
	public OfferDTO(){
		this.offerId = 1L;
		this.orderId = 1111L;
		this.partnerId = 11111L;
		this.locationId = "latitue:1.45' longitude:34.4'";
		this.offerCode = "AMAZONFLY";
		this.offerValidTill= Calendar.getInstance().getTime();
		this.timestamp = Calendar.getInstance().getTime();
		this.offerStatus = "ACTIVE";
		this.benefit ="one plus one free";
		this.offerDescription= "Test";

	}
}
