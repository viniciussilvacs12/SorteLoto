package br.com.smartloto.service;

import br.com.smartloto.domain.Draw;
import br.com.smartloto.domain.LotteryType;
import br.com.smartloto.dto.SmartAnalysisResponse;
import br.com.smartloto.repository.DrawRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SmartAnalysisService {

    private final DrawRepository drawRepository;

    public SmartAnalysisService(DrawRepository drawRepository) {
        this.drawRepository = drawRepository;
    }

    public SmartAnalysisResponse analyze(LotteryType type, List<Integer> inputNumbers) {
        validate(type, inputNumbers);

        List<Integer> numbers = new ArrayList<>(inputNumbers);
        numbers.sort(Integer::compareTo);

        List<Draw> draws = drawRepository.findByLotteryTypeOrderByContestNumberDesc(type);

        int evenCount = (int) numbers.stream().filter(n -> n % 2 == 0).count();
        int oddCount = numbers.size() - evenCount;
        int sum = numbers.stream().mapToInt(Integer::intValue).sum();
        int consecutivePairs = countConsecutivePairs(numbers);

        double structuralScore = calculateStructuralScore(type, numbers, evenCount, consecutivePairs);
        double frequencyScore = calculateFrequencyScore(type, numbers, draws);
        double delayScore = calculateDelayScore(type, numbers, draws);

        double smartScore = round(
                structuralScore * 0.55 +
                frequencyScore * 0.25 +
                delayScore * 0.20
        );

        String classification =
                smartScore >= 85 ? "MUITO EQUILIBRADO" :
                smartScore >= 75 ? "EQUILIBRADO" :
                smartScore >= 60 ? "MODERADO" :
                "CONCENTRADO";

        String explanation =
                "SmartScore 2.0 combina estrutura do jogo (55%), frequência histórica (25%) "
                + "e atraso relativo (20%). Frequência e atraso não tornam uma dezena mais provável.";

        return new SmartAnalysisResponse(
                type, numbers, evenCount, oddCount, sum, consecutivePairs,
                structuralScore, frequencyScore, delayScore, smartScore,
                classification, explanation
        );
    }

    private double calculateStructuralScore(LotteryType type, List<Integer> numbers, int evenCount, int consecutivePairs) {
        double idealEven = type == LotteryType.MEGA_SENA ? 3.0 : 7.5;
        double parity = Math.max(0, 100 - Math.abs(evenCount - idealEven) * 18);

        int segments = type == LotteryType.MEGA_SENA ? 3 : 5;
        int segmentSize = type.getMaxNumber() / segments;
        int[] counts = new int[segments];

        for (int number : numbers) {
            int index = Math.min((number - 1) / segmentSize, segments - 1);
            counts[index]++;
        }

        long occupied = Arrays.stream(counts).filter(c -> c > 0).count();
        double distribution = ((double) occupied / segments) * 100.0;
        double sequence = Math.max(0, 100 - consecutivePairs * 15.0);

        return round(parity * 0.40 + distribution * 0.35 + sequence * 0.25);
    }

    private double calculateFrequencyScore(LotteryType type, List<Integer> numbers, List<Draw> draws) {
        if (draws.isEmpty()) return 50.0;

        Map<Integer, Long> frequency = new HashMap<>();
        for (int n = 1; n <= type.getMaxNumber(); n++) frequency.put(n, 0L);

        for (Draw draw : draws) {
            for (Integer n : draw.getNumbers()) {
                frequency.put(n, frequency.getOrDefault(n, 0L) + 1);
            }
        }

        long max = frequency.values().stream().mapToLong(Long::longValue).max().orElse(1);
        long min = frequency.values().stream().mapToLong(Long::longValue).min().orElse(0);

        double total = 0;
        for (Integer n : numbers) {
            long value = frequency.getOrDefault(n, 0L);
            double normalized = max == min ? 50.0 : ((double)(value - min) / (max - min)) * 100.0;
            // Favorece zona intermediária-alta sem supervalorizar os extremos.
            total += 100.0 - Math.abs(normalized - 65.0);
        }
        return round(total / numbers.size());
    }

    private double calculateDelayScore(LotteryType type, List<Integer> numbers, List<Draw> draws) {
        if (draws.isEmpty()) return 50.0;

        Map<Integer, Integer> delay = new HashMap<>();
        for (int n = 1; n <= type.getMaxNumber(); n++) {
            int d = draws.size();
            for (int i = 0; i < draws.size(); i++) {
                if (draws.get(i).getNumbers().contains(n)) {
                    d = i;
                    break;
                }
            }
            delay.put(n, d);
        }

        int maxDelay = delay.values().stream().mapToInt(Integer::intValue).max().orElse(1);
        double total = 0;
        for (Integer n : numbers) {
            double normalized = maxDelay == 0 ? 0 : ((double) delay.getOrDefault(n, 0) / maxDelay) * 100.0;
            // Zona moderada de atraso recebe melhor nota; atraso extremo não é tratado como "melhor".
            total += 100.0 - Math.abs(normalized - 45.0);
        }
        return round(total / numbers.size());
    }

    private int countConsecutivePairs(List<Integer> numbers) {
        int count = 0;
        for (int i = 1; i < numbers.size(); i++) {
            if (numbers.get(i) == numbers.get(i - 1) + 1) count++;
        }
        return count;
    }

    private void validate(LotteryType type, List<Integer> numbers) {
        if (numbers == null || numbers.size() != type.getQuantity()) {
            throw new IllegalArgumentException("Quantidade inválida para " + type);
        }
        if (new HashSet<>(numbers).size() != numbers.size()) {
            throw new IllegalArgumentException("Os números não podem se repetir.");
        }
        if (numbers.stream().anyMatch(n -> n < 1 || n > type.getMaxNumber())) {
            throw new IllegalArgumentException("Números fora do intervalo da modalidade.");
        }
    }

    private double round(double value) {
        return Math.round(value * 10.0) / 10.0;
    }
}
