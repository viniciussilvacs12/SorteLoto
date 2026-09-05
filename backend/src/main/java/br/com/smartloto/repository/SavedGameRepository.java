package br.com.smartloto.repository;

import br.com.smartloto.domain.SavedGame;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface SavedGameRepository extends JpaRepository<SavedGame,Long> {
    List<SavedGame> findByUserEmailOrderByCreatedAtDesc(String email);
    Optional<SavedGame> findByIdAndUserEmail(Long id, String email);
    long countByUserEmail(String email);
    long countByUserEmailAndFavoriteTrue(String email);
}
