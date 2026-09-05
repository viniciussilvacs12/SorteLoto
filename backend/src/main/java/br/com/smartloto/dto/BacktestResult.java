package br.com.smartloto.dto;

import br.com.smartloto.domain.LotteryType;

public record BacktestResult(
        LotteryType lotteryType,
        int tests,
        double averageHits,
        int bestHits,
        int bestContest,
        int hitsAtLeast3,
        int hitsAtLeast4,
        int hitsAtLeast5,
        int hitsAtLeast6,
        String note
) {}
