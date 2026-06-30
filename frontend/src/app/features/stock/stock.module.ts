import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { StockRoutingModule } from './stock-routing.module';
import { StockListComponent } from './stock-list/stock-list.component';
import { StockFormComponent } from './stock-form/stock-form.component';
import { StockDetailComponent } from './stock-detail/stock-detail.component';
import { SharedModule } from '../../shared/shared.module';

@NgModule({
  declarations: [
    StockListComponent,
    StockFormComponent,
    StockDetailComponent
  ],
  imports: [
    CommonModule,
    StockRoutingModule,
    SharedModule
  ]
})
export class StockModule { }
