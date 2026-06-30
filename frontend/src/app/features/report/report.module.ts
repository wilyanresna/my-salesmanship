import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { ReportRoutingModule } from './report-routing.module';
import { VisitReportComponent } from './visit-report/visit-report.component';
import { SalesReportComponent } from './sales-report/sales-report.component';
import { AchievementReportComponent } from './achievement-report/achievement-report.component';


@NgModule({
  declarations: [
    VisitReportComponent,
    SalesReportComponent,
    AchievementReportComponent
  ],
  imports: [
    CommonModule,
    ReportRoutingModule
  ]
})
export class ReportModule { }
