package br.com.smartloto.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="import_sync_state", uniqueConstraints=@UniqueConstraint(columnNames="lottery_type"))
public class ImportSyncState {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name="lottery_type", nullable=false, unique=true)
    private LotteryType lotteryType;

    private LocalDateTime lastAttemptAt;
    private LocalDateTime lastSuccessAt;
    private Integer sourceLatestContest;
    private boolean sourceReachable;
    private String lastMessage;

    protected ImportSyncState(){}

    public ImportSyncState(LotteryType lotteryType){
        this.lotteryType=lotteryType;
    }

    public void attempt(LocalDateTime when){
        this.lastAttemptAt=when;
    }

    public void success(LocalDateTime when,Integer sourceLatestContest,String message){
        this.lastAttemptAt=when;
        this.lastSuccessAt=when;
        this.sourceLatestContest=sourceLatestContest;
        this.sourceReachable=true;
        this.lastMessage=message;
    }

    public void failure(LocalDateTime when,String message){
        this.lastAttemptAt=when;
        this.sourceReachable=false;
        this.lastMessage=message;
    }

    public Long getId(){return id;}
    public LotteryType getLotteryType(){return lotteryType;}
    public LocalDateTime getLastAttemptAt(){return lastAttemptAt;}
    public LocalDateTime getLastSuccessAt(){return lastSuccessAt;}
    public Integer getSourceLatestContest(){return sourceLatestContest;}
    public boolean isSourceReachable(){return sourceReachable;}
    public String getLastMessage(){return lastMessage;}
}
