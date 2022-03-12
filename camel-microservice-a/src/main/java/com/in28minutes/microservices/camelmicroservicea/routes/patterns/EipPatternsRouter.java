package com.in28minutes.microservices.camelmicroservicea.routes.patterns;

import java.util.List;
import java.util.Map;

import org.apache.camel.Body;
import org.apache.camel.ExchangeProperties;
import org.apache.camel.Headers;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.in28minutes.microservices.camelmicroservicea.CurrencyExchange;

@Component
public class EipPatternsRouter extends RouteBuilder {
	
	@Autowired
	SplitterComponent splitter;
	
	@Autowired
	DynamicRouterBean dynamicRouterBean;

	@Override
	public void configure() throws Exception {

		//habilitando Tracing - traz mais informações sobre a mensagem e tudo o que está sendo impresso
		getContext().setTracing(true);
		
		//aqui estamos captando as mensagens perdidas ou que ocorreram falhas
		errorHandler(deadLetterChannel("activemq:dead-letter-queue"));
		
		// MULTICAST PATTERN	
//		from("timer:multicast?period=10000")
//		.multicast()//podemos usar varios endpoints com o multicast
//		.to("log:something1", "log:something2", "log:something3");
		
		
//		from("file:files/csv")
//		.unmarshal().csv()
//		.split(body())//aqui estamos separando o arquivo todo por linha
//		.to("log:split-files");
//		//.to("activemq:split-queue"); // essa linha informada no curso da erro
		
		//Message, Message2, Message3
//		from("file:files/csv")
//		.convertBodyTo(String.class)
//		//.split(body(),",")//aqui estamos separando o arquivo todo por linha
//		.split(method(splitter))
//		.to("log:split-files");
		
		// Agrega todas as mensagens com base em um parametro estabelecido
		from("file:files/aggregate-json")
		.unmarshal().json(JsonLibrary.Jackson, CurrencyExchange.class)
		.aggregate(simple("${body.to}"), new ArrayListAggregationStrategy())
		.completionSize(3)
		//.competionTimeout(HIGHEST)
		.to("log:aggregate-json");
		
		//routing slip
		//
		
		//Routing slip - decide dinamicamente qual endpoint usar conforme o que a gente informar, no multicast os endpoints são hardcoded(chumbados)
		
//		String routingSlip = "direct:endpoint1,direct:endpoint3";
		//String routingSlip = "direct:endpoint1,direct:endpoint2,direct:endpoint3";
		
//		from("timer:routingSlip?period=10000")
//		.transform().constant("My message is hardcoded")
//		.routingSlip(simple(routingSlip));
		
		//Dynamic Routing - a rota será escolhida com base na lógica que implementarmos
		// Step 1, Step 2, Step 3
		
		from("timer:dynamicRouting?period={{timePeriod}}")// Podemos configurar tudo o que quisermos e deixar dinâmico com "{{}}" e configurando no "application.properties"
		.transform().constant("My message is hardcoded")
		.dynamicRouter(method(dynamicRouterBean));
		
		from("direct:endpoint1")
		.wireTap("log:wire-tap")
		.to("{{endpoint-for-logging}}");
		
		from("direct:endpoint2")
		.to("log:directendpoint2");
		
		from("direct:endpoint3")
		.to("log:directendpoint3");
		
//		from("timer:multicast?period=10000")
//		.multicast()//podemos usar varios endpoints com o multicast
//		.to("log:something1", "log:something2", "log:something3");
		
	}
}

@Component
class SplitterComponent {
	
	public List<String> splitInput(String body){
		return List.of("ABC", "DEF", "GHI");
	}
}
@Component
class DynamicRouterBean{
	
	Logger logger = LoggerFactory.getLogger(DynamicRouterBean.class);
	
	int invocations;
	
	public String decideTheNextEndpoint(
			@ExchangeProperties Map<String, String> properties,
			@Headers Map<String, String> headers,
			@Body String body) {
		
		logger.info("{} {} {}", properties, headers, body);
		invocations++;
		//Aqui é o que chamamos de rota dinâmica pois é decidido com base na lógica implementada
		if(invocations % 3 == 0)
			return "direct:endpoint1";
		if(invocations % 3 == 1)
			return "direct:endpoint2, direct:endpoint3";

					
		return null;
	}
	
}