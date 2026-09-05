package br.com.smartloto.service;

import br.com.smartloto.domain.LotteryType;
import br.com.smartloto.dto.AnalysisRequest;
import br.com.smartloto.dto.AnalysisResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class AnalysisService {

    public AnalysisResponse analyze(AnalysisRequest request) {
        LotteryType type = request.lotteryType();
        List<Integer> numbers = new ArrayList<>(request.numbers());
        numbers.sort(Integer::compareTo);

        validate(type, numbers);

        int evenCount = (int) numbers.stream().filter(n -> n % 2 == 0).count();
        int oddCount = numbers.size() - evenCount;
        int sum = numbers.stream().mapToInt(Integer::intValue).sum();
        int consecutivePairs = countConsecutivePairs(numbers);

        double parityScore = calculateParityScore(type, evenCount);
        double rangeScore = calculateRangeScore(type, numbers);
        double sequenceScore = Math.max(0, 100 - (consecutivePairs * 15));

        double score = Math.round(((parityScore * 0.40)
                + (rangeScore * 0.35)
                + (sequenceScore * 0.25)) * 10.0) / 10.0;

        String classification;
        if (score >= 80) {
            classification = "EQUILIBRADO";
        } else if (score >= 60) {
            classification = "MODERADO";
        } else {
            classification = "CONCENTRADO";
        }

        String message = "Pontuação heurística baseada em equilíbrio de paridade, distribuição por faixas "
                + "e quantidade de sequências. Não representa previsão de resultado futuro.";

        return new AnalysisResponse(
                type,
                numbers,
                evenCount,
                oddCount,
                sum,
                consecutivePairs,
                score,
                classification,
                message
        );
    }

    private void validate(LotteryType type, List<Integer> numbers) {
        if (numbers.size() != type.getQuantity()) {
            throw new IllegalArgumentException(
                    "A quantidade de números para " + type + " deve ser " + type.getQuantity()
            );
        }

        Set<Integer> unique = new HashSet<>(numbers);
        if (unique.size() != numbers.size()) {
            throw new IllegalArgumentException("Os números não podem se repetir.");
        }

        boolean invalid = numbers.stream()
                .anyMatch(n -> n < 1 || n > type.getMaxNumber());

        if (invalid) {
            throw new IllegalArgumentException(
                    "Os números devem estar entre 1 e " + type.getMaxNumber()
            );
        }
    }

    private int countConsecutivePairs(List<Integer> numbers) {
        int count = 0;
        for (int i = 1; i < numbers.size(); i++) {
            if (numbers.get(i) == numbers.get(i - 1) + 1) {
                count++;
            }
        }
        return count;
    }

    private double calculateParityScore(LotteryType type, int evenCount) {
        double ideal = type == LotteryType.MEGA_SENA ? 3.0 : 7.5;
        double distance = Math.abs(evenCount - ideal);
        return Math.max(0, 100 - (distance * 18));
    }

    private double calculateRangeScore(LotteryType type, List<Integer> numbers) {
        int segments = type == LotteryType.MEGA_SENA ? 3 : 5;
        int segmentSize = type.getMaxNumber() / segments;

        int[] counts = new int[segments];

        for (int number : numbers) {
            int index = Math.min((number - 1) / segmentSize, segments - 1);
            counts[index]++;
        }

        long occupied = java.util.Arrays.stream(counts).filter(c -> c > 0).count();
        return ((double) occupied / segments) * 100.0;
    }
}
