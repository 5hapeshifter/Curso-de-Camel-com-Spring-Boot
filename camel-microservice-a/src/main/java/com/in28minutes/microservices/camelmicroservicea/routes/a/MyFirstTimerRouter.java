package com.in28minutes.microservices.camelmicroservicea.routes.a;

import java.time.LocalDateTime;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MyFirstTimerRouter extends RouteBuilder{

	@Autowired // anotação para fazer injeção de dependência
	private GetCurrentTimeBean getCurrentTimeBean;// aqui estamos usando as melhores práticas com injeção de dependências
	
	@Autowired
	private SimpleLoggingProcessingComponent loggingComponent;
	
	@Override
	public void configure() throws Exception {
		// timer - dispara uma mensagem a cada segundo
		// transformation - utilizado para alterar o corpo da msg
		// log
		// Exchange[ExchangePattern: InOnly, BodyType: null, Body: [Body is null]]
		from("timer:first-timer")// queue - GERALMENTE QUANDO PEGAMOS UMA MENSAGEM, FAZEMOS DIVERSAS ALTERAÇÕES PARA TRABALHAR COM ELA
		.log("${body}")
		.transform().constant("My Constant Message")// estamos usando o "transform" para mudar a mensagem, captada do timer, e o "constant" para deixar ela fixa
		.log("${body}")
		//.transform().constant("Time now is " + LocalDateTime.now())// aqui estamos definindo o tempo como uma constante, ou seja, vai imprimir a mesma data e hora
		.bean(getCurrentTimeBean) // aqui criamos um método Bean e definimos a impressão da data e hora como dinâmica
		.log("${body}")
		.bean(loggingComponent) // estamos usando o método criado somente para processar a mensagem, não para alterá-la
		//podemos especificar o método que queremos utilizar se tiver mais de um na classe, se tiver só um, não precisamos informar, EX: .bean(getCurrentTimeBean, "getCurrentTime")
		.log("${body}")
		.process(new SimpleLoggingProcessor())// usando o process para somente exibir a mensagem, sem alterá-la
		.to("log:first-timer");// database
		
	}
}

@Component
class GetCurrentTimeBean{
	public String getCurrentTime() {
		return "Time now is " + LocalDateTime.now();
	}
}

@Component // Componente criado para manter a mesma mensagem que é recebida
class SimpleLoggingProcessingComponent{
	
	private Logger logger = LoggerFactory.getLogger(SimpleLoggingProcessingComponent.class);
	
	public void process(String message) {
		logger.info("SimpleLoggingProcessingComponent {}", message);
	}
}

@Component
class SimpleLoggingProcessor implements Processor { // método criado somente para processar a mensagem, manter a mesma mensagem

	private Logger logger = LoggerFactory.getLogger(SimpleLoggingProcessingComponent.class);
	
	@Override
	public void process(Exchange exchange) throws Exception {
		logger.info("SimpleLoggingProcessor {}", exchange.getMessage().getBody());

	}

}