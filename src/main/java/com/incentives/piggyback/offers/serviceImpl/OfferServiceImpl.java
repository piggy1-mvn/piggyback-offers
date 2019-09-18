package com.incentives.piggyback.offers.serviceImpl;

import java.util.Calendar;
import java.util.List;
import java.util.Optional;

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

import com.google.gson.Gson;
import com.incentives.piggyback.offers.adapter.ObjectAdapter;
import com.incentives.piggyback.offers.dto.BroadcastRequest;
import com.incentives.piggyback.offers.dto.BroadcastResponse;
import com.incentives.piggyback.offers.dto.GetUsersResponse;
import com.incentives.piggyback.offers.dto.OfferDTO;
import com.incentives.piggyback.offers.dto.PartnerOrderDTO;
import com.incentives.piggyback.offers.dto.UserData;
import com.incentives.piggyback.offers.entity.OfferEntity;
import com.incentives.piggyback.offers.exception.InvalidRequestException;
import com.incentives.piggyback.offers.publisher.OffersEventPublisher;
import com.incentives.piggyback.offers.repository.OfferRepository;
import com.incentives.piggyback.offers.service.OfferService;
import com.incentives.piggyback.offers.utils.CommonUtility;
import com.incentives.piggyback.offers.utils.constants.Constant;

@Service
public class OfferServiceImpl implements OfferService {

	@Autowired
	private OffersEventPublisher.PubsubOutboundGateway messagingGateway;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private Environment env;

	@Autowired
	private OfferRepository offerRepository;

	Gson gson = new Gson();


	@Override
	public ResponseEntity<OfferEntity> offerForPartnerOrder(PartnerOrderDTO partnerOrderDTO) {
		OfferEntity offerEntity = offerRepository.save(ObjectAdapter.generateOfferEntity(partnerOrderDTO));
		publishOffer(offerEntity, Constant.OFFER_CREATED_EVENT);
		List<Long> usersList = getNearbyUsers(offerEntity.getInitiatorUserId(), offerEntity.getOrderLocation().getLatitude(),
				offerEntity.getOrderLocation().getLongitude());
		List<UserData> usersDataList = getUsersWithInterest(usersList, partnerOrderDTO.getInterestCategories());
		sendNotification(ObjectAdapter.generateBroadCastRequest(usersDataList, offerEntity));
		return ResponseEntity.ok(offerEntity);
	}

	@Override
	public ResponseEntity<OfferDTO> updateOfferStatus(OfferDTO offer)  {
		Optional<OfferEntity> offerEntity = offerRepository.findById(offer.getOfferId());
		if (!offerEntity.isPresent())
			throw new InvalidRequestException("No offer available for this id");

		offerRepository.save(ObjectAdapter.updateOfferEntity(offerEntity.get(), offer));
		publishOffer(offerEntity.get(), Constant.OFFER_UPDATED_EVENT);
		return ResponseEntity.ok(offer);
	}

	private void publishOffer(OfferEntity offer, String status) {
		messagingGateway.sendToPubsub(
				CommonUtility.stringifyEventForPublish(
						gson.toJson(offer),
						status,
						Calendar.getInstance().getTime().toString(),
						"",
						Constant.OFFER_SOURCE_ID
						));
	}

	@Override
	public List<Long> getNearbyUsers(Long userId, double latitude, 
			double longitude) {
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