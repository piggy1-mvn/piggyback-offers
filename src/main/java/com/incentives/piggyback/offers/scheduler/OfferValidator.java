package com.incentives.piggyback.offers.scheduler;

import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.google.gson.Gson;
import com.incentives.piggyback.offers.dto.WebhookResponse;
import com.incentives.piggyback.offers.entity.OfferEntity;
import com.incentives.piggyback.offers.publisher.KafkaMessageProducer;
import com.incentives.piggyback.offers.repository.OfferRepository;
import com.incentives.piggyback.offers.utils.CommonUtility;
import com.incentives.piggyback.offers.utils.constants.Constant;
import com.incentives.piggyback.offers.utils.constants.OfferStatus;

@Component
public class OfferValidator {

	@Autowired
	private OfferRepository offerRepository;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private Environment env;

	private final KafkaMessageProducer kafkaMessageProducer;

	Gson gson = new Gson();

	public OfferValidator(KafkaMessageProducer kafkaMessageProducer) {
		this.kafkaMessageProducer = kafkaMessageProducer;
	}


	@Scheduled(fixedRate = 600000)
	public void validateOffer() {
		List<OfferEntity> offersList = offerRepository.findByOfferStatus(OfferStatus.ACTIVE.name());
		if (CommonUtility.isValidList(offersList)) {
			for (OfferEntity offer : offersList) {
				if (offer.getExpiryDate().before(Calendar.getInstance().getTime())
						|| offer.getMaxOptimizations() < 1) {
					offer.setOfferStatus(OfferStatus.INACTIVE.name());
					offerRepository.save(offer);
					sendWebhookToPartner(offer);
					publishOffer(offer, Constant.OFFER_DEACTIVATED_EVENT);
				}
			}
		}
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