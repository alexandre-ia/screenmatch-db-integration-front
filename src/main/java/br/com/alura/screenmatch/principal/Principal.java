package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.model.*;
import br.com.alura.screenmatch.repository.SerieRepository;
import br.com.alura.screenmatch.service.ConsumoApi;
import br.com.alura.screenmatch.service.ConverteDados;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class Principal {



    private ConsumoApi consumo = new ConsumoApi();
    private ConverteDados conversor = new ConverteDados();
    private Scanner leitura = new Scanner(System.in);

    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=6585022c";
    private List<DadosSerie> dadosSeries = new ArrayList<>();
    private SerieRepository repositorio;
    private List<Serie> series =  new ArrayList<>();

    public Principal(SerieRepository repositorio) {
        this.repositorio = repositorio;
    }


    public  void exibeMenu(){
        var opcao = -1;
        while (opcao != 0){
        var menu = """
                1 - Buscar séries
                2 - Buscar episódios
                3 - Listar Series buscadas
                4 - Buscar series por titulo
                5 - Buscar series por ator
                6 - Top 5 series
                7 - Buscar series pela categoria
                8 - Buscar series por total  de temporadas e avaliacao
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
                       buscarSeriePorTittulo();
                       break;
                case 5:
                       buscarSeriePorAtor();
                       break;
                case 6:
                    buscarTop5Series();
                    break;
                case 7:
                    buscarPorCategoria();
                    break;
                case 8:
                    buscarPorTemporadasAvaliacao();
                    break;
                case 0:
                    System.out.println("Saindo...");
                    break;
                default:
                    System.out.println("Opção inválida");

                    //Selecionar numero maximo de temporadas e com avaliacao acima de um valor que eu der
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

        private void buscarEpisodioPorSerie(){
        listarSeriesBuscadas();
            System.out.println("Digite o nome do serie para busca");
            var nomeSerie = leitura.nextLine();
            Optional<Serie> serie = repositorio.findByTituloContainingIgnoreCase(nomeSerie.toLowerCase());

            if (serie.isPresent()) {
                var serieEncontrado = serie.get();
                List<DadosTemporada> temporadas = new ArrayList<>();

                for (int i = 1; i <= serieEncontrado.getTotalTemporada(); i++) {
                    var json = consumo.obterDados(ENDERECO + serieEncontrado.getTitulo().replace(" ", "+") + "&season=" + i + API_KEY);
                    DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
                    temporadas.add(dadosTemporada);
                }
                temporadas.forEach(System.out::println);

                List<Episodio> episodios = temporadas.stream().
                        flatMap(d -> d.episodios().stream()
                                .map(e -> new Episodio(d.numero(), e)))
                        .collect(Collectors.toList());
                serieEncontrado.setEpisodios(episodios);
                repositorio.save(serieEncontrado);
            }else{
                System.out.println("Nenhum serie encontrado!");
            }
        }

        private void listarSeriesBuscadas() {

            series =  repositorio.findAll();
            series.stream()
                    .sorted(Comparator.comparing(Serie::getGenero))
                    .forEach(System.out::println);

        }


      private void buscarSeriePorTittulo(){
          System.out.println("Digite o nome do serie para busca");
          var nomeSerie = leitura.nextLine();
          Optional<Serie> serieBuscada = repositorio.findByTituloContainingIgnoreCase(nomeSerie.toLowerCase());

          if (serieBuscada.isPresent()) {
              System.out.println("Dados do serie buscado: " + serieBuscada.get());
          }else{
                  System.out.println("Nenhum serie encontrado!");
          }

        }

        private void buscarSeriePorAtor() {
            System.out.println("Digite o nome para busca");
            var nomeAtor = leitura.nextLine();
            System.out.println("Avaliacoes a partir de que valor?");
            var avaliacao = leitura.nextDouble();
            List<Serie> seriesEncontradas = repositorio.findByAtoresContainingIgnoreCaseAndAvaliacaoGreaterThanEqual(nomeAtor, avaliacao);
            System.out.println("Series em que " + nomeAtor + " trabalhou: ");
            seriesEncontradas.forEach(s -> System.out.println(s.getTitulo() + "avaliacao " + s.getAvaliacao()));
        }

    private void buscarTop5Series() {
        List<Serie> seriesTop = repositorio.findTop5ByOrderByAvaliacaoDesc();
        seriesTop.forEach(s ->
                System.out.println(s.getTitulo() + " avaliacao " + s.getAvaliacao()));
    }

    private void buscarPorCategoria() {
        System.out.println("Digite uma categoria para busca ");
        var nomeCategoria = leitura.nextLine();
        Categoria categoria = Categoria.fromPortugues(nomeCategoria);
        List<Serie> seriesCatergoria= repositorio.findByGenero(categoria);
        System.out.println("Series da categoria " + nomeCategoria + ": ");
        seriesCatergoria.forEach(System.out::println);

    }

    private void buscarPorTemporadasAvaliacao() {
        System.out.println("Digite o numero maximo de temporadas para busca ");
        var numeroMaximoTemporadas = leitura.nextInt();
        System.out.println("Avaliacoes a partir de que valor?");
        var avaliacao = leitura.nextDouble();
        List<Serie> seriesEncontradas = repositorio.findByTotalTemporadaLessThanEqualAndAvaliacaoGreaterThan(numeroMaximoTemporadas, avaliacao);
        seriesEncontradas.forEach(s -> System.out.println(s.getTitulo() + " avaliacao " + s.getAvaliacao()));
    }
    }

