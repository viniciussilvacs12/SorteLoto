package br.com.smartloto.service;

import br.com.smartloto.domain.LotteryType;
import br.com.smartloto.dto.SmartAnalysisResponse;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.*;

@Service
public class SmartGeneratorService {

    private final SmartAnalysisService smartAnalysisService;
    private final SecureRandom random = new SecureRandom();

    public SmartGeneratorService(SmartAnalysisService smartAnalysisService) {
        this.smartAnalysisService = smartAnalysisService;
    }

    public SmartAnalysisResponse generateBest(LotteryType type, int candidates) {
        SmartAnalysisResponse best = null;

        for (int i = 0; i < Math.min(Math.max(candidates, 10), 5000); i++) {
            Set<Integer> set = new TreeSet<>();
            while (set.size() < type.getQuantity()) {
                set.add(random.nextInt(type.getMaxNumber()) + 1);
            }

            SmartAnalysisResponse candidate =
                    smartAnalysisService.analyze(type, new ArrayList<>(set));

            if (best == null || candidate.smartScore() > best.smartScore()) {
                best = candidate;
            }
        }

        return best;
    }
}
