package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.model.DadosEpisodio;
import br.com.alura.screenmatch.model.DadosSerie;
import br.com.alura.screenmatch.model.DadosTemporada;
import br.com.alura.screenmatch.model.Episodio;
import br.com.alura.screenmatch.service.ConsumoApi;
import br.com.alura.screenmatch.service.ConverteDados;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Principal {

    private Scanner leitura = new Scanner(System.in);
    private ConsumoApi consumo = new ConsumoApi();
    private ConverteDados conversor = new ConverteDados();

    private final String URL_BASE = "https://www.omdbapi.com/?";
    private final String API_KEY = "&apikey=bfd66ad7";

    public void exibeMenu() {
        System.out.println("Digite o nome da série para busca");
        var nomeSerie = leitura.nextLine();

        // 1. PRIMEIRA CHAMADA (Busca da Série)
        var json = consumo.obterDados(URL_BASE + "t=" + nomeSerie.replace(" ", "+") + API_KEY);
        DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
        System.out.println(dados);


        List<DadosTemporada> temporadas = new ArrayList<>();

        for (int i = 1; i <= dados.totalTemporadas(); i++) {
            // 2. CHAMADAS DE TEMPORADA (Estrutura da URL Corrigida)
            json = consumo.obterDados(
                    URL_BASE + "t=" + nomeSerie.replace(" ", "+") + "&season=" + i + API_KEY
            );
            DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
            temporadas.add(dadosTemporada);
        }
        temporadas.forEach(System.out::println);

        // ...
        temporadas.forEach(t -> t.episodios().forEach(e ->
                System.out.println(e.titulo())));


        List<DadosEpisodio> dadosEpisodios = temporadas.stream()
                .flatMap(t -> t.episodios().stream())
                .collect(Collectors.toList());

        System.out.println("Top 5 episódios");
        dadosEpisodios.stream()
                .filter(e -> !e.titulo().equalsIgnoreCase("N/A"))
                .filter(e -> !e.avaliacao().equalsIgnoreCase("N/A"))

                // CORREÇÃO: Usa o novo método auxiliar para simplificar o Comparator.comparing
                .sorted(Comparator.comparing(this::getAvaliacaoNumerica).reversed())
                .limit(5)
                .forEach(System.out::println);

//        System.out.println("A partir de que ano você deseja ver os episódios? ");
//        var ano int = leitura.nextInt();
//        leitura.nextLine();
//
//        LocalDate dataBusca = LocalDate.of((ano, 1, 1))


    }


    // NOVO MÉTODO: Simplifica o código de ordenação e trata NumberFormatException
    private Double getAvaliacaoNumerica(DadosEpisodio e) {
        if (e.avaliacao() == null || e.avaliacao().equalsIgnoreCase("N/A")) {
            return 0.0;
        }
        try {
            return Double.parseDouble(e.avaliacao());
        } catch (NumberFormatException ex) {
            // Pode acontecer se a string for inesperada (ex: "4/5")
            return 0.0;
        }
    }


    public static void main(String[] args) {
        Principal principal = new Principal();
        principal.exibeMenu();
    }
}
