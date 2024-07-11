package br.com.alura.screenmatch;

import br.com.alura.screenmatch.model.DadosSerie;
import br.com.alura.screenmatch.service.ConsumeAPI;
import br.com.alura.screenmatch.service.ConverteDados;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@SpringBootApplication
public class ScreenmatchApplication implements CommandLineRunner {

	// mostra que foi inicializado um projteo spring
	public static void main(String[] args) {
		SpringApplication.run(ScreenmatchApplication.class, args);
	}


	// o método run em um projeto de linha de comando (CommandLineRunner) será o nosso famoso método main
	@Override
	public void run(String... args) throws Exception {
		// var utilizado para evitar repetição, ele entende que será um novo ConsumeAPI, ou seja, desse mesmo tipo
		var consumeAPI = new ConsumeAPI();
		// json recebe o método obterDados, assim, podemos escolher outro da próxima vez
		var json = consumeAPI.obterDados("https://www.omdbapi.com/?t=gilmore+girls&apikey=6585022c");
		System.out.println(json);
		// aqui por exemplo trocamos, mas não vamos utilizar:
//		json = consumeAPI.obterDados("https://coffee.alexflipnote.dev/random.json");
//		System.out.println(json);

		//criando o Conversor
		ConverteDados conversor = new ConverteDados();
		DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
		System.out.println(dados);
	}
}
