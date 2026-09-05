package br.com.smartloto.dto;

import br.com.smartloto.domain.LotteryType;
import java.time.LocalDate;
import java.util.List;

public record DrawResponse(
        Long id,
        LotteryType lotteryType,
        Integer contestNumber,
        LocalDate drawDate,
        List<Integer> numbers
) {}
