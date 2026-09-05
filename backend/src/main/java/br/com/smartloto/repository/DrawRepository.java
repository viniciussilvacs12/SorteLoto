package br.com.smartloto.repository;

import br.com.smartloto.domain.Draw;
import br.com.smartloto.domain.LotteryType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DrawRepository extends JpaRepository<Draw,Long> {
    List<Draw> findByLotteryTypeOrderByContestNumberDesc(LotteryType lotteryType);
    boolean existsByLotteryTypeAndContestNumber(LotteryType lotteryType, Integer contestNumber);
    Optional<Draw> findTopByLotteryTypeOrderByContestNumberDesc(LotteryType lotteryType);
    Optional<Draw> findByLotteryTypeAndContestNumber(LotteryType lotteryType, Integer contestNumber);
    long countByLotteryType(LotteryType lotteryType);
    Page<Draw> findByLotteryType(LotteryType lotteryType, Pageable pageable);
}
