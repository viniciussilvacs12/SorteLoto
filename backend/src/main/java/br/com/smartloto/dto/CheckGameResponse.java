package br.com.smartloto.dto;
import java.util.List;

public record CheckGameResponse(
    Long savedGameId,
    int contestNumber,
    int hits,
    List<Integer> matchedNumbers
) {}
