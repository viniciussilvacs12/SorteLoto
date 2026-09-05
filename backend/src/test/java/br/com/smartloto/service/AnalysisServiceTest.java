package br.com.smartloto.service;

import br.com.smartloto.domain.LotteryType;
import br.com.smartloto.dto.AnalysisRequest;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AnalysisServiceTest {

    private final AnalysisService service = new AnalysisService();

    @Test
    void shouldAnalyzeMegaSenaGame() {
        var response = service.analyze(
                new AnalysisRequest(
                        LotteryType.MEGA_SENA,
                        List.of(4, 12, 23, 34, 47, 59)
                )
        );

        assertEquals(6, response.numbers().size());
        assertTrue(response.balanceScore() >= 0);
        assertTrue(response.balanceScore() <= 100);
    }

    @Test
    void shouldRejectDuplicatedNumbers() {
        assertThrows(IllegalArgumentException.class, () ->
                service.analyze(
                        new AnalysisRequest(
                                LotteryType.MEGA_SENA,
                                List.of(4, 4, 23, 34, 47, 59)
                        )
                )
        );
    }
}
