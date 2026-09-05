package br.com.smartloto.service;

import br.com.smartloto.domain.Draw;
import br.com.smartloto.domain.ImportSyncState;
import br.com.smartloto.domain.LotteryType;
import br.com.smartloto.dto.CaixaDrawResponse;
import br.com.smartloto.dto.ImportResponse;
import br.com.smartloto.dto.SyncStatusResponse;
import br.com.smartloto.repository.DrawRepository;
import br.com.smartloto.repository.ImportSyncStateRepository;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class CaixaImportService {
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final int MAX_ATTEMPTS = 3;
    private static final int INITIAL_SYNC_WINDOW = 200;

    private final DrawRepository repository;
    private final ImportSyncStateRepository syncRepository;
    private final RestClient client;

    public CaixaImportService(
            DrawRepository repository,
            ImportSyncStateRepository syncRepository
    ){
        this.repository=repository;
        this.syncRepository=syncRepository;

        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(8_000);
        factory.setReadTimeout(12_000);

        this.client = RestClient.builder()
                .baseUrl("https://servicebus2.caixa.gov.br/portaldeloterias/api")
                .requestFactory(factory)
                .defaultHeader("User-Agent","Mozilla/5.0 SorteLoto/1.2")
                .defaultHeader("Accept","application/json,text/plain,*/*")
                .build();
    }

    @Transactional
    public ImportResponse syncNow(LotteryType type){
        LocalDateTime now=LocalDateTime.now();
        ImportSyncState state=state(type);
        state.attempt(now);
        syncRepository.save(state);

        try{
            CaixaDrawResponse latest=fetchLatest(type);
            validateResponse(type,latest,"concurso atual");

            Integer dbLatest=repository.findTopByLotteryTypeOrderByContestNumberDesc(type)
                    .map(Draw::getContestNumber)
                    .orElse(null);

            int first = dbLatest == null
                    ? Math.max(1, latest.numero()-INITIAL_SYNC_WINDOW+1)
                    : dbLatest+1;

            ImportCounts counts=importRange(type,first,latest.numero());

            Integer newDbLatest=repository.findTopByLotteryTypeOrderByContestNumberDesc(type)
                    .map(Draw::getContestNumber)
                    .orElse(null);

            boolean upToDate=newDbLatest!=null && newDbLatest>=latest.numero();
            String message=upToDate
                    ? "Dados sincronizados com o concurso mais recente da fonte."
                    : "Sincronização concluída, mas ainda existem concursos pendentes.";

            state.success(now,latest.numero(),message);
            syncRepository.save(state);

            return new ImportResponse(
                    type,counts.imported,counts.skipped,counts.failed,
                    latest.numero(),newDbLatest,upToDate,message
            );
        }catch(Exception e){
            state.failure(now,safeMessage(e));
            syncRepository.save(state);
            throw e;
        }
    }

    @Transactional
    public ImportResponse importRecent(LotteryType type,int quantity){
        LocalDateTime now=LocalDateTime.now();
        ImportSyncState state=state(type);
        state.attempt(now);
        syncRepository.save(state);

        try{
            CaixaDrawResponse latest=fetchLatest(type);
            validateResponse(type,latest,"concurso atual");

            int first=Math.max(1,latest.numero()-Math.max(1,quantity)+1);
            ImportCounts counts=importRange(type,first,latest.numero());

            Integer dbLatest=repository.findTopByLotteryTypeOrderByContestNumberDesc(type)
                    .map(Draw::getContestNumber)
                    .orElse(null);

            boolean upToDate=dbLatest!=null && dbLatest>=latest.numero();
            String message=upToDate
                    ? "Importação concluída e base atualizada."
                    : "Importação concluída com pendências.";

            state.success(now,latest.numero(),message);
            syncRepository.save(state);

            return new ImportResponse(
                    type,counts.imported,counts.skipped,counts.failed,
                    latest.numero(),dbLatest,upToDate,message
            );
        }catch(Exception e){
            state.failure(now,safeMessage(e));
            syncRepository.save(state);
            throw e;
        }
    }

    public ImportResponse importMissing(LotteryType type){
        return syncNow(type);
    }

    public SyncStatusResponse status(LotteryType type){
        long count=repository.countByLotteryType(type);
        Draw dbLatest=repository.findTopByLotteryTypeOrderByContestNumberDesc(type).orElse(null);
        ImportSyncState state=syncRepository.findByLotteryType(type).orElse(null);

        Integer dbContest=dbLatest==null?null:dbLatest.getContestNumber();
        LocalDate dbDate=dbLatest==null?null:dbLatest.getDrawDate();
        Integer sourceContest=state==null?null:state.getSourceLatestContest();

        int missing=(sourceContest!=null && dbContest!=null)
                ? Math.max(0,sourceContest-dbContest)
                : 0;

        boolean reachable=state!=null && state.isSourceReachable();
        boolean upToDate=reachable && sourceContest!=null && dbContest!=null && dbContest>=sourceContest;

        String status;
        String message;

        if(state==null){
            status="NUNCA_SINCRONIZADO";
            message="A fonte oficial ainda não foi consultada nesta instalação.";
        }else if(!reachable){
            status="FONTE_INDISPONIVEL";
            message=state.getLastMessage();
        }else if(upToDate){
            status="ATUALIZADO";
            message="Dados atualizados até o concurso "+sourceContest+".";
        }else{
            status="PENDENTE";
            message="Há "+missing+" concurso(s) entre o banco e a última referência da fonte.";
        }

        return new SyncStatusResponse(
                type,count,dbContest,dbDate,sourceContest,missing,
                reachable,upToDate,
                state==null?null:state.getLastAttemptAt(),
                state==null?null:state.getLastSuccessAt(),
                status,message
        );
    }

    private ImportCounts importRange(LotteryType type,int first,int last){
        int imported=0,skipped=0,failed=0;

        if(first>last) return new ImportCounts(0,0,0);

        for(int contest=first;contest<=last;contest++){
            if(repository.existsByLotteryTypeAndContestNumber(type,contest)){
                skipped++;
                continue;
            }

            try{
                CaixaDrawResponse r=fetchContest(type,contest);
                validateResponse(type,r,"concurso "+contest);

                List<Integer> numbers=r.listaDezenas().stream()
                        .map(Integer::parseInt)
                        .sorted()
                        .toList();

                repository.save(new Draw(
                        type,r.numero(),
                        LocalDate.parse(r.dataApuracao(),DATE),
                        numbers
                ));
                imported++;
            }catch(Exception e){
                failed++;
            }
        }

        return new ImportCounts(imported,skipped,failed);
    }

    private CaixaDrawResponse fetchLatest(LotteryType type){
        return fetchWithRetry("/"+gamePath(type));
    }

    private CaixaDrawResponse fetchContest(LotteryType type,int contest){
        return fetchWithRetry("/"+gamePath(type)+"/"+contest);
    }

    private String gamePath(LotteryType type){
        return type==LotteryType.MEGA_SENA?"megasena":"lotofacil";
    }

    private CaixaDrawResponse fetchWithRetry(String path){
        Exception last=null;

        for(int attempt=1;attempt<=MAX_ATTEMPTS;attempt++){
            try{
                CaixaDrawResponse response=client.get()
                        .uri(path)
                        .retrieve()
                        .body(CaixaDrawResponse.class);
                if(response!=null) return response;
            }catch(Exception e){
                last=e;
            }

            try{
                Thread.sleep(350L*attempt);
            }catch(InterruptedException e){
                Thread.currentThread().interrupt();
                throw new IllegalStateException("Importação interrompida.");
            }
        }

        throw new IllegalStateException(
                "A fonte de resultados não respondeu após "+MAX_ATTEMPTS+" tentativas.",
                last
        );
    }

    private void validateResponse(LotteryType type,CaixaDrawResponse r,String label){
        if(r==null || r.numero()==null || r.dataApuracao()==null || r.listaDezenas()==null){
            throw new IllegalStateException("Resposta incompleta para "+label+".");
        }

        if(r.listaDezenas().size()!=type.getQuantity()){
            throw new IllegalStateException("Quantidade de dezenas inválida em "+label+".");
        }

        long unique=r.listaDezenas().stream().distinct().count();
        if(unique!=type.getQuantity()){
            throw new IllegalStateException("Dezenas duplicadas em "+label+".");
        }

        for(String raw:r.listaDezenas()){
            int n=Integer.parseInt(raw);
            if(n<1 || n>type.getMaxNumber()){
                throw new IllegalStateException("Dezena fora da faixa em "+label+".");
            }
        }
    }

    private ImportSyncState state(LotteryType type){
        return syncRepository.findByLotteryType(type)
                .orElseGet(() -> new ImportSyncState(type));
    }

    private String safeMessage(Exception e){
        String m=e.getMessage();
        return m==null || m.isBlank()?"Falha ao consultar a fonte de resultados.":m;
    }

    private record ImportCounts(int imported,int skipped,int failed){}
}
