package com.incentives.piggyback.offers.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.incentives.piggyback.offers.entity.OfferEntity;

public interface OfferRepository extends MongoRepository<OfferEntity, Long>{

	List<OfferEntity> findByOrderId(String orderId);
	List<OfferEntity> findByOfferStatus(String offerStatus);
}