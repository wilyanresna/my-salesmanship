import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { TargetRoutingModule } from './target-routing.module';
import { TargetListComponent } from './target-list/target-list.component';


@NgModule({
  declarations: [
    TargetListComponent
  ],
  imports: [
    CommonModule,
    TargetRoutingModule
  ]
})
export class TargetModule { }
