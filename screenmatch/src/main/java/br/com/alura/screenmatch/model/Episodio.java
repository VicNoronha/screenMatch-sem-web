package br.com.alura.screenmatch.model;
import java.time.LocalDate;
import java.time.format.DateTimeParseException; // Importação necessária

public class Episodio {
    private Integer temporada;
    private String titulo;
    private Integer numeroEpisodio;
    private Double avaliacao;
    private LocalDate dataLancamento;

    // Construtor
    public Episodio (Integer numeroTemporada, DadosEpisodio dadosEpisodio){
        this.temporada = numeroTemporada;
        this.titulo = dadosEpisodio.titulo();
        this.numeroEpisodio = dadosEpisodio.numero();

        // --- CORREÇÃO 1: Tratamento de NumberFormatException para 'avaliacao' ---
        // A avaliação pode vir como "N/A" ou outro texto inválido, causando erro em Double.valueOf()
        try {
            this.avaliacao = Double.valueOf(dadosEpisodio.avaliacao());
        } catch (NumberFormatException e) {
            this.avaliacao = 0.0; // Define como 0.0 (ou outro valor padrão) se for inválido
        }

        // --- CORREÇÃO 2: Tratamento de DateTimeParseException para 'dataLancamento' ---
        // A data de lançamento também pode vir como "N/A" ou em formato inesperado
        try {
            // Assumindo que dadosEpisodio.dataLancamento() retorna a data no formato padrão ISO (YYYY-MM-DD)
            this.dataLancamento = LocalDate.parse(dadosEpisodio.dataLancamento());
        } catch (DateTimeParseException e) {
            this.dataLancamento = null; // Define como null (ou outra data padrão) se for inválida
        }
    }

    // Getters e Setters (já estavam corretos)
    public Integer getTemporada() {
        return temporada;
    }

    public void setTemporada(Integer temporada) {
        this.temporada = temporada;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Integer getNumeroEpisodio() {
        return numeroEpisodio;
    }

    public void setNumeroEpisodio(Integer numeroEpisodio) {
        this.numeroEpisodio = numeroEpisodio;
    }

    public Double getAvaliacao() {
        return avaliacao;
    }

    public void setAvaliacao(Double avaliacao) {
        this.avaliacao = avaliacao;
    }

    public LocalDate getDataLancamento() {
        return dataLancamento;
    }

    public void setDataLancamento(LocalDate dataLancamento) {
        this.dataLancamento = dataLancamento;
    }

    // Método toString (já estava correto na lógica, mas vou deixá-lo um pouco mais formatado)
    @Override
    public String toString() {
        return "Temporada: " + temporada +
                ", Episódio: " + numeroEpisodio +
                ", Título: '" + titulo + '\'' +
                ", Avaliação: " + avaliacao +
                ", Lançamento: " + dataLancamento;
    }
}