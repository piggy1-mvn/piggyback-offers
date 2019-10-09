package com.incentives.piggyback.offers.dto;

import lombok.Data;

import java.security.interfaces.RSAPublicKey;

@Data
public class PushNotificationRecipient {
    private String deviceId;
    private String user_rsa_key;
}
