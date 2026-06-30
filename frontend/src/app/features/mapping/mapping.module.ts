import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { MappingRoutingModule } from './mapping-routing.module';
import { MappingSpvComponent } from './mapping-spv/mapping-spv.component';
import { MappingSalesComponent } from './mapping-sales/mapping-sales.component';
import { MappingOutletComponent } from './mapping-outlet/mapping-outlet.component';
import { SharedModule } from '../../shared/shared.module';

@NgModule({
  declarations: [
    MappingSpvComponent,
    MappingSalesComponent,
    MappingOutletComponent
  ],
  imports: [
    CommonModule,
    MappingRoutingModule,
    SharedModule
  ]
})
export class MappingModule { }
