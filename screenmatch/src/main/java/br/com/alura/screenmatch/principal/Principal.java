package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.model.DadosEpisodio;
import br.com.alura.screenmatch.model.DadosSerie;
import br.com.alura.screenmatch.model.DadosTemporada;
import br.com.alura.screenmatch.model.Episodio;
import br.com.alura.screenmatch.service.ConsumoApi;
import br.com.alura.screenmatch.service.ConverteDados;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class Principal {

    private Scanner leitura = new Scanner(System.in);
    private ConsumoApi consumo = new ConsumoApi();
    private ConverteDados conversor = new ConverteDados();

    private final String URL_BASE = "https://www.omdbapi.com/?";
    private final String API_KEY = "&apikey=bfd66ad7";

    public void exibeMenu() {
        System.out.println("Digite o nome da série para busca:");
        var nomeSerie = leitura.nextLine().trim();
        String nomeSerieFormatado = nomeSerie.replace(" ", "+"); // Formata para a URL

        // 1. PRIMEIRA CHAMADA (Busca da Série)
        var json = consumo.obterDados(URL_BASE + "t=" + nomeSerieFormatado + API_KEY);
        DadosSerie dadosSerie = conversor.obterDados(json, DadosSerie.class);
        System.out.println(dadosSerie);


        if (dadosSerie == null || dadosSerie.totalTemporadas() == null || dadosSerie.totalTemporadas() == 0) {
            System.out.println("Não foi possível encontrar a série ou ela não possui temporadas.");
            return;
        }

        List<DadosTemporada> temporadas = new ArrayList<>();

        for (int i = 1; i <= dadosSerie.totalTemporadas(); i++) {
            // 2. CHAMADAS DE TEMPORADA
            json = consumo.obterDados(
                    URL_BASE + "t=" + nomeSerieFormatado + "&season=" + i + API_KEY
            );
            DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
            temporadas.add(dadosTemporada);
        }


        // --- Mapeamento para Objetos Episodio ---
        List<Episodio> episodios = temporadas.stream()
                .flatMap(t -> t.episodios().stream()
                        .map(d -> new Episodio(t.numero(), d)))
                .collect(Collectors.toList());

        System.out.println("\n--- Todos os Episódios Mapeados ---");
        // episodios.forEach(System.out::println); // Comentado

        // --- Busca de episódio por trecho do título ---
        System.out.println("\nDigite um trecho do título do episódio para buscar:");
        var trechoTitulo = leitura.nextLine();

        Optional<Episodio> episodioEncontrado = episodios.stream()
                .filter(e -> e.getTitulo().toLowerCase().contains(trechoTitulo.toLowerCase()))
                .findFirst();

        if (episodioEncontrado.isPresent()) {
            System.out.println("Episódio encontrado: " + episodioEncontrado.get());
        } else {
            System.out.println("Episódio não encontrado.");
        }

        // Coletando Estatisticas

        Map <Integer, Double> avaliacoesPorTemporada = episodios.stream()
                .filter(e-> e.getAvaliacao() > 0.0)
                .collect(Collectors.groupingBy(Episodio::getTemporada,
                                Collectors.averagingDouble(Episodio::getAvaliacao)));

        System.out.println(avaliacoesPorTemporada);

        DoubleSummaryStatistics est = episodios.stream()
                .filter(e-> e.getAvaliacao() > 0.0)
                .collect(Collectors.summarizingDouble(Episodio::getAvaliacao));
        System.out.println("Media: " + est.getAverage());
        System.out.println("Melhor Episodio: " + est.getMax());
        System.out.println("Pior Episodio: " + est.getMin());
        System.out.println("Quantidade: " + est.getCount());




        // --- Top 5 episódios ---
        System.out.println("\n--- Top 5 episódios por Avaliação ---");
        episodios.stream()
                .filter(e -> e.getAvaliacao() > 0.0) // Filtra avaliações válidas
                .sorted(Comparator.comparing(Episodio::getAvaliacao).reversed())
                .limit(5)
                .forEach(System.out::println);


//        // --- Busca por ano ---
//        System.out.println("\nA partir de que ano você deseja ver os episódios? ");
//        int ano = 0;
//        try {
//            ano = leitura.nextInt();
//        } catch (InputMismatchException e) {
//            System.err.println("Entrada inválida. Digite um número inteiro para o ano.");
//            return;
//        }
//        leitura.nextLine(); // Consome a quebra de linha após nextInt()
//
//        LocalDate dataBusca = LocalDate.of(ano, 1, 1);
//
////        System.out.println("\n--- Episódios a partir do ano " + ano + " ---");
//        episodios.stream()
//                .filter(e -> e.getDataLancamento() != null && e.getDataLancamento().isAfter(dataBusca))
//                .forEach(System.out::println);

    }

    public static void main(String[] args) {
        Principal principal = new Principal();
        principal.exibeMenu();
        principal.leitura.close(); // Fechar o Scanner
    }
}