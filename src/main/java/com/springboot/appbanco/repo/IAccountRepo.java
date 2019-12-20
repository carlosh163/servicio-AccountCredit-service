package com.springboot.appbanco.repo;


import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.springboot.appbanco.model.CreditAccount;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface IAccountRepo extends ReactiveMongoRepository<CreditAccount,String>{

	/*@Query("{'customerList.documentNumber' : ?0}")
	Flux<CreditAccount> findByAccountXDocument(String document);*/
	
	Flux<CreditAccount> findByAccountType(String typeA);
	
	Mono<CreditAccount> findByAccountNumber(Integer nroAccount);
}
