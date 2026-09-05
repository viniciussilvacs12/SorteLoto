package br.com.smartloto.dto;

import br.com.smartloto.domain.LotteryType;

public record ImportResponse(
        LotteryType lotteryType,
        int imported,
        int skipped,
        int failed,
        Integer sourceLatestContest,
        Integer databaseLatestContest,
        boolean upToDate,
        String message
) {}
