package br.com.smartloto.dto;
import br.com.smartloto.domain.LotteryType;
import java.time.LocalDateTime;
import java.util.List;

public record SavedGameResponse(
    Long id,
    LotteryType lotteryType,
    List<Integer> numbers,
    boolean favorite,
    LocalDateTime createdAt
) {}
