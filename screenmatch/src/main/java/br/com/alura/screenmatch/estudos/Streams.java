package br.com.alura.screenmatch.estudos;

import java.util.Arrays;
import java.util.List;

public class Streams {
    public static void main(String[] args) {
        // cada episódio é um fluxo de dados, e com o Java 8 vieram as streams, fluxos de dados usados para fazer operações encadeadas
        //com o lambda, faz-se uma iteração sobre os fluxos de dados, com a stream se opera de várias formas sobre tais
        List<String> nomes = Arrays.asList("Paulo", "José", "Rafael", "Mariana", "Cauã");
        //stream é encadeamento, começa com .stream, depois coloca as operações como sorted (ordenar), limit e forEach (para cada faça tal)
        nomes.stream()
                .sorted()
                .limit(3)
                .filter(n -> n.startsWith("M"))
                .map(n -> n.toUpperCase())
                .forEach(System.out::println);
        // isso tudo em UMA linha de código
    }
}
