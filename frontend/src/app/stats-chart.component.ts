import {
  AfterViewInit, Component, ElementRef, Input, OnChanges, OnDestroy,
  SimpleChanges, ViewChild
} from '@angular/core';
import { Chart, registerables } from 'chart.js';
import { StatsResponse } from './models';

Chart.register(...registerables);

@Component({
  selector:'app-stats-chart',
  standalone:true,
  template:`<div class="chart-wrap"><canvas #canvas></canvas></div>`,
  styles:[`.chart-wrap{position:relative;height:320px;width:100%;}`]
})
export class StatsChartComponent implements AfterViewInit,OnChanges,OnDestroy {
  @Input() stats?:StatsResponse;
  @ViewChild('canvas') canvas?:ElementRef<HTMLCanvasElement>;
  private chart?:Chart;

  ngAfterViewInit(){ this.render(); }

  ngOnChanges(changes:SimpleChanges){
    if(changes['stats']) setTimeout(()=>this.render());
  }

  ngOnDestroy(){ this.chart?.destroy(); }

  private render(){
    if(!this.canvas || !this.stats) return;
    this.chart?.destroy();

    const items=this.stats.hottest.slice(0,10);
    this.chart=new Chart(this.canvas.nativeElement,{
      type:'bar',
      data:{
        labels:items.map(x=>String(x.number).padStart(2,'0')),
        datasets:[{
          label:'Frequência',
          data:items.map(x=>x.frequency)
        }]
      },
      options:{
        responsive:true,
        maintainAspectRatio:false,
        plugins:{legend:{display:false}},
        scales:{
          x:{ticks:{color:'#cfe4d3'},grid:{display:false}},
          y:{ticks:{color:'#91a498'},grid:{color:'rgba(255,255,255,.06)'}}
        }
      }
    });
  }
}
