package com.springboot.appbanco.service;

import static org.springframework.http.MediaType.APPLICATION_JSON;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com.springboot.appbanco.model.CreditAccount;
import com.springboot.appbanco.model.AccountDue;
import com.springboot.appbanco.model.Client;
import com.springboot.appbanco.repo.IAccountRepo;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class AccountServiceImpl implements IAccountService {
	private static Logger log = LoggerFactory.getLogger(AccountServiceImpl.class);

	@Autowired
	@Qualifier("client")
	private WebClient wCClient;
	
	
	@Autowired
	@Qualifier("accountDue")
	private WebClient wCAccountDue;

	@Autowired
	IAccountRepo repo;

	@Override
	public Flux<CreditAccount> findAll() {
		return repo.findAll();
	}

	@Override
	public Mono<CreditAccount> findById(String id) {
		return repo.findById(id);
	}

	// REQ03 : Validacion de Cuenta Unica- Ahorro.,
	@Override
	public Mono<CreditAccount> create(CreditAccount account) {
		System.out.println("CUENTAAA CREDITO");

		Client clie = account.getCustomer();

		return Mono.just(clie).flatMap(objClient -> {

			String numberDoc = objClient.getDocumentNumber();			
			return wCAccountDue.get().uri("/searchByDocNumber/{docNum}", Collections.singletonMap("docNum", numberDoc))
					.retrieve().bodyToFlux(AccountDue.class).count();
		}).flatMap(sum -> {
			
			if(sum> 0) {
				log.info("Debe..");
				return Mono.empty();
			}else {
				log.info("Crear Cuenta..");
				
				return Mono.just(account).flatMap(objC -> {
					// Flux:
					objC.setOpeningDate(new Date());
					objC.setConsumption(0);

					objC.setBalance(account.getCreditLimit());
					
					return wCClient.post().uri("/SaveAccountCredit")
							.accept(APPLICATION_JSON)
							.contentType(APPLICATION_JSON)
							.syncBody(objC)
							.retrieve().bodyToMono(CreditAccount.class);
				}).flatMap(client -> {
							return repo.save(account);
						});
			}
		});

	}

	@Override
	public Mono<CreditAccount> update(CreditAccount account, String id) {
		// TODO Auto-generated method stub
		/*
		 * return repo.findById(id).flatMap(Account ->{
		 * 
		 * Account.setNombres(persoClie.getNombres());
		 * Account.setApellidos(persoClie.getApellidos());
		 * Account.setTipoDocumento(persoClie.getTipoDocumento());
		 * Account.setNroDocumento(persoClie.getNroDocumento());
		 * Account.setEstado(persoClie.getEstado());
		 * //Account.setAccountsList(accountsList); return repo.save(Account); });
		 */
		return Mono.error(new Throwable());
	}

	@Override
	public Mono<Void> delete(String id) {
		// TODO Auto-generated method stub
		return repo.findById(id).flatMap(account -> repo.delete(account));
	}

	// Consumiendo servicios de otro MS.
	@Override
	public Flux<Client> findAllClients() {

		return wCClient.get().uri("/SearchAll").accept(APPLICATION_JSON).exchange()
				.flatMapMany(response -> response.bodyToFlux(Client.class));

	}

	@Override
	public Mono<Client> findByIdClient(String id) {
		// TODO Auto-generated method stub
		Map<String, Object> params = new HashMap<>();
		params.put("id", id);
		return wCClient.get().uri("/SearchById/{id}", params).accept(APPLICATION_JSON)
				// .retrieve()
				// .bodyToMono(Producto.class);
				.exchange().flatMap(response -> response.bodyToMono(Client.class));
	}

	@Override
	public Mono<Client> createClient(Client cliente) {

		return wCClient.post().uri("/Create").accept(APPLICATION_JSON).contentType(APPLICATION_JSON)
				// .body(fromObject(cliente)) - -BodyInserters
				.body(BodyInserters.fromObject(cliente)).retrieve().bodyToMono(Client.class);
				//.syncBody(cliente).retrieve().bodyToMono(Client.class);
	}

	@Override
	public Mono<Client> updateClient(Client cliente, String id) {
		Map<String, Object> params = new HashMap<>();
		params.put("id", id);

		/*
		 * Collections.singletonMap("id",id)
		 */

		return wCClient.put().uri("/Edit/{id}", params).accept(APPLICATION_JSON).contentType(APPLICATION_JSON)
				.syncBody(cliente).retrieve().bodyToMono(Client.class);
	}

	@Override
	public Mono<Void> deleteClient(String id) {

		return wCClient.delete().uri("/Remove/{id}", Collections.singletonMap("id", id)).exchange().then();
	}

	@Override
	public Mono<Client> findClientByNroDoc(String nroDoc) {

		// Map<String, Object> params = new HashMap<>();
		// params.put("id", id);
		return wCClient.get().uri("/BuscarClientePorNroDoc/{nroDoc}", Collections.singletonMap("nroDoc", nroDoc))
				.accept(APPLICATION_JSON).exchange().flatMap(response -> response.bodyToMono(Client.class));
	}

	/*@Override
	public Flux<CreditAccount> findClienteByNroDocAccount(String nroDoc) {

		return repo.findByAccountXDocument(nroDoc);
	}*/

	@Override
	public Mono<CreditAccount> findAccountByNroAccount(Integer accNumber) {

		return repo.findByAccountNumber(accNumber);
	}

	@Override
	public Mono<CreditAccount> save(CreditAccount account) {

		return repo.save(account);
	}

}
