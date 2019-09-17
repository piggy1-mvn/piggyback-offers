package com.incentives.piggyback.offers.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.incentives.piggyback.offers.entity.OfferEntity;

public interface OfferRepository extends MongoRepository<OfferEntity, Long>{

}