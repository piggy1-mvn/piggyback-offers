package com.incentives.piggyback.offers.dto;

import java.util.List;

import lombok.Data;

@Data
public class BroadcastRequest {

	private PushNotificationRequest pushNotificationRequest;
	private List<EmailRequest> emailRequestList;
}