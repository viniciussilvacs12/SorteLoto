import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  AnalysisResponse, GameResponse, LotteryType, StatsResponse,
  BacktestResult, StrategyComparisonResponse, AuthResponse,
  SavedGameResponse, CheckGameResponse, ProfileResponse,
  RankedGameResponse, ImportResponse, GoogleConfigResponse, SyncStatusResponse, DrawPageResponse, DrawResponse
} from './models';

@Injectable({providedIn:'root'})
export class LotteryService {
  private readonly baseUrl='/api';

  constructor(private readonly http:HttpClient){}

  generateMegaSena(){return this.http.get<GameResponse>(`${this.baseUrl}/games/mega-sena`);}
  generateLotofacil(){return this.http.get<GameResponse>(`${this.baseUrl}/games/lotofacil`);}
  analyze(lotteryType:LotteryType,numbers:number[]){return this.http.post<AnalysisResponse>(`${this.baseUrl}/analysis`,{lotteryType,numbers});}
  stats(type:LotteryType){return this.http.get<StatsResponse>(`${this.baseUrl}/stats/${type}`);}
  backtest(type:LotteryType,tests=30,candidates=200){return this.http.get<BacktestResult>(`${this.baseUrl}/backtest/${type}?tests=${tests}&candidates=${candidates}`);}
  compare(type:LotteryType,tests=30,candidates=200){return this.http.get<StrategyComparisonResponse>(`${this.baseUrl}/strategy-comparison/${type}?tests=${tests}&candidates=${candidates}`);}

  register(name:string,email:string,password:string){return this.http.post<AuthResponse>(`${this.baseUrl}/auth/register`,{name,email,password});}
  login(email:string,password:string){return this.http.post<AuthResponse>(`${this.baseUrl}/auth/login`,{email,password});}
  googleConfig(){return this.http.get<GoogleConfigResponse>(`${this.baseUrl}/auth/google/config`);}
  googleLogin(credential:string){return this.http.post<AuthResponse>(`${this.baseUrl}/auth/google`,{credential});}

  profile(){return this.http.get<ProfileResponse>(`${this.baseUrl}/profile/me`);}
  saveGame(lotteryType:LotteryType,numbers:number[]){return this.http.post<SavedGameResponse>(`${this.baseUrl}/saved-games`,{lotteryType,numbers});}
  savedGames(){return this.http.get<SavedGameResponse[]>(`${this.baseUrl}/saved-games`);}
  ranking(){return this.http.get<RankedGameResponse[]>(`${this.baseUrl}/saved-games/ranking`);}
  favorite(id:number,value:boolean){return this.http.patch<SavedGameResponse>(`${this.baseUrl}/saved-games/${id}/favorite?value=${value}`,{});}
  checkGame(id:number){return this.http.get<CheckGameResponse>(`${this.baseUrl}/saved-games/${id}/check`);}
  deleteGame(id:number){return this.http.delete<void>(`${this.baseUrl}/saved-games/${id}`);}
  importDraws(type:LotteryType,quantity=200){return this.http.post<ImportResponse>(`${this.baseUrl}/import/${type}?quantity=${quantity}`,{});}
  importMissing(type:LotteryType){return this.http.post<ImportResponse>(`${this.baseUrl}/import/${type}/missing`,{});}
  syncNow(type:LotteryType){return this.http.post<ImportResponse>(`${this.baseUrl}/import/${type}/sync`,{});}
  syncStatus(type:LotteryType){return this.http.get<SyncStatusResponse>(`${this.baseUrl}/sync-status/${type}`);}
  drawHistory(type:LotteryType,page=0,size=20){return this.http.get<DrawPageResponse>(`${this.baseUrl}/draws/${type}?page=${page}&size=${size}`);}
  drawByContest(type:LotteryType,contest:number){return this.http.get<DrawResponse>(`${this.baseUrl}/draws/${type}/${contest}`);}
}
