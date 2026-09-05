package br.com.smartloto.dto;
import java.util.List;

public record CaixaDrawResponse(
    Integer numero,
    String dataApuracao,
    List<String> listaDezenas
) {}
