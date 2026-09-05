package br.com.smartloto.dto;
import br.com.smartloto.domain.LotteryType;
import java.util.List;
public record SaveGameRequest(LotteryType lotteryType, List<Integer> numbers) {}
