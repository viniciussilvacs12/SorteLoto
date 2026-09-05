package br.com.smartloto.service;

import br.com.smartloto.domain.Draw;
import br.com.smartloto.domain.LotteryType;
import br.com.smartloto.dto.BacktestResult;
import br.com.smartloto.dto.SmartAnalysisResponse;
import br.com.smartloto.repository.DrawRepository;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.*;

@Service
public class BacktestService {

    private final DrawRepository drawRepository;
    private final SecureRandom random = new SecureRandom();

    public BacktestService(DrawRepository drawRepository) {
        this.drawRepository = drawRepository;
    }

    public BacktestResult run(LotteryType type, int tests, int candidatesPerTest) {
        List<Draw> all = drawRepository.findByLotteryTypeOrderByContestNumberDesc(type)
                .stream()
                .sorted(Comparator.comparingInt(Draw::getContestNumber))
                .toList();

        if (all.size() < 30) {
            throw new IllegalStateException("Importe pelo menos 30 concursos antes de executar o backtest.");
        }

        int actualTests = Math.min(Math.max(tests, 5), Math.min(100, all.size() - 20));
        int candidates = Math.min(Math.max(candidatesPerTest, 20), 1000);

        int startIndex = all.size() - actualTests;
        double hitSum = 0;
        int bestHits = -1;
        int bestContest = -1;
        int atLeast3 = 0, atLeast4 = 0, atLeast5 = 0, atLeast6 = 0;

        for (int targetIndex = startIndex; targetIndex < all.size(); targetIndex++) {
            Draw target = all.get(targetIndex);
            List<Draw> historicalWindow = all.subList(0, targetIndex);

            List<Integer> selected = pickBest(type, historicalWindow, candidates);
            int hits = countHits(selected, target.getNumbers());

            hitSum += hits;
            if (hits > bestHits) {
                bestHits = hits;
                bestContest = target.getContestNumber();
            }
            if (hits >= 3) atLeast3++;
            if (hits >= 4) atLeast4++;
            if (hits >= 5) atLeast5++;
            if (hits >= 6) atLeast6++;
        }

        return new BacktestResult(
                type,
                actualTests,
                Math.round((hitSum / actualTests) * 100.0) / 100.0,
                bestHits,
                bestContest,
                atLeast3,
                atLeast4,
                atLeast5,
                atLeast6,
                "Backtest usa somente concursos anteriores ao concurso-alvo. Não demonstra poder preditivo."
        );
    }

    private List<Integer> pickBest(LotteryType type, List<Draw> history, int candidates) {
        List<Integer> best = null;
        double bestScore = -1;

        for (int i = 0; i < candidates; i++) {
            List<Integer> game = randomGame(type);
            double score = historicalScore(type, game, history);
            if (score > bestScore) {
                bestScore = score;
                best = game;
            }
        }
        return best;
    }

    private double historicalScore(LotteryType type, List<Integer> game, List<Draw> history) {
        int even = (int) game.stream().filter(n -> n % 2 == 0).count();
        double idealEven = type == LotteryType.MEGA_SENA ? 3.0 : 7.5;
        double parity = Math.max(0, 100 - Math.abs(even - idealEven) * 18);

        Map<Integer, Long> freq = new HashMap<>();
        for (int n = 1; n <= type.getMaxNumber(); n++) freq.put(n, 0L);
        for (Draw d : history) for (Integer n : d.getNumbers()) freq.put(n, freq.get(n) + 1);

        long max = freq.values().stream().mapToLong(Long::longValue).max().orElse(1);
        long min = freq.values().stream().mapToLong(Long::longValue).min().orElse(0);

        double frequency = 0;
        for (Integer n : game) {
            long f = freq.get(n);
            double normalized = max == min ? 50 : ((double)(f - min) / (max - min)) * 100;
            frequency += 100 - Math.abs(normalized - 65);
        }
        frequency /= game.size();

        return parity * 0.6 + frequency * 0.4;
    }

    private List<Integer> randomGame(LotteryType type) {
        Set<Integer> set = new TreeSet<>();
        while (set.size() < type.getQuantity()) {
            set.add(random.nextInt(type.getMaxNumber()) + 1);
        }
        return new ArrayList<>(set);
    }

    private int countHits(List<Integer> game, List<Integer> result) {
        Set<Integer> target = new HashSet<>(result);
        return (int) game.stream().filter(target::contains).count();
    }
}
