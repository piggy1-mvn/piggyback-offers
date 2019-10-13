package com.incentives.piggyback.offers.serviceImpl;

import java.util.Calendar;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
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
import com.incentives.piggyback.offers.dto.JwtResponse;
import com.incentives.piggyback.offers.dto.PartnerOrderDTO;
import com.incentives.piggyback.offers.dto.UserCredential;
import com.incentives.piggyback.offers.dto.UserData;
import com.incentives.piggyback.offers.dto.WebhookResponse;
import com.incentives.piggyback.offers.entity.OfferEntity;
import com.incentives.piggyback.offers.exception.InvalidRequestException;
import com.incentives.piggyback.offers.publisher.KafkaMessageProducer;
import com.incentives.piggyback.offers.repository.OfferRepository;
import com.incentives.piggyback.offers.service.OfferService;
import com.incentives.piggyback.offers.utils.CommonUtility;
import com.incentives.piggyback.offers.utils.constants.Constant;
import com.incentives.piggyback.offers.utils.constants.OfferStatus;

@Service
public class OfferServiceImpl implements OfferService {

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private Environment env;

	@Autowired
	private OfferRepository offerRepository;

	private final KafkaMessageProducer kafkaMessageProducer;

	Gson gson = new Gson();

	public OfferServiceImpl(KafkaMessageProducer kafkaMessageProducer) {
		this.kafkaMessageProducer = kafkaMessageProducer;
	}

	private static final Logger log = LoggerFactory.getLogger(OfferServiceImpl.class);

	@Override
	public OfferEntity offerForPartnerOrder(PartnerOrderDTO partnerOrderDTO) {
		log.info("offerForPartnerOrder partnerOrderDTO {}", partnerOrderDTO);
		OfferEntity offerEntity = offerRepository.save(ObjectAdapter.generateOfferEntity(partnerOrderDTO));
		publishOffer(offerEntity, Constant.OFFER_CREATED_EVENT);
		sendWebhookToPartner(offerEntity);
		try {
			List<Long> userIdsList = getNearbyUsers(offerEntity.getInitiatorUserId(), offerEntity.getOrderLocation().getLatitude(),
					offerEntity.getOrderLocation().getLongitude(), offerEntity.getOptimizationRadius());
			log.info("offerForPartnerOrder userid list {}", userIdsList);
			List<UserData> usersDataList = getUsersWithInterest(userIdsList, partnerOrderDTO.getOrderType());
			log.info("offerForPartnerOrder usersDataList {}", usersDataList);
			sendNotification(ObjectAdapter.generateBroadCastRequest(usersDataList, offerEntity));
		} catch (Exception e) {
			log.error("offer for partner order failed as {}", e);
		}
		return offerEntity;
	}


	@Override
	public void updateOfferStatus(PartnerOrderDTO partnerOrderData) {
		List<OfferEntity> offerList = offerRepository.findByOrderId(partnerOrderData.getOrderId());
		if (!CommonUtility.isValidList(offerList)) {
			log.info("There is no offer available for this id so closing the message");
			return;
		}
		OfferEntity offer = offerList.get(0);
		offerRepository.save(ObjectAdapter.updateOfferEntity(offer, partnerOrderData));
		if (offer.getOfferStatus().equals(OfferStatus.INACTIVE.name())) {
			publishOffer(offer, Constant.OFFER_DEACTIVATED_EVENT);
		} else {
			publishOffer(offer, Constant.OFFER_UPDATED_EVENT);
		}
		sendWebhookToPartner(offer);
	}

	private HttpStatus sendWebhookToPartner(OfferEntity offer) {
		String url = env.getProperty("notification.api.webhook");
		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
				.queryParam("webhookurl", offer.getPartnerWebHookAddress());
		HttpEntity<?> entity = new HttpEntity<>(offer, headers);
		ResponseEntity<WebhookResponse> response =
				restTemplate.exchange(builder.toUriString(), HttpMethod.POST,
						entity, WebhookResponse.class);
		response.getStatusCode();
		return response.getStatusCode();
	}

	@Override
	public List<Long> getNearbyUsers(Long userId, double latitude, 
			double longitude, double optimizedRadius) {
		String url = env.getProperty("location.api.fetch.nearby.users");
		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
				.queryParam("userId", userId)
				.queryParam("latitude", latitude)
				.queryParam("longitude", longitude)
				.queryParam("optimizedRadius", optimizedRadius);
		HttpEntity<?> entity = new HttpEntity<>(headers);
		ResponseEntity<GetUsersResponse> response = 
				restTemplate.exchange(builder.toUriString(), HttpMethod.GET, 
						entity, GetUsersResponse.class);
		if (CommonUtility.isNullObject(response.getBody()) ||
				!CommonUtility.isValidList(response.getBody().getData()))
			throw new InvalidRequestException("No nearby users present!");
		return response.getBody().getData();
	}

	@Override
	public String sendNotification(BroadcastRequest broadcastRequest) {
		log.info("sendNotification broadcast data {}", broadcastRequest);
		String url = env.getProperty("notification.api.broadcast");
		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
		HttpEntity<?> entity = new HttpEntity<>(broadcastRequest, headers);
		ResponseEntity<BroadcastResponse> response = 
				restTemplate.exchange(url, HttpMethod.POST, 
						entity, BroadcastResponse.class);
		if (CommonUtility.isNullObject(response.getBody()) ||
				!CommonUtility.isValidString(response.getBody().getData()))
			throw new InvalidRequestException("Broadcast of notifications failed");
		return response.getBody().getData();
	}

	@Override
	public List<UserData> getUsersWithInterest(List<Long> users, String interest) {
		String url = env.getProperty("users.api.usersWithInterest");
		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
		headers.set("Authorization", "Bearer "+ generateLoginToken());
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
				.queryParam("users", StringUtils.join(users, ','))
				.queryParam("interest", interest);
		HttpEntity<?> entity = new HttpEntity<>(headers);
		ResponseEntity<List<UserData>> response = 
				restTemplate.exchange(builder.toUriString(), HttpMethod.GET, 
						entity, new ParameterizedTypeReference<List<UserData>>(){});
		if (CommonUtility.isNullObject(response.getBody()) ||
				!CommonUtility.isValidList(response.getBody()))
			throw new InvalidRequestException("No users with desired interest found!");
		return response.getBody();
	}

	private String generateLoginToken() {
		String url = env.getProperty("user.api.login");
		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
		HttpEntity<?> entity = new HttpEntity<>(setUserCredentials(), headers);
		ResponseEntity<JwtResponse> response = 
				restTemplate.exchange(url, HttpMethod.POST, 
						entity, JwtResponse.class);
		return response.getBody().getJwttoken();
	}


	private UserCredential setUserCredentials() {
		UserCredential userCredential = new UserCredential();
		userCredential.setEmail(env.getProperty("user.login.email"));
		userCredential.setUser_password(env.getProperty("user.login.password"));
		return userCredential;
	}


	private void publishOffer(OfferEntity offer, String status) {
		kafkaMessageProducer.send(
				CommonUtility.stringifyEventForPublish(
						gson.toJson(offer),
						status,
						Calendar.getInstance().getTime().toString(),
						offer.getPartnerId(),
						Constant.OFFER_SOURCE_ID
						));
	}
}