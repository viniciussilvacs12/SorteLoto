import { Component, OnInit, AfterViewInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { LotteryService } from './lottery.service';
import { StatsChartComponent } from './stats-chart.component';
import {
  AnalysisResponse, GameResponse, LotteryType, StatsResponse,
  BacktestResult, StrategyComparisonResponse, AuthResponse,
  SavedGameResponse, CheckGameResponse, ProfileResponse,
  RankedGameResponse, ImportResponse, SyncStatusResponse,
  DrawPageResponse, DrawResponse
} from './models';

declare global { interface Window { google:any; } }

type View='dashboard'|'generator'|'history'|'games'|'lab'|'profile'|'admin';
type SavedFilter='ALL'|'MEGA_SENA'|'LOTOFACIL'|'FAVORITES';

@Component({
  selector:'app-root',
  standalone:true,
  imports:[CommonModule,FormsModule,StatsChartComponent],
  templateUrl:'./app.component.html'
})
export class AppComponent implements OnInit,AfterViewInit {
  selected:LotteryType='MEGA_SENA';
  view:View='dashboard';
  currentGame?:GameResponse;
  analysis?:AnalysisResponse;
  stats?:StatsResponse;
  backtest?:BacktestResult;
  comparison?:StrategyComparisonResponse;

  auth?:AuthResponse;
  profile?:ProfileResponse;
  savedGames:SavedGameResponse[]=[];
  ranking:RankedGameResponse[]=[];
  checks:Record<number,CheckGameResponse>={};
  lastImport?:ImportResponse;
  syncStatus?:SyncStatusResponse;

  history?:DrawPageResponse;
  historyPage=0;
  historySize=12;
  historySearch:number|null=null;
  historyFound?:DrawResponse;
  historyLoading=false;

  savedFilter:SavedFilter='ALL';
  authMode:'login'|'register'='login';
  name=''; email=''; password='';
  importQuantity=200;

  googleEnabled=false;
  googleClientId='';
  loading=false;
  analyticsLoading=false;
  importLoading=false;
  error='';
  message='';

  constructor(private readonly api:LotteryService){}

  ngOnInit(){
    const raw=localStorage.getItem('sorteloto_user') || localStorage.getItem('smartloto_user');
    if(raw){
      try{
        this.auth=JSON.parse(raw);
        localStorage.setItem('sorteloto_user',raw);
        localStorage.removeItem('smartloto_user');
      }catch{}
    }
    this.refreshPublic();
    this.loadGoogleConfig();
    if(this.auth) this.loadPrivateData();
  }

  ngAfterViewInit(){ setTimeout(()=>this.renderGoogleButton(),250); }

  get isAdmin(){ return this.auth?.role==='ADMIN'; }
  get filteredSavedGames(){
    if(this.savedFilter==='FAVORITES') return this.savedGames.filter(g=>g.favorite);
    if(this.savedFilter==='ALL') return this.savedGames;
    return this.savedGames.filter(g=>g.lotteryType===this.savedFilter);
  }
  get latestHistory(){ return this.history?.items?.[0]; }
  get dbHealth(){
    if(!this.syncStatus) return 'Carregando';
    if(this.syncStatus.upToDate) return 'Atualizada';
    if(this.syncStatus.status==='FONTE_INDISPONIVEL') return 'Fonte offline';
    if(this.syncStatus.status==='PENDENTE') return 'Pendente';
    return 'Aguardando sync';
  }

  navigate(v:View){
    this.view=v;
    this.error=''; this.message='';
    if(v==='history') this.loadHistory(0);
    if(v==='dashboard') this.refreshPublic();
    if(v==='games' && this.auth) this.loadPrivateData();
    if(v==='profile' && this.auth) this.loadPrivateData();
    setTimeout(()=>this.renderGoogleButton(),50);
  }

  refreshPublic(){
    this.loadStats();
    this.loadSyncStatus();
    this.loadHistory(0);
  }

  select(t:LotteryType){
    this.selected=t;
    this.currentGame=undefined; this.analysis=undefined; this.historyFound=undefined;
    this.historyPage=0;
    this.refreshPublic();
  }

  submitAuth(){
    this.error=''; this.message='';
    const req=this.authMode==='login'
      ? this.api.login(this.email,this.password)
      : this.api.register(this.name,this.email,this.password);
    req.subscribe({
      next:a=>this.completeAuth(a),
      error:e=>this.error=e?.error?.message || 'Falha na autenticação.'
    });
  }

  completeAuth(a:AuthResponse){
    this.auth=a;
    localStorage.setItem('sorteloto_user',JSON.stringify(a));
    this.loadPrivateData();
    this.message=`Bem-vindo ao SorteLoto, ${a.name}!`;
    this.navigate('dashboard');
  }

  logout(){
    this.auth=undefined; this.profile=undefined; this.savedGames=[]; this.ranking=[];
    localStorage.removeItem('sorteloto_user');
    this.navigate('dashboard');
    setTimeout(()=>this.renderGoogleButton(),100);
  }

  loadGoogleConfig(){
    this.api.googleConfig().subscribe({
      next:c=>{
        this.googleEnabled=c.enabled; this.googleClientId=c.clientId;
        setTimeout(()=>this.renderGoogleButton(),100);
      }
    });
  }

  renderGoogleButton(){
    if(!this.googleEnabled || !this.googleClientId || this.auth) return;
    const host=document.getElementById('googleSignIn');
    if(!host) return;

    const initialize=()=>{
      if(!window.google?.accounts?.id) return;
      host.innerHTML='';
      window.google.accounts.id.initialize({
        client_id:this.googleClientId,
        callback:(response:any)=>this.api.googleLogin(response.credential).subscribe({
          next:a=>this.completeAuth(a),
          error:e=>this.error=e?.error?.message || 'Falha ao entrar com Google.'
        })
      });
      window.google.accounts.id.renderButton(host,{theme:'outline',size:'large',width:320,text:'continue_with',shape:'pill'});
    };

    if(window.google?.accounts?.id){ initialize(); return; }
    if(!document.getElementById('google-gsi-script')){
      const script=document.createElement('script');
      script.id='google-gsi-script'; script.src='https://accounts.google.com/gsi/client';
      script.async=true; script.defer=true; script.onload=initialize;
      document.head.appendChild(script);
    }
  }

  generate(){
    this.loading=true; this.error='';
    const req=this.selected==='MEGA_SENA'?this.api.generateMegaSena():this.api.generateLotofacil();
    req.subscribe({
      next:g=>{
        this.currentGame=g; this.loading=false;
        this.api.analyze(g.lotteryType,g.numbers).subscribe({
          next:a=>this.analysis=a,
          error:e=>this.error=e?.error?.message || 'Falha na análise estatística do jogo.'
        });
      },
      error:()=>{this.error='Falha ao gerar jogo.';this.loading=false;}
    });
  }

  saveCurrent(){
    if(!this.auth || !this.currentGame) return;
    this.api.saveGame(this.currentGame.lotteryType,this.currentGame.numbers).subscribe({
      next:()=>{this.message='Jogo salvo!';this.loadPrivateData();},
      error:()=>this.error='Não foi possível salvar o jogo.'
    });
  }

  deleteGame(game:SavedGameResponse){
    if(!confirm('Excluir este jogo salvo?')) return;
    this.api.deleteGame(game.id).subscribe({
      next:()=>{delete this.checks[game.id];this.message='Jogo excluído.';this.loadPrivateData();},
      error:e=>this.error=e?.error?.message || 'Não foi possível excluir o jogo.'
    });
  }

  loadPrivateData(){
    this.api.profile().subscribe({next:p=>this.profile=p});
    this.api.savedGames().subscribe({next:g=>this.savedGames=g});
    this.api.ranking().subscribe({next:r=>this.ranking=r});
  }

  toggleFavorite(game:SavedGameResponse){
    this.api.favorite(game.id,!game.favorite).subscribe({
      next:()=>this.loadPrivateData(), error:()=>this.error='Falha ao atualizar favorito.'
    });
  }

  check(game:SavedGameResponse){
    this.api.checkGame(game.id).subscribe({
      next:r=>this.checks[game.id]=r,
      error:()=>this.error='Importe resultados antes de conferir.'
    });
  }

  loadHistory(page=this.historyPage){
    this.historyLoading=true;
    this.api.drawHistory(this.selected,page,this.historySize).subscribe({
      next:r=>{this.history=r;this.historyPage=r.page;this.historyLoading=false;},
      error:()=>{this.history=undefined;this.historyLoading=false;}
    });
  }

  searchContest(){
    if(!this.historySearch || this.historySearch<1) return;
    this.historyLoading=true; this.error='';
    this.api.drawByContest(this.selected,this.historySearch).subscribe({
      next:r=>{this.historyFound=r;this.historyLoading=false;},
      error:e=>{this.historyFound=undefined;this.historyLoading=false;this.error=e?.error?.message || 'Concurso não encontrado no banco local.';}
    });
  }

  previousHistory(){ if(this.historyPage>0) this.loadHistory(this.historyPage-1); }
  nextHistory(){ if(this.history && this.historyPage+1<this.history.totalPages) this.loadHistory(this.historyPage+1); }

  runAnalytics(){
    this.analyticsLoading=true; this.error='';
    this.api.backtest(this.selected,30,200).subscribe({
      next:b=>{
        this.backtest=b;
        this.api.compare(this.selected,30,200).subscribe({
          next:c=>{this.comparison=c;this.analyticsLoading=false;},
          error:()=>{this.error='Falha na comparação.';this.analyticsLoading=false;}
        });
      },
      error:()=>{this.error='Importe pelo menos 30 concursos antes do backtest.';this.analyticsLoading=false;}
    });
  }

  importOfficial(missingOnly=false){
    if(!this.isAdmin) return;
    this.importLoading=true; this.error=''; this.message='';
    const req=missingOnly?this.api.importMissing(this.selected):this.api.importDraws(this.selected,this.importQuantity);
    req.subscribe({
      next:r=>{this.lastImport=r;this.importLoading=false;this.message=r.message;this.refreshPublic();},
      error:e=>{this.importLoading=false;this.error=e?.error?.message || 'A fonte de resultados está indisponível.';this.loadSyncStatus();}
    });
  }

  syncNow(){
    if(!this.isAdmin) return;
    this.importLoading=true; this.error=''; this.message='';
    this.api.syncNow(this.selected).subscribe({
      next:r=>{this.lastImport=r;this.importLoading=false;this.message=r.message;this.refreshPublic();},
      error:e=>{this.importLoading=false;this.error=e?.error?.message || 'Não foi possível sincronizar com a fonte.';this.loadSyncStatus();}
    });
  }

  loadSyncStatus(){ this.api.syncStatus(this.selected).subscribe({next:s=>this.syncStatus=s}); }
  loadStats(){ this.api.stats(this.selected).subscribe({next:s=>this.stats=s,error:()=>this.stats=undefined}); }
  label(t:string){return t==='MEGA_SENA'?'Mega-Sena':'Lotofácil';}
}
