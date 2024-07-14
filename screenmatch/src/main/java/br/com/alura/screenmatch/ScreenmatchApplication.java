package br.com.alura.screenmatch;

import br.com.alura.screenmatch.model.DadosTemporada;
import br.com.alura.screenmatch.model.Serie;
import br.com.alura.screenmatch.principal.Principal;
import br.com.alura.screenmatch.repository.SerieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class ScreenmatchApplication implements CommandLineRunner {

	@Autowired
	private SerieRepository repositorio;
	// mostra que foi inicializado um projteo spring
	public static void main(String[] args) {
		SpringApplication.run(ScreenmatchApplication.class, args);
	}


	// o método run em um projeto de linha de comando (CommandLineRunner) será o nosso famoso método main
	@Override
	public void run(String... args) throws Exception {
		// var utilizado para evitar repetição, ele entende que será um novo ConsumeAPI, ou seja, desse mesmo tipo
		// json recebe o método obterDados, assim, podemos escolher outro da próxima vez
		// aqui por exemplo trocamos, mas não vamos utilizar:
//		json = consumeAPI.obterDados("https://coffee.alexflipnote.dev/random.json");
//		System.out.println(json);
// todos passados para o Principal
		//criando o Conversor


		Principal principal	= new Principal(repositorio);
		principal.exibeMenu();

	}
}
