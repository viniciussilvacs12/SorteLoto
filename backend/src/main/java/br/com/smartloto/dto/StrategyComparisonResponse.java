package br.com.smartloto.dto;

import br.com.smartloto.domain.LotteryType;

public record StrategyComparisonResponse(
        LotteryType lotteryType,
        int tests,
        double smartAverageHits,
        double randomAverageHits,
        int smartBestHits,
        int randomBestHits,
        double smartAdvantage,
        String verdict
) {}
