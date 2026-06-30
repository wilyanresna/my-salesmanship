import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { EmployeeListComponent } from './employee-list/employee-list.component';
import { OutletListComponent } from './outlet-list/outlet-list.component';
import { AreaListComponent } from './area-list/area-list.component';
import { TerritoryListComponent } from './territory-list/territory-list.component';
import { DistrictListComponent } from './district-list/district-list.component';
import { RouteListComponent } from './route-list/route-list.component';
import { ProductListComponent } from './product-list/product-list.component';
import { ParamListComponent } from './param-list/param-list.component';

const routes: Routes = [
  { path: 'employees', component: EmployeeListComponent },
  { path: 'outlets', component: OutletListComponent },
  { path: 'areas', component: AreaListComponent },
  { path: 'territories', component: TerritoryListComponent },
  { path: 'districts', component: DistrictListComponent },
  { path: 'routes', component: RouteListComponent },
  { path: 'products', component: ProductListComponent },
  { path: 'params', component: ParamListComponent }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class MasterRoutingModule { }
