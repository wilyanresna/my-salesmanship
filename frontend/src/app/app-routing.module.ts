import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AppShellComponent } from './layout/app-shell/app-shell.component';
import { authGuard } from './core/guards/auth.guard';
import { roleGuard } from './core/guards/role.guard';

const routes: Routes = [
  {
    path: 'login',
    loadChildren: () => import('./features/auth/auth.module').then(m => m.AuthModule)
  },
  {
    path: '',
    component: AppShellComponent,
    canActivate: [authGuard, roleGuard],
    children: [
      {
        path: '',
        redirectTo: 'dashboard',
        pathMatch: 'full'
      },
      {
        path: 'dashboard',
        loadChildren: () => import('./features/dashboard/dashboard.module').then(m => m.DashboardModule)
      },
      {
        path: 'master',
        loadChildren: () => import('./features/master/master.module').then(m => m.MasterModule)
      },
      {
        path: 'mapping',
        loadChildren: () => import('./features/mapping/mapping.module').then(m => m.MappingModule)
      },
      {
        path: 'stock',
        loadChildren: () => import('./features/stock/stock.module').then(m => m.StockModule)
      },
      {
        path: 'target',
        loadChildren: () => import('./features/target/target.module').then(m => m.TargetModule)
      },
      {
        path: 'reports',
        loadChildren: () => import('./features/report/report.module').then(m => m.ReportModule)
      }
    ]
  },
  {
    path: '**',
    redirectTo: 'dashboard'
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
