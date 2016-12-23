package com.poc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.poc.model.Customer;

@EnableCircuitBreaker
@EnableDiscoveryClient
@SpringBootApplication
public class CustomerControllerApplication {

	public static void main(String[] args) {
		SpringApplication.run(CustomerControllerApplication.class, args);
	}
	
	@Bean
	@LoadBalanced
	public RestTemplate restTemplate(){
		return new RestTemplate();
	}
}


@RestController
class CustomerController{
	
	private final static String getinfo = "http://customer-service-config/customer/getinfo";
	private final static String postinfo = "http://customer-service-config/customer/postinfo";
	
	/**
	 * this is the fallback method for hystrix
	 * @return
	 */
	public Customer fallbackCustomerService(){
		return new Customer();
	}
	
	public Customer fallbackCustomerService(String name,String address){
		return new Customer("","");
	}
	
	@Autowired
	RestTemplate rt;
	
	@HystrixCommand(fallbackMethod="fallbackCustomerService")
	@RequestMapping(value = "/getcustinfo" ,method = RequestMethod.GET)
	public Customer getCustomer(){
		return rt.getForObject(getinfo, Customer.class);
	}
	
	@HystrixCommand(fallbackMethod="fallbackCustomerService")
	@RequestMapping(value = "/postcustinfo" ,method = RequestMethod.GET)
	public Customer postCustomer(
			@RequestParam String name,
			@RequestParam String address
			){
		return rt.postForObject(postinfo, new Customer(name,address), Customer.class);
	}
}