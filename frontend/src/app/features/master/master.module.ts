import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { MasterRoutingModule } from './master-routing.module';
import { EmployeeListComponent } from './employee-list/employee-list.component';
import { OutletListComponent } from './outlet-list/outlet-list.component';
import { AreaListComponent } from './area-list/area-list.component';
import { TerritoryListComponent } from './territory-list/territory-list.component';
import { DistrictListComponent } from './district-list/district-list.component';
import { RouteListComponent } from './route-list/route-list.component';
import { ProductListComponent } from './product-list/product-list.component';
import { ParamListComponent } from './param-list/param-list.component';
import { EmployeeFormComponent } from './employee-form/employee-form.component';
import { OutletFormComponent } from './outlet-form/outlet-form.component';
import { SharedModule } from '../../shared/shared.module';

@NgModule({
  declarations: [
    EmployeeListComponent,
    OutletListComponent,
    AreaListComponent,
    TerritoryListComponent,
    DistrictListComponent,
    RouteListComponent,
    ProductListComponent,
    ParamListComponent,
    EmployeeFormComponent,
    OutletFormComponent
  ],
  imports: [
    CommonModule,
    MasterRoutingModule,
    SharedModule
  ]
})
export class MasterModule { }
