package br.com.smartloto.service;

import br.com.smartloto.domain.Draw;
import br.com.smartloto.domain.LotteryType;
import br.com.smartloto.dto.NumberStat;
import br.com.smartloto.dto.StatsResponse;
import br.com.smartloto.repository.DrawRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class StatisticsService {
    private final DrawRepository repository;

    public StatisticsService(DrawRepository repository){
        this.repository=repository;
    }

    public StatsResponse stats(LotteryType type){
        List<Draw> draws=repository.findByLotteryTypeOrderByContestNumberDesc(type);

        if(draws.isEmpty()){
            return demoStats(type);
        }

        Map<Integer,Long> freq=new HashMap<>();
        Map<Integer,Integer> delay=new HashMap<>();

        for(int n=1;n<=type.getMaxNumber();n++){
            freq.put(n,0L);
            delay.put(n,draws.size());
        }

        for(Draw d:draws){
            for(Integer n:d.getNumbers()){
                freq.put(n,freq.getOrDefault(n,0L)+1);
            }
        }

        for(int n=1;n<=type.getMaxNumber();n++){
            for(int i=0;i<draws.size();i++){
                if(draws.get(i).getNumbers().contains(n)){
                    delay.put(n,i);
                    break;
                }
            }
        }

        List<NumberStat> all=new ArrayList<>();
        for(int n=1;n<=type.getMaxNumber();n++){
            all.add(new NumberStat(n,freq.get(n),delay.get(n)));
        }

        return build(type,draws.size(),all,"BANCO_IMPORTADO");
    }

    private StatsResponse demoStats(LotteryType type){
        // Dados sintéticos determinísticos SOMENTE para permitir testar o dashboard
        // antes da primeira importação. Não são resultados oficiais.
        Random random = new Random(type == LotteryType.MEGA_SENA ? 605060L : 152515L);
        int simulatedContests = type == LotteryType.MEGA_SENA ? 120 : 120;
        int max = type.getMaxNumber();
        int qty = type.getQuantity();

        long[] freq = new long[max + 1];
        int[] delay = new int[max + 1];
        Arrays.fill(delay, simulatedContests);

        List<Set<Integer>> history = new ArrayList<>();
        for(int c=0;c<simulatedContests;c++){
            Set<Integer> draw = new HashSet<>();
            while(draw.size()<qty){
                draw.add(1+random.nextInt(max));
            }
            history.add(draw);
            for(int n:draw) freq[n]++;
        }

        for(int n=1;n<=max;n++){
            for(int i=history.size()-1, d=0;i>=0;i--,d++){
                if(history.get(i).contains(n)){
                    delay[n]=d;
                    break;
                }
            }
        }

        List<NumberStat> all=new ArrayList<>();
        for(int n=1;n<=max;n++){
            all.add(new NumberStat(n,freq[n],delay[n]));
        }

        return build(type,simulatedContests,all,"DEMO_SIMULADO");
    }

    private StatsResponse build(
            LotteryType type,
            long contests,
            List<NumberStat> all,
            String source
    ){
        List<NumberStat> hot=all.stream()
                .sorted(Comparator.comparingLong(NumberStat::frequency).reversed()
                        .thenComparingInt(NumberStat::number))
                .limit(10)
                .toList();

        List<NumberStat> cold=all.stream()
                .sorted(Comparator.comparingLong(NumberStat::frequency)
                        .thenComparingInt(NumberStat::number))
                .limit(10)
                .toList();

        List<NumberStat> delayed=all.stream()
                .sorted(Comparator.comparingInt(NumberStat::delay).reversed()
                        .thenComparingInt(NumberStat::number))
                .limit(10)
                .toList();

        return new StatsResponse(type,contests,hot,cold,delayed,source);
    }
}
