package br.com.alura.screenmatch.repository;

import br.com.alura.screenmatch.model.Categoria;
import br.com.alura.screenmatch.model.Serie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SerieRepository extends JpaRepository<Serie, Long> {
    Optional<Serie> findByTituloContainingIgnoreCase(String nomeSerie);

    List<Serie> findByAtoresContainingIgnoreCaseAndAvaliacaoGreaterThanEqual(String nomeAtor, double avaliacao);

    List<Serie> findTop5ByOrderByAvaliacaoDesc();

    List<Serie> findByGenero(Categoria categoria);

    List<Serie> findByTotalTemporadasLessThanEqualAndAvaliacaoGreaterThanEqual(int totalTemporadas, double avaliacao);
}

    // List<Serie> findByTotalTemporadasLessThanEqualAndAvaliacaoGreaterThanEqual(int maxTemp, double minAvaliacao);
    // por baixo dos panos dentro do PG: select * from series WHERE series.total_temporadas < = 5 AND series....
// então é melhor... (: para parametros) e s sendo a série, no singular pois faz uma de cada vez
    //@Query("select s from Serie s WHERE s.totalTemporadas <= :maxTemp AND s.avaliacao >= :minAvaliacao")
    //List<Serie> filtrarSeriesPorTemporadaEAvaliacao(int maxTemp, double minAvaliacao);

    // JOIN agrupa da lista WHERE dentro do titulo o trecho episodio for ILIKE o titulo da serie, colocando %: % no parametro para comparação
   // @Query("SELECT e FROM Serie s JOIN s.episodios e WHERE LOWER(e.titulo) LIKE LOWER(%:trechoEpisodio%)")
   // List<Episodio> episodiosPorTrecho(String trechoEpisodio);

// será chamado na principal.buscarSerieWeb
