package br.com.smartloto.domain;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="draws", uniqueConstraints=@UniqueConstraint(columnNames={"lottery_type","contest_number"}))
public class Draw {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name="lottery_type", nullable=false)
    private LotteryType lotteryType;

    @Column(name="contest_number", nullable=false)
    private Integer contestNumber;

    private LocalDate drawDate;

    @ElementCollection(fetch=FetchType.EAGER)
    @CollectionTable(name="draw_numbers", joinColumns=@JoinColumn(name="draw_id"))
    @Column(name="number_value")
    @OrderColumn(name="number_order")
    private List<Integer> numbers = new ArrayList<>();

    protected Draw(){}

    public Draw(LotteryType lotteryType, Integer contestNumber, LocalDate drawDate, List<Integer> numbers){
        this.lotteryType=lotteryType; this.contestNumber=contestNumber; this.drawDate=drawDate; this.numbers=numbers;
    }

    public Long getId(){return id;}
    public LotteryType getLotteryType(){return lotteryType;}
    public Integer getContestNumber(){return contestNumber;}
    public LocalDate getDrawDate(){return drawDate;}
    public List<Integer> getNumbers(){return numbers;}
}
