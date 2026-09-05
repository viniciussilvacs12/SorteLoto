package br.com.smartloto.dto;

import br.com.smartloto.domain.LotteryType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record AnalysisRequest(
        @NotNull LotteryType lotteryType,
        @NotEmpty List<Integer> numbers
) {
}
