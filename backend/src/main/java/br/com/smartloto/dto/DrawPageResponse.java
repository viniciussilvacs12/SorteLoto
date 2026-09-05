package br.com.smartloto.dto;

import br.com.smartloto.domain.LotteryType;
import java.util.List;

public record DrawPageResponse(
        LotteryType lotteryType,
        List<DrawResponse> items,
        long total,
        int page,
        int pageSize,
        int totalPages
) {}
