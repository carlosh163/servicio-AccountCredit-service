package com.springboot.appbanco.service;

import com.springboot.appbanco.model.CreditAccount;
import com.springboot.appbanco.model.Client;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IAccountService {

	public Flux<CreditAccount> findAll();

	public Mono<CreditAccount> findById(String id);

	public Mono<CreditAccount> create(CreditAccount account);

	public Mono<CreditAccount> update(CreditAccount account, String id);

	public Mono<Void> delete(String id);
	
	//Metodos para Client:
	public Flux<Client> findAllClients();
	public Mono<Client> findByIdClient(String id);
	public Mono<Client> createClient(Client client);
	public Mono<Client> updateClient(Client client, String id);
	public Mono<Void> deleteClient(String id);
	
	
	public Mono<Client> findClientByNroDoc(String nroDoc); //Consumo A cliente.
	//public Flux<CreditAccount> findClienteByNroDocAccount(String nroDoc); //Consulta al documento Account
	
	public Mono<CreditAccount> findAccountByNroAccount (Integer accNumber);
	
	public Mono<CreditAccount> save(CreditAccount account);
}
