package br.com.smartloto.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="saved_games")
public class SavedGame {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional=false, fetch=FetchType.LAZY)
    private AppUser user;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private LotteryType lotteryType;

    @ElementCollection(fetch=FetchType.EAGER)
    @CollectionTable(name="saved_game_numbers", joinColumns=@JoinColumn(name="saved_game_id"))
    @Column(name="number_value")
    @OrderColumn(name="number_order")
    private List<Integer> numbers = new ArrayList<>();

    @Column(nullable=false)
    private boolean favorite;

    @Column(nullable=false)
    private LocalDateTime createdAt = LocalDateTime.now();

    protected SavedGame(){}

    public SavedGame(AppUser user, LotteryType lotteryType, List<Integer> numbers){
        this.user=user;
        this.lotteryType=lotteryType;
        this.numbers=new ArrayList<>(numbers);
    }

    public Long getId(){return id;}
    public AppUser getUser(){return user;}
    public LotteryType getLotteryType(){return lotteryType;}
    public List<Integer> getNumbers(){return numbers;}
    public boolean isFavorite(){return favorite;}
    public LocalDateTime getCreatedAt(){return createdAt;}
    public void setFavorite(boolean favorite){this.favorite=favorite;}
}
