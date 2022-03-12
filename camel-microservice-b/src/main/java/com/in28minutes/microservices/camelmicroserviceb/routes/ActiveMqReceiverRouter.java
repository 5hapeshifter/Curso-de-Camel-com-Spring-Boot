package com.in28minutes.microservices.camelmicroserviceb.routes;

import java.math.BigDecimal;

import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.in28minutes.microservices.camelmicroserviceb.CurrencyExchange;

@Component
public class ActiveMqReceiverRouter extends RouteBuilder{

	@Autowired
	MyCurrencyExchangeProcessor myCurrencyExchangeProcessor;
	
	@Autowired
	MyCurrencyExchangeTransformer myCurrencyExchangeTransformer;
	
	@Override
	public void configure() throws Exception {
		
		// JSON
		// CurrencyExchange
		//{  "id":1000, "from": "USD",  "to": "INR",  "conversionMultiple": 70}
		
		// receiver para o arquivo Json
//		from("activemq:my-activemq-queue") // aqui estamos criando um endpoint(referência para uma fila, database ou arquivo)
//		.unmarshal()
//		.json(JsonLibrary.Jackson, CurrencyExchange.class)
//		.bean(myCurrencyExchangeProcessor)
//		.bean(myCurrencyExchangeTransformer)
//		.to("log:received-message-from-active-mq");
		
		// receiver para o arquivo Xml 
//		from("activemq:my-activemq-xml-queue") // aqui estamos criando um endpoint(referência para uma fila, database ou arquivo)
//		.unmarshal()
//		.jacksonxml(CurrencyExchange.class)
//		.to("log:received-message-from-active-mq");
		
		from("activemq:split-queue")
		.to("log:received-message-from-active-mq");
		
	}

}

@Component
class MyCurrencyExchangeProcessor {
	
	Logger logger = LoggerFactory.getLogger(MyCurrencyExchangeProcessor.class);

	public void processMessage(CurrencyExchange currencyExchange){
		
		logger.info("Do some processing with currencyExchange.getConversionMultiple() value wich is {}",
				currencyExchange.getConversionMultiple());
		
	}

}

@Component
class MyCurrencyExchangeTransformer {
	
	Logger logger = LoggerFactory.getLogger(MyCurrencyExchangeProcessor.class);

	public CurrencyExchange processMessage(CurrencyExchange currencyExchange){

		currencyExchange.setConversionMultiple(
				currencyExchange.getConversionMultiple().multiply(BigDecimal.TEN));
		
		return currencyExchange;
	}

}
