package com.discovery.network;

import org.springframework.data.repository.CrudRepository;

//This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
//CRUD refers Create, Read, Update, Delete

public interface ConfigurationRepository extends CrudRepository<Configuration, Long> {

}