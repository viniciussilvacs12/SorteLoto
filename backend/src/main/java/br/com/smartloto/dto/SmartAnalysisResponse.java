package br.com.smartloto.dto;

import br.com.smartloto.domain.LotteryType;
import java.util.List;

public record SmartAnalysisResponse(
        LotteryType lotteryType,
        List<Integer> numbers,
        int evenCount,
        int oddCount,
        int sum,
        int consecutivePairs,
        double structuralScore,
        double frequencyScore,
        double delayScore,
        double smartScore,
        String classification,
        String explanation
) {}
