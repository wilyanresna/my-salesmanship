import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { MappingSpvComponent } from './mapping-spv/mapping-spv.component';
import { MappingSalesComponent } from './mapping-sales/mapping-sales.component';
import { MappingOutletComponent } from './mapping-outlet/mapping-outlet.component';

const routes: Routes = [
  { path: 'spv', component: MappingSpvComponent },
  { path: 'sales', component: MappingSalesComponent },
  { path: 'outlet', component: MappingOutletComponent }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class MappingRoutingModule { }
