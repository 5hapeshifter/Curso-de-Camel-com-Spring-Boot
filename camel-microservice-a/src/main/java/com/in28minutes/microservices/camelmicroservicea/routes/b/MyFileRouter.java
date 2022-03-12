package com.in28minutes.microservices.camelmicroservicea.routes.b;

import java.util.Map;

import org.apache.camel.Body;
import org.apache.camel.ExchangeProperties;
import org.apache.camel.Headers;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

//@Component
public class MyFileRouter extends RouteBuilder {
	
	@Autowired
	private DeciderBean deciderBean;

	@Override
	public void configure() throws Exception {// criamos uma rota de transferência de arquivos entre duas pastas
		
		//First pattern - Pipeline - é usado apenas para determinar uma sequência de passos
		
		from("file:files/input")// aqui estamos informando a o local do arquivo "files" que está na pasta input
		//.pipeline()
		.routeId("Files-Input-Route") // aqui estamos atribuindo um ID para a rota
		.transform().body(String.class)// aqui estamos transformando o body em String para o "when simple body" ler o conteúdo do body e ver se contem USD 
		.choice()// aqui estamos usando o choice para definir o que o sistema deve fazer conforme o tipo de arquivo
		//choice é um Content Based Routing PATTERN - é tipicamente baseado em choice/escolhas
			.when(simple("${file:ext} == 'xml'"))
				.log("XML FILE")
			//.when(simple("${body} contains 'USD'"))
			.when(method(deciderBean))
				.log("Not an XML FILE BUT contains USD")
			.otherwise()
				.log("Not an XML FILE")
		.end()
		//.log("${messageHistory} ${file:absolute.path}")
		//.to("direct://log-file-values")
		.to("file:files/output");// aqui estamos informando que enviaremos o arquivo para a pasta output
		
		from("direct://log-file-values")
		
		//as linhas abaixo são um pipeline
		.log("${messageHistory} ${file:absolute.path}")
		.log("${file:name} ${file:name.ext} ${file:name.noext} ${file:onlyname}")
		.log("${file:onlyname.noext} ${file:parent} ${file:path} ${file:absolute}")
		.log("${file:size} ${file:modified}")
		.log("${routeId} ${camelId} ${body}")
		.to("file:files/output");
		
	}
	
}

@Component
class DeciderBean{
	
	Logger logger = LoggerFactory.getLogger(DeciderBean.class);
	
	public boolean isThisConditionMet(@Body String body, 
			@Headers Map<String,String> headers, 
			@ExchangeProperties Map<String,String> exchangeProperties) {
		logger.info("DeciderBean {} {} {}", body, headers, exchangeProperties);
		return true;
	}
	
}
