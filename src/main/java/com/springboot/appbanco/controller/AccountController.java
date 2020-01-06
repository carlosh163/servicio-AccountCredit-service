package com.springboot.appbanco.controller;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.appbanco.model.CreditAccount;
import com.springboot.appbanco.model.Client;
import com.springboot.appbanco.service.IAccountService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@RefreshScope
@RestController
//@RequestMapping("api/creditAccount")
public class AccountController {
	
	
	private static Logger log = LoggerFactory.getLogger(AccountController.class);

	@Autowired
	private Environment env;
	@Autowired
	private IAccountService service;

	@Value("${configuracion.texto}")
	private String texto;
	
	@GetMapping("/obtener-config")
	public ResponseEntity<?> obtenerConfig(@Value("${server.port}") String puerto){
		log.info(texto);
		Map<String,String> json =  new HashMap<>();
		json.put("texto", texto);
		json.put("puerto", puerto);
		
		if(env.getActiveProfiles().length > 0 && env.getActiveProfiles()[0].equals("dev")) {
			json.put("auto.nombre", env.getProperty("configuracion.autor.nombre"));
			json.put("auto.email", env.getProperty("configuracion.autor.email"));
		}
		
		 return new ResponseEntity<Map<String,String>>(json,HttpStatus.OK);
	}
	
	
	@GetMapping("/SearchAll")
	public Flux<CreditAccount> findAll(){
		return service.findAll();
	}
	
	@GetMapping("/SearchById/{id}")
	public Mono<CreditAccount> findById(@PathVariable String id){
		
		return service.findById(id);
	}
	//Apertura
	@PostMapping("/CreateAccountCreditNew")
	public Mono<CreditAccount> create(@RequestBody CreditAccount account){
		return service.create(account);
	}
	
	
	@PutMapping("/Edit/{id}")
	public Mono<CreditAccount> update(@RequestBody CreditAccount perso, @PathVariable String id){
		return service.update(perso, id);
	}
	
	@DeleteMapping("/Remove/{id}")
	public Mono<Void> delete(@PathVariable String id){
		return service.delete(id);
	}
	
	@GetMapping("/ListarClientes")
	public Flux<Client> findAllClients(){
		return service.findAllClients();
	}
	
	//Consumo CLIENTE::
	
	@GetMapping("/ListarClientesXNroDocu/{nroDoc}")
	public Mono<Client> findClientNrDocu(@PathVariable String nroDoc){
		return service.findClientByNroDoc(nroDoc);
	}
	
	//
	
	/*@GetMapping("/ListarClientesXNroDocuLocal/{nroDoc}")
	public Flux<CreditAccount> findClientNrDocuL(@PathVariable String nroDoc){
		return service.findClienteByNroDocAccount(nroDoc);
	}*/
	
	
	//Consumo Trans:
	//+cantidad a consumption -balance
	@PutMapping("/updateBalanceAccountByAccountNumberConsumer/{accountNumber}/{quantity}")
	public Mono<CreditAccount> updateBalanceAccountByAccountNumberConsumer(@PathVariable Integer accountNumber,@PathVariable double quantity){
		
		return service.findAccountByNroAccount(accountNumber).flatMap(account ->{
			
			if(quantity>account.getBalance()) {
				System.out.println("No se puede consumir más de su Saldo Actual."+account.getBalance());
				return Mono.empty();
			}else {
				account.setBalance(account.getBalance()-quantity);
				account.setConsumption(account.getConsumption() + quantity);
				return service.save(account);
			}
			
			
			
		});
		
	}
	//-cantidad a consumption +balance
	@PutMapping("/updateBalanceAccounByAccountNumberPayment/{accountNumber}/{quantity}")
	public Mono<CreditAccount> updateBalanceAccounByAccountNumberPayment(@PathVariable Integer accountNumber,@PathVariable double quantity){
		
		return service.findAccountByNroAccount(accountNumber).flatMap(account ->{
			
			
			if(quantity>account.getConsumption()) {
				System.out.println("No se puede pagar más de su Consumo Actual (Deuda)."+account.getConsumption());
				return Mono.empty();
			}else {
				account.setBalance(account.getBalance()+quantity);
				account.setConsumption(account.getConsumption() - quantity);
				return service.save(account);
			}
			
		});
		
	}
	
	
	@GetMapping("/findAccountByNumberAccount/{numAcc}")
	public Mono<CreditAccount> findClientNrDocuL(@PathVariable Integer numAcc){
		return service.findAccountByNroAccount(numAcc);
	}
}
