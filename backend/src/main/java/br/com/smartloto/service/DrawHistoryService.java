package br.com.smartloto.service;

import br.com.smartloto.domain.Draw;
import br.com.smartloto.domain.LotteryType;
import br.com.smartloto.dto.DrawPageResponse;
import br.com.smartloto.dto.DrawResponse;
import br.com.smartloto.repository.DrawRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DrawHistoryService {
    private final DrawRepository repository;

    public DrawHistoryService(DrawRepository repository){
        this.repository=repository;
    }

    public DrawPageResponse list(LotteryType type,int page,int size){
        int safePage=Math.max(page,0);
        int safeSize=Math.min(Math.max(size,1),100);
        PageRequest request=PageRequest.of(
                safePage,
                safeSize,
                Sort.by(Sort.Direction.DESC,"contestNumber")
        );
        Page<Draw> result=repository.findByLotteryType(type,request);
        List<DrawResponse> items=result.getContent().stream().map(this::toResponse).toList();
        return new DrawPageResponse(
                type,items,result.getTotalElements(),safePage,safeSize,result.getTotalPages()
        );
    }

    public DrawResponse find(LotteryType type,int contest){
        Draw draw=repository.findByLotteryTypeAndContestNumber(type,contest)
                .orElseThrow(() -> new IllegalArgumentException("Concurso não encontrado no banco local."));
        return toResponse(draw);
    }

    private DrawResponse toResponse(Draw draw){
        return new DrawResponse(
                draw.getId(),draw.getLotteryType(),draw.getContestNumber(),draw.getDrawDate(),draw.getNumbers()
        );
    }
}
