package br.com.smartloto.service;

import br.com.smartloto.domain.GeneratedGame;
import br.com.smartloto.domain.LotteryType;
import br.com.smartloto.dto.GameResponse;
import br.com.smartloto.repository.GeneratedGameRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

@Service
public class LotteryService {

    private final SecureRandom random = new SecureRandom();
    private final GeneratedGameRepository repository;

    public LotteryService(GeneratedGameRepository repository) {
        this.repository = repository;
    }

    public GameResponse generate(LotteryType type) {
        List<Integer> numbers = generateNumbers(type.getQuantity(), type.getMaxNumber());
        GeneratedGame game = repository.save(new GeneratedGame(type, numbers));
        return toResponse(game);
    }

    public List<GameResponse> history() {
        return repository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"))
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private List<Integer> generateNumbers(int quantity, int maxNumber) {
        Set<Integer> numbers = new TreeSet<>();

        while (numbers.size() < quantity) {
            numbers.add(random.nextInt(maxNumber) + 1);
        }

        return new ArrayList<>(numbers);
    }

    private GameResponse toResponse(GeneratedGame game) {
        return new GameResponse(
                game.getId(),
                game.getLotteryType(),
                game.getNumbers(),
                game.getCreatedAt()
        );
    }
}
