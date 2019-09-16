package com.incentives.piggyback.offers.dto;

import java.util.Date;

import lombok.Data;

@Data
public class LocationDTO {

	private String locationId;
	private Long userId;
	private double[] location;
	private double gpsAccuracy;
	private String deviceId;
	private Date createdDate;
	private Date lastModifiedDate;
}