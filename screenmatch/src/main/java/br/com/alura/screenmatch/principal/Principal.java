package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.model.*;
import br.com.alura.screenmatch.repository.SerieRepository;
import br.com.alura.screenmatch.service.ConsumoApi;
import br.com.alura.screenmatch.service.ConverteDados;

import java.util.*;
import java.util.stream.Collectors;

public class Principal {
    private Scanner leitura = new Scanner(System.in);
    private ConsumoApi consumo = new ConsumoApi();
    private ConverteDados conversor = new ConverteDados();
    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=6585022c";
//    private List<DadosSerie> dadosSeries = new ArrayList<>();

    private SerieRepository repositorio;
    private List<Serie> series = new ArrayList<>();

    // para a 10
    private Optional<Serie> serieBusca;

    public Principal(SerieRepository repositorio) {
        this.repositorio = repositorio;
    }

    public void exibeMenu() {
        var opcao = -1;
        while(opcao != 0) {
            var menu = """
                    1 - Buscar séries
                    2 - Buscar episódios
                    3 - Listar séries buscadas
                    4 - Buscar série por título
                    5 - Buscar séries por ator
                    6 - Top 5 Séries
                    7 - Buscar séries por categoria
                    8 - Filtrar séries 
                    9 - Buscar episódios por trecho do título
                    10 - Buscar top 5 episódios de uma Série
                    11 - Buscar episódios a partir de uma data
                                    
                    0 - Sair                                 
                    """;

            System.out.println(menu);
            opcao = leitura.nextInt();
            leitura.nextLine();

            switch (opcao) {
                case 1:
                    buscarSerieWeb();
                    break;
                case 2:
                    buscarEpisodioPorSerie();
                    break;
                case 3:
                    listarSeriesBuscadas();
                    break;
                case 4:
                    buscarSeriePorTitulo();
                    break;
                case 5:
                    buscarSeriesPorAtor();
                    break;
                case 6:
                    buscarTop5Series();
                    break;
                case 7:
                    buscarSeriesPorCategoria();
                    break;
                case 8:
                    filtrarSeriesPorTemporadaEAvaliacao();
                    break;
                case 9:
                    buscarEpisodioPorTrecho();
                    break;
                case 10:
                    topEpisodiosDaSerie();
                    break;
                case 11:
                    buscarEpisodiosAPartirDeUmAno();
                    break;
                case 0:
                    System.out.println("Saindo...");
                    break;
                default:
                    System.out.println("Opção inválida");
            }
        }
    }



    private void buscarSerieWeb() {
        DadosSerie dados = getDadosSerie();
        Serie serie = new Serie(dados);
        //dadosSeries.add(dados);
        repositorio.save(serie);
        System.out.println(dados);
    }

    private DadosSerie getDadosSerie() {
        System.out.println("Digite o nome da série para busca");
        var nomeSerie = leitura.nextLine();
        var json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);
        DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
        return dados;
    }

    private void buscarSeriePorTitulo() {
        System.out.println("Escolha um série pelo nome: ");
        var nomeSerie = leitura.nextLine();
         serieBusca = repositorio.findByTituloContainingIgnoreCase(nomeSerie);

        if (serieBusca.isPresent()) {
            System.out.println("Dados da série: " + serieBusca.get());

        } else {
            System.out.println("Série não encontrada!");
        }

    }
    private void buscarEpisodioPorSerie(){
        listarSeriesBuscadas();
        buscarSeriePorTitulo();
        if(serieBusca.isPresent()) {

            var serieEncontrada = serieBusca.get();
            List<DadosTemporada> temporadas = new ArrayList<>();

            for (int i = 1; i <= serieEncontrada.getTotalTemporadas(); i++) {
                var json = consumo.obterDados(ENDERECO + serieEncontrada.getTitulo().replace(" ", "+") + "&season=" + i + API_KEY);
                DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
                temporadas.add(dadosTemporada);
            }
            temporadas.forEach(System.out::println);

            List<Episodio> episodios = temporadas.stream()
                    .flatMap(d -> d.episodios().stream()
                            .map(e -> new Episodio(d.numero(), e)))
                    .collect(Collectors.toList());

            serieEncontrada.setEpisodios(episodios);
            repositorio.save(serieEncontrada);
        } else {
            System.out.println("Série não encontrada!");
        }
    }

    private void listarSeriesBuscadas(){
        series = repositorio.findAll();
        series.stream()
                .sorted(Comparator.comparing(Serie::getGenero))
                .forEach(System.out::println);
    }


    private void buscarSeriesPorAtor() {
        System.out.println("Qual o nome para busca?");
        var nomeAtor = leitura.nextLine();
        System.out.println("Avaliações a partir de que valor? ");
        var avaliacao = leitura.nextDouble();
        List<Serie> seriesEncontradas = repositorio.findByAtoresContainingIgnoreCaseAndAvaliacaoGreaterThanEqual(nomeAtor, avaliacao);
        System.out.println("Séries em que " + nomeAtor + " trabalhou: ");
        seriesEncontradas.forEach(s ->
                System.out.println(s.getTitulo() + " avaliação: " + s.getAvaliacao()));
    }

    private void buscarTop5Series() {
        List<Serie> serieTop = repositorio.findTop5ByOrderByAvaliacaoDesc();
        serieTop.forEach(s ->
                System.out.println(s.getTitulo() + " avaliação: " + s.getAvaliacao()));
    }

    private void buscarSeriesPorCategoria() {
        System.out.println("Deseja buscar séries de que categoria/gênero? ");
        var nomeGenero = leitura.nextLine();
        Categoria categoria = Categoria.fromPortugues(nomeGenero);
        List<Serie> seriesPorCategoria = repositorio.findByGenero(categoria);
        System.out.println("Séries da categoria " + nomeGenero);
        seriesPorCategoria.forEach(System.out::println);
    }

    private void filtrarSeriesPorTemporadaEAvaliacao(){
        System.out.println("Filtrar séries até quantas temporadas? ");
        var totalTemporadas = leitura.nextInt();
        leitura.nextLine();
        System.out.println("Com avaliação a partir de que valor? ");
        var avaliacao = leitura.nextDouble();
        leitura.nextLine();
        List<Serie> filtroSeries = repositorio.filtrarPorTemporadasEAvaliacao(totalTemporadas, avaliacao);
        System.out.println("*** Séries filtradas ***");
        filtroSeries.forEach(s ->
                System.out.println(s.getTitulo() + "  - avaliação: " + s.getAvaliacao()));
    }

    private void buscarEpisodioPorTrecho(){
        System.out.println("Digite um trecho que faça parte do título de um ou mais episódios");
        var trechoEpisodio = leitura.nextLine();
        List<Episodio> episodiosPorTrecho = repositorio.episodiosPorTrecho(trechoEpisodio);
        System.out.println("Episódios encontrados:");
        episodiosPorTrecho.forEach(e->
                System.out.printf("Série: %s Temporada %s Episódio %s: %s\n",e.getSerie().getTitulo(),e.getTemporada(),e.getNumeroEpisodio(),e.getTitulo()));
    }

    private void topEpisodiosDaSerie() {
        buscarSeriePorTitulo();
        if (serieBusca.isPresent()){
            Serie serie = serieBusca.get();
            List<Episodio> topEpisodios = repositorio.topEpisodiosPorSerie(serie);
            topEpisodios.forEach(e->
                    System.out.printf("Série: %s Temporada %s Episódio %s: %s de avaliação %s\n",e.getSerie().getTitulo(),e.getTemporada(),e.getNumeroEpisodio(),e.getTitulo(), e.getAvaliacao()));
        }
    }

    private void buscarEpisodiosAPartirDeUmAno() {
        buscarSeriePorTitulo();
        if (serieBusca.isPresent()){
            Serie serie = serieBusca.get();
            System.out.println("Quer buscar episódios a partir de que ano?");
            var ano = leitura.nextInt(); leitura.nextLine();
            List<Episodio> episodiosAPartirDoAno = repositorio.buscarEpisodiosPorAno(serie, ano);

            episodiosAPartirDoAno.forEach(e->
                    System.out.printf("Série: %s Temporada %s Episódio %s: %s. Data: %s\n ",e.getSerie().getTitulo(),e.getTemporada(),e.getNumeroEpisodio(),e.getTitulo(),e.getDataLancamento()));
        }
    }















}












    //ANTES:
//        System.out.println("Digite o nome da série que deseja buscar:");
//        var nomeSerie = leitura.nextLine();
//        var json = consumeAPI.obterDados(ENDERECO + nomeSerie.replace(' ', '+') + API_KEY);
//        DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
//
//        List<DadosTemporada> temporadas = new ArrayList<>();
//		for (int i = 1; i <=dados.totalTemporadas() ; i++) {
//			json = consumeAPI.obterDados(ENDERECO + nomeSerie.replace(' ', '+') + "&season="+ i + "&" + API_KEY);
//			DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
//			temporadas.add(dadosTemporada);
//		}
//
//        // AO INVÉS DE:
////        for (int i = 0; i < dados.totalTemporadas(); i++) {
////            List<DadosEpisodio> episodiosTemporada = temporadas.get(i).episodios();
////            for (int j = 0; j < episodiosTemporada.size(); j++) {
////                System.out.println(episodiosTemporada.get(j).titulo());
////            }
////        }
//        // FAÇA ISSO, o java entende que você trabalhará com temporadas e essas flechas são utilizadas para os lambdas (arrow functions/ funçoes anonimas)
//        // outra forma de lambda for(Integer i: lista) para cada Integer em lista, faça tal...
//        temporadas.forEach(t -> t.episodios().forEach(e -> System.out.println( " - T" + t.numero() + "Ep" +e.numero()+ ". " + e.titulo())));
//        temporadas.forEach(System.out::println);
//
//        List<DadosEpisodio> dadosEpisodios = temporadas.stream()
//                .flatMap(t -> t.episodios().stream())
//                        .collect(Collectors.toList());
//        // se fizessemos apenas .toList(), viraria uma lista estática, imutável não podendo inserir novos itens
//
//        System.out.println("Top 10 Episódios");
//        dadosEpisodios.stream()
//                .filter(e -> !e.avaliacao().equalsIgnoreCase("N/A"))
//                .sorted(Comparator.comparing(DadosEpisodio::avaliacao).reversed())
//                .map(e-> e.titulo().toUpperCase())
//                        .limit(10)
//                        .forEach(System.out::println);
////        dadosEpisodios.add(new DadosEpisodio("teste",3 , "10", "2020-01-01"));
//
//        List<Episodio> episodios =temporadas.stream()
//                .flatMap(t -> t.episodios().stream()
//                        .map(d -> new Episodio(t.numero(), d)))
//                .collect(Collectors.toList());
//
//        episodios.forEach(System.out::println);
//
//        System.out.println("Digite um trecho de um título de um episódio");
//        var trechoTitulo = leitura.nextLine();
//        // o legal do optional é que pode retornar null caso não for encontrado!
//        Optional<Episodio> episodioBuscado = episodios.stream()
//                .filter(e -> e.getTitulo().toUpperCase().contains(trechoTitulo.toUpperCase()))
//                .findFirst();
//        if(episodioBuscado.isPresent()){
//            System.out.println("Episódio encontrado");
//            System.out.println("Temporada: " + episodioBuscado.get().getTemporada());
//        } else{
//            System.out.println("Episódio não encontrado");
//        }
//
//        Map<Integer,Double> avaliacoesPorTemporada = episodios.stream()
//                .filter(e -> e.getAvaliacao() > 0.0)
//                .collect(Collectors.groupingBy(Episodio::getTemporada,
//                        Collectors.averagingDouble(Episodio::getAvaliacao)));
//        System.out.println(avaliacoesPorTemporada);
//
//        DoubleSummaryStatistics est = episodios.stream()
//                .filter(e -> e.getAvaliacao()>0.0)
//                .collect(Collectors.summarizingDouble(Episodio::getAvaliacao));
//        System.out.println("Média: " + est.getAverage());

//        System.out.println("A partir de que ano você deseja ver os episódios?");
//        var ano = leitura.nextInt(); leitura.nextLine(); // bom fazer isso aós o nextInt pois ele pode confundir quando aperta enter
//
//        LocalDate dataBusca = LocalDate.of(ano, 1, 1);
//
//        DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy");
//        episodios.stream()
//                .peek(e-> System.out.println("Primeiro filtro" + e))
//                .filter(e -> e.getDataDeLancamento() != null && e.getDataDeLancamento().isAfter(dataBusca))
//                .forEach(e -> System.out.println(
//                        "Temporada " + e.getTemporada() +
//                                ", Episódio: " + e.getTitulo() +
//                                ", Data de Lançamento: " + e.getDataDeLancamento().format(formatador)
//                ));


