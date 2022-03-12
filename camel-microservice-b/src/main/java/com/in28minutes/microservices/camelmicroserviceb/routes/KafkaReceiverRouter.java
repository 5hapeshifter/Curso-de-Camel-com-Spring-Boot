package com.in28minutes.microservices.camelmicroserviceb.routes;

import org.apache.camel.builder.RouteBuilder;

//@Component
public class KafkaReceiverRouter extends RouteBuilder{

	@Override
	public void configure() throws Exception {
		
		from("kafka:myKafkaTopic") // aqui estamos criando um endpoint(referência para uma fila, database ou arquivo)
		.to("log:received-message-from-kafka");
	}

}