package com.discovery.network;

import org.springframework.data.repository.CrudRepository;

import com.discovery.network.Metrics;

//This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
//CRUD refers Create, Read, Update, Delete

public interface MetricsRepository extends CrudRepository<Metrics, Long> {

}