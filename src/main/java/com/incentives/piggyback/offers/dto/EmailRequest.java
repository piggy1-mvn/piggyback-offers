package com.incentives.piggyback.offers.dto;

import com.incentives.piggyback.offers.utils.constants.Constant;

import lombok.Data;

@Data
public class EmailRequest {

	private String emailId;
	private String vendorDisplayName;
	private String couponCode;
	private String subject = Constant.EMAIL_TITLE;
	private String redirectUrl;
}