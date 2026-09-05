package br.com.smartloto.repository;

import br.com.smartloto.domain.GeneratedGame;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GeneratedGameRepository extends JpaRepository<GeneratedGame, Long> {
}
