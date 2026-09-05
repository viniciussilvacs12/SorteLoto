package br.com.smartloto.service;

import br.com.smartloto.domain.*;
import br.com.smartloto.dto.*;
import br.com.smartloto.repository.*;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SavedGameService {
    private final SavedGameRepository repository;
    private final AppUserRepository userRepository;
    private final DrawRepository drawRepository;
    private final SmartAnalysisService smartAnalysisService;

    public SavedGameService(
            SavedGameRepository repository,
            AppUserRepository userRepository,
            DrawRepository drawRepository,
            SmartAnalysisService smartAnalysisService
    ){
        this.repository=repository;
        this.userRepository=userRepository;
        this.drawRepository=drawRepository;
        this.smartAnalysisService=smartAnalysisService;
    }

    public SavedGameResponse save(String email, SaveGameRequest request){
        AppUser user=userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));
        SavedGame game=repository.save(new SavedGame(user,request.lotteryType(),request.numbers()));
        return map(game);
    }

    public List<SavedGameResponse> list(String email){
        return repository.findByUserEmailOrderByCreatedAtDesc(email).stream().map(this::map).toList();
    }

    public List<RankedGameResponse> ranking(String email){
        return repository.findByUserEmailOrderByCreatedAtDesc(email).stream()
                .map(game -> {
                    var analysis=smartAnalysisService.analyze(game.getLotteryType(),game.getNumbers());
                    return new RankedGameResponse(
                            game.getId(),game.getLotteryType(),game.getNumbers(),game.isFavorite(),
                            analysis.smartScore(),analysis.classification()
                    );
                })
                .sorted(Comparator.comparingDouble(RankedGameResponse::smartScore).reversed())
                .toList();
    }


    public void delete(String email, Long id){
        SavedGame game=repository.findByIdAndUserEmail(id,email)
                .orElseThrow(() -> new IllegalArgumentException("Jogo não encontrado."));
        repository.delete(game);
    }

    public SavedGameResponse favorite(String email, Long id, boolean favorite){
        SavedGame game=repository.findByIdAndUserEmail(id,email)
                .orElseThrow(() -> new IllegalArgumentException("Jogo não encontrado."));
        game.setFavorite(favorite);
        return map(repository.save(game));
    }

    public CheckGameResponse check(String email, Long savedGameId){
        SavedGame game=repository.findByIdAndUserEmail(savedGameId,email)
                .orElseThrow(() -> new IllegalArgumentException("Jogo não encontrado."));

        Draw latest=drawRepository.findByLotteryTypeOrderByContestNumberDesc(game.getLotteryType())
                .stream().findFirst()
                .orElseThrow(() -> new IllegalStateException("Nenhum concurso importado para a modalidade."));

        Set<Integer> result=new HashSet<>(latest.getNumbers());
        List<Integer> matched=game.getNumbers().stream().filter(result::contains).sorted().toList();

        return new CheckGameResponse(game.getId(),latest.getContestNumber(),matched.size(),matched);
    }

    private SavedGameResponse map(SavedGame game){
        return new SavedGameResponse(
                game.getId(),game.getLotteryType(),game.getNumbers(),game.isFavorite(),game.getCreatedAt()
        );
    }
}
