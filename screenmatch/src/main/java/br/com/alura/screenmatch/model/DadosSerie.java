package br.com.alura.screenmatch.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

// com o JsonIgnoreProperties, ignoramos as chaves Json que não utilizaremos para não dar erros
@JsonIgnoreProperties(ignoreUnknown = true)
// JsonAlias serve para o processo de desserialização de um arquivo json, ou seja, ele recebe um Json com tais chaves e
// passa seus valores para a variável criada dentro da classe/record
// JsonProperty serve para serializar (convertido de objetos Java p JSON) e também para desserializar, mas aqui precisamos apenas do segundo
// melhor: Essa anotação é usada para definir o nome da propriedade JSON que está associada ao campo Java.
// aqui já foi usada de forma mais enxuta, mas o normal é colocar 2 parametros
//    JsonAlias({"nomeCompleto", "nome"})
//    private String nomeCompleto;

public record DadosSerie(@JsonAlias("Title") String titulo,
                         @JsonAlias("totalSeasons") Integer totalTemporadas,
                         @JsonAlias("imdbRating") String avaliacao,
                         @JsonAlias("Genre") String genero,
                         @JsonAlias("Actors") String atores,
                         @JsonAlias("Poster") String poster,
                         @JsonAlias("Plot") String sinopse) {
}
