package com.incentives.piggyback.offers.dto;

public class PushNotificationPayload {

	private String notificationID;
	private String notificationTitle;
	private String notificationMessage;
	
	public String getNotificationID() {
		return notificationID;
	}
	public String getNotificationTitle() {
		return notificationTitle;
	}
	public String getNotificationMessage() {
		return notificationMessage;
	}
	public void setNotificationID(String notificationID) {
		this.notificationID = notificationID;
	}
	public void setNotificationTitle(String notificationTitle) {
		this.notificationTitle = notificationTitle;
	}
	public void setNotificationMessage(String notificationMessage) {
		this.notificationMessage = notificationMessage;
	}
	
	@Override
	public String toString() {
		return "PushNotification [notificationID=" + notificationID + ", notificationTitle=" + notificationTitle
				+ ", notificationMessage=" + notificationMessage + "]";
	}
	
}