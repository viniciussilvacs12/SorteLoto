package br.com.smartloto.dto;

import br.com.smartloto.domain.LotteryType;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record SyncStatusResponse(
        LotteryType lotteryType,
        long contestsInDatabase,
        Integer databaseLatestContest,
        LocalDate databaseLatestDrawDate,
        Integer sourceLatestContest,
        Integer missingContests,
        boolean sourceReachable,
        boolean upToDate,
        LocalDateTime lastAttemptAt,
        LocalDateTime lastSuccessAt,
        String status,
        String message
) {}
