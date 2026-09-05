package br.com.smartloto.dto;

import br.com.smartloto.domain.LotteryType;
import java.util.List;

public record StatsResponse(
        LotteryType lotteryType,
        long contestsAnalyzed,
        List<NumberStat> hottest,
        List<NumberStat> coldest,
        List<NumberStat> mostDelayed,
        String dataSource
) {}
