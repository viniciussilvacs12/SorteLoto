package br.com.smartloto.repository;

import br.com.smartloto.domain.ImportSyncState;
import br.com.smartloto.domain.LotteryType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ImportSyncStateRepository extends JpaRepository<ImportSyncState,Long> {
    Optional<ImportSyncState> findByLotteryType(LotteryType lotteryType);
}
