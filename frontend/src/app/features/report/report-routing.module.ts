import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { VisitReportComponent } from './visit-report/visit-report.component';
import { SalesReportComponent } from './sales-report/sales-report.component';
import { AchievementReportComponent } from './achievement-report/achievement-report.component';

const routes: Routes = [
  { path: 'visits', component: VisitReportComponent },
  { path: 'sales', component: SalesReportComponent },
  { path: 'achievement', component: AchievementReportComponent }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ReportRoutingModule { }
