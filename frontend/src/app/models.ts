export type LotteryType = 'MEGA_SENA' | 'LOTOFACIL';
export interface GameResponse { id:number; lotteryType:LotteryType; numbers:number[]; createdAt:string; }
export interface AnalysisResponse { lotteryType:LotteryType; numbers:number[]; evenCount:number; oddCount:number; sum:number; consecutivePairs:number; balanceScore:number; classification:string; message:string; }
export interface NumberStat { number:number; frequency:number; delay:number; }
export interface StatsResponse { lotteryType:LotteryType; contestsAnalyzed:number; hottest:NumberStat[]; coldest:NumberStat[]; mostDelayed:NumberStat[]; dataSource:string; }

export interface BacktestResult {
  lotteryType: LotteryType;
  tests: number;
  averageHits: number;
  bestHits: number;
  bestContest: number;
  hitsAtLeast3: number;
  hitsAtLeast4: number;
  hitsAtLeast5: number;
  hitsAtLeast6: number;
  note: string;
}

export interface StrategyComparisonResponse {
  lotteryType: LotteryType;
  tests: number;
  smartAverageHits: number;
  randomAverageHits: number;
  smartBestHits: number;
  randomBestHits: number;
  smartAdvantage: number;
  verdict: string;
}

export interface AuthResponse {
  userId:number;
  name:string;
  email:string;
  role:string;
  token:string;
}

export interface SavedGameResponse {
  id:number;
  lotteryType:LotteryType;
  numbers:number[];
  favorite:boolean;
  createdAt:string;
}

export interface CheckGameResponse {
  savedGameId:number;
  contestNumber:number;
  hits:number;
  matchedNumbers:number[];
}

export interface ProfileResponse {
  id:number;
  name:string;
  email:string;
  role:string;
  savedGames:number;
  favorites:number;
}

export interface RankedGameResponse {
  id:number;
  lotteryType:LotteryType;
  numbers:number[];
  favorite:boolean;
  smartScore:number;
  classification:string;
}

export interface ImportResponse {
  lotteryType:LotteryType;
  imported:number;
  skipped:number;
  failed:number;
  sourceLatestContest:number|null;
  databaseLatestContest:number|null;
  upToDate:boolean;
  message:string;
}

export interface GoogleConfigResponse {
  enabled:boolean;
  clientId:string;
}

export interface SyncStatusResponse {
  lotteryType:LotteryType;
  contestsInDatabase:number;
  databaseLatestContest:number|null;
  databaseLatestDrawDate:string|null;
  sourceLatestContest:number|null;
  missingContests:number;
  sourceReachable:boolean;
  upToDate:boolean;
  lastAttemptAt:string|null;
  lastSuccessAt:string|null;
  status:'NUNCA_SINCRONIZADO'|'FONTE_INDISPONIVEL'|'ATUALIZADO'|'PENDENTE';
  message:string;
}

export interface DrawResponse {
  id:number;
  lotteryType:LotteryType;
  contestNumber:number;
  drawDate:string;
  numbers:number[];
}

export interface DrawPageResponse {
  lotteryType:LotteryType;
  items:DrawResponse[];
  total:number;
  page:number;
  pageSize:number;
  totalPages:number;
}
