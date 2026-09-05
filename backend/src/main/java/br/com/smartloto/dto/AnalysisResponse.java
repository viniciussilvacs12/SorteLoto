package br.com.smartloto.dto;

import br.com.smartloto.domain.LotteryType;

import java.util.List;

public record AnalysisResponse(
        LotteryType lotteryType,
        List<Integer> numbers,
        int evenCount,
        int oddCount,
        int sum,
        int consecutivePairs,
        double balanceScore,
        String classification,
        String message
) {
}
