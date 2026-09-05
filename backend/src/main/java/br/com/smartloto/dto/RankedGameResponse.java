package br.com.smartloto.dto;
import br.com.smartloto.domain.LotteryType;
import java.util.List;

public record RankedGameResponse(
        Long id,
        LotteryType lotteryType,
        List<Integer> numbers,
        boolean favorite,
        double smartScore,
        String classification
) {}
