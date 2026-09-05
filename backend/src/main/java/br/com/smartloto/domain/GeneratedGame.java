package br.com.smartloto.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "generated_games")
public class GeneratedGame {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LotteryType lotteryType;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "generated_game_numbers",
            joinColumns = @JoinColumn(name = "game_id")
    )
    @Column(name = "number_value", nullable = false)
    @OrderColumn(name = "number_order")
    private List<Integer> numbers = new ArrayList<>();

    @Column(nullable = false)
    private LocalDateTime createdAt;

    protected GeneratedGame() {
    }

    public GeneratedGame(LotteryType lotteryType, List<Integer> numbers) {
        this.lotteryType = lotteryType;
        this.numbers = numbers;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public LotteryType getLotteryType() {
        return lotteryType;
    }

    public List<Integer> getNumbers() {
        return numbers;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
