package com.incentives.piggyback.offers.serviceImpl;

import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.incentives.piggyback.offers.dto.BroadcastRequest;
import com.incentives.piggyback.offers.dto.BroadcastResponse;
import com.incentives.piggyback.offers.dto.GetUsersResponse;
import com.incentives.piggyback.offers.dto.OfferDTO;
import com.incentives.piggyback.offers.dto.UserData;
import com.incentives.piggyback.offers.exception.InvalidRequestException;
import com.incentives.piggyback.offers.publisher.OffersEventPublisher;
import com.incentives.piggyback.offers.service.OfferService;
import com.incentives.piggyback.offers.utils.CommonUtility;
import com.incentives.piggyback.offers.utils.constants.Constant;
import com.incentives.piggyback.offers.utils.constants.OfferStatus;

@Service
public class OfferServiceImpl implements OfferService {

	@Autowired
	private OffersEventPublisher.PubsubOutboundGateway messagingGateway;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private Environment env;

	@Override
	public ResponseEntity<OfferDTO> updateOfferStatus(Long id, OfferDTO offers)  {
		updateOffer(offers);
		//PUSHING MESSAGES TO GCP
		messagingGateway.sendToPubsub(
				CommonUtility.stringifyEventForPublish(
						offers.toString(),
						Constant.OFFER_CREATED_EVENT,
						Calendar.getInstance().getTime().toString(),
						"",
						Constant.OFFER_SOURCE_ID
						));
		return ResponseEntity.ok(offers);
	}

	private OfferDTO updateOffer(OfferDTO offer) {
		// as these are mandatory fields and should be present for update
		if(offer.getOfferId()!=null && offer.getPartnerId()!=null && offer.getOfferCode()!=null){

			if (OfferStatus.getAllStatus().contains(offer.getOfferStatus()))
				offer.setOfferStatus(offer.getOfferStatus());
			else
				throw new InvalidRequestException("Invalid Status");

			//date need to be updated to activate the offer
			if(null!=offer.getOfferValidTill())
				offer.setOfferValidTill(offer.getOfferValidTill());
			else
				throw new InvalidRequestException("Offer Validity date is not passed");

			return offer;
		} else {
			throw new InvalidRequestException("Offer Id or Order Id or OfferCode cannot be null");
		}
	}

	@Override
	public List<String> getNearbyUsers(Long userId, Double latitude, 
			Double longitude) {
		String url = env.getProperty("location.api.fetch.nearby.users");
		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
				.queryParam("userId", userId)
				.queryParam("latitude", latitude)
				.queryParam("longitude", longitude);
		HttpEntity<?> entity = new HttpEntity<>(headers);
		ResponseEntity<GetUsersResponse> response = 
				restTemplate.exchange(builder.toUriString(), HttpMethod.GET, 
						entity, GetUsersResponse.class);
		if (CommonUtility.isNullObject(response.getBody()) ||
				CommonUtility.isValidList(response.getBody().getData()))
			throw new InvalidRequestException("No nearby users present!");
		return response.getBody().getData();
	}

	@Override
	public String sendNotification(BroadcastRequest broadcastRequest) {
		String url = env.getProperty("notification.api.broadcast");
		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
		HttpEntity<?> entity = new HttpEntity<>(broadcastRequest, headers);
		ResponseEntity<BroadcastResponse> response = 
				restTemplate.exchange(url, HttpMethod.POST, 
						entity, BroadcastResponse.class);
		if (CommonUtility.isNullObject(response.getBody()) ||
				CommonUtility.isValidString(response.getBody().getData()))
			throw new InvalidRequestException("Broadcast of notifications failed");
		return response.getBody().getData();
	}

	@Override
	public List<UserData> getUsersWithInterest(List<Long> users, List<String> interests) {
		String url = env.getProperty("users.api.usersWithInterest");
		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
				.queryParam("users", users)
				.queryParam("interest", interests);
		HttpEntity<?> entity = new HttpEntity<>(headers);
		ResponseEntity<List<UserData>> response = 
				restTemplate.exchange(builder.toUriString(), HttpMethod.GET, 
						entity, new ParameterizedTypeReference<List<UserData>>(){});
		if (CommonUtility.isNullObject(response.getBody()) ||
				CommonUtility.isValidList(response.getBody()))
			throw new InvalidRequestException("No users with desired interest found!");
		return response.getBody();
	}

}