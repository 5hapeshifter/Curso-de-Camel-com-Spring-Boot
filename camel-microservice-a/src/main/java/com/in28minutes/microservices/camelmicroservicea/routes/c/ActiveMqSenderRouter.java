package com.in28minutes.microservices.camelmicroservicea.routes.c;

import org.apache.camel.builder.RouteBuilder;

//@Component
public class ActiveMqSenderRouter extends RouteBuilder{

	@Override
	public void configure() throws Exception {
		// timer - endpoint da origem da mensagem
//		from("timer:active-mq-timer?period=10000")		
//		.transform().constant("My message for Active MQ")
//		.log("${body}")
//		.to("activemq:my-activemq-queue");
		//queue - o endpoint final é do ActiveMQ

		// envio de um Json
//		from("file:files/json")		
//		.log("${body}")
//		.to("activemq:my-activemq-queue");
		
		from("file:files/xml")		
		.log("${body}") // uma das boas práticas é ter logs entre o que estamos fazendo para ver se está ok
		.to("activemq:my-activemq-xml-queue");
		
	}

}
