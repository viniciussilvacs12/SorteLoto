package br.com.smartloto.service;

import br.com.smartloto.domain.Draw;
import br.com.smartloto.domain.LotteryType;
import br.com.smartloto.dto.StrategyComparisonResponse;
import br.com.smartloto.repository.DrawRepository;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.*;

@Service
public class StrategyComparisonService {

    private final DrawRepository drawRepository;
    private final SecureRandom random = new SecureRandom();

    public StrategyComparisonService(DrawRepository drawRepository) {
        this.drawRepository = drawRepository;
    }

    public StrategyComparisonResponse compare(LotteryType type, int tests, int smartCandidates) {
        List<Draw> all = drawRepository.findByLotteryTypeOrderByContestNumberDesc(type)
                .stream()
                .sorted(Comparator.comparingInt(Draw::getContestNumber))
                .toList();

        if (all.size() < 30) {
            throw new IllegalStateException("Importe pelo menos 30 concursos antes de comparar estratégias.");
        }

        int actualTests = Math.min(Math.max(tests, 5), Math.min(100, all.size() - 20));
        int candidates = Math.min(Math.max(smartCandidates, 20), 1000);

        int start = all.size() - actualTests;

        double smartTotal = 0;
        double randomTotal = 0;
        int smartBest = 0;
        int randomBest = 0;

        for (int targetIndex = start; targetIndex < all.size(); targetIndex++) {
            Draw target = all.get(targetIndex);
            List<Draw> history = all.subList(0, targetIndex);

            List<Integer> smartGame = pickSmart(type, history, candidates);
            List<Integer> randomGame = randomGame(type);

            int smartHits = hits(smartGame, target.getNumbers());
            int randomHits = hits(randomGame, target.getNumbers());

            smartTotal += smartHits;
            randomTotal += randomHits;
            smartBest = Math.max(smartBest, smartHits);
            randomBest = Math.max(randomBest, randomHits);
        }

        double smartAvg = round(smartTotal / actualTests);
        double randomAvg = round(randomTotal / actualTests);
        double advantage = round(smartAvg - randomAvg);

        String verdict;
        if (Math.abs(advantage) < 0.15) {
            verdict = "EMPATE TÉCNICO";
        } else if (advantage > 0) {
            verdict = "SMARTLOTO ACIMA DO ALEATÓRIO NESTE RECORTE";
        } else {
            verdict = "ALEATÓRIO ACIMA DO SMARTLOTO NESTE RECORTE";
        }

        return new StrategyComparisonResponse(
                type, actualTests,
                smartAvg, randomAvg,
                smartBest, randomBest,
                advantage, verdict
        );
    }

    private List<Integer> pickSmart(LotteryType type, List<Draw> history, int candidates) {
        List<Integer> best = null;
        double bestScore = -1;

        for (int i = 0; i < candidates; i++) {
            List<Integer> game = randomGame(type);
            double score = score(type, game, history);
            if (score > bestScore) {
                bestScore = score;
                best = game;
            }
        }
        return best;
    }

    private double score(LotteryType type, List<Integer> game, List<Draw> history) {
        int even = (int) game.stream().filter(n -> n % 2 == 0).count();
        double idealEven = type == LotteryType.MEGA_SENA ? 3.0 : 7.5;
        double parity = Math.max(0, 100 - Math.abs(even - idealEven) * 18);

        Map<Integer, Long> freq = new HashMap<>();
        for (int n = 1; n <= type.getMaxNumber(); n++) freq.put(n, 0L);
        for (Draw draw : history) {
            for (Integer n : draw.getNumbers()) {
                freq.put(n, freq.get(n) + 1);
            }
        }

        long max = freq.values().stream().mapToLong(Long::longValue).max().orElse(1);
        long min = freq.values().stream().mapToLong(Long::longValue).min().orElse(0);

        double frequency = 0;
        for (Integer n : game) {
            long f = freq.get(n);
            double normalized = max == min ? 50 : ((double)(f - min) / (max - min)) * 100;
            frequency += 100 - Math.abs(normalized - 65);
        }
        frequency /= game.size();

        return parity * 0.60 + frequency * 0.40;
    }

    private List<Integer> randomGame(LotteryType type) {
        Set<Integer> set = new TreeSet<>();
        while (set.size() < type.getQuantity()) {
            set.add(random.nextInt(type.getMaxNumber()) + 1);
        }
        return new ArrayList<>(set);
    }

    private int hits(List<Integer> game, List<Integer> result) {
        Set<Integer> target = new HashSet<>(result);
        return (int) game.stream().filter(target::contains).count();
    }

    private double round(double v) {
        return Math.round(v * 100.0) / 100.0;
    }
}
