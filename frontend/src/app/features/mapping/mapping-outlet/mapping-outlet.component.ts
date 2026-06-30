import { Component, OnInit } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Outlet, OutletService } from '../../../core/services/outlet.service';
import { Route, RouteService } from '../../../core/services/route.service';
import { MappingOutlet, MappingOutletService } from '../../../core/services/mapping-outlet.service';

@Component({
  selector: 'app-mapping-outlet',
  templateUrl: './mapping-outlet.component.html',
  styleUrls: ['./mapping-outlet.component.scss']
})
export class MappingOutletComponent implements OnInit {
  mappings: MappingOutlet[] = [];
  filteredMappings: MappingOutlet[] = [];
  outlets: Outlet[] = [];
  routes: Route[] = [];
  isLoading = true;

  // Filter
  filterRouteId: number | null = null;

  // Form Fields
  selectedOutletId: number | null = null;
  selectedRouteId: number | null = null;
  weekStart = '';
  weekEnd = '';

  constructor(
    private mappingOutletService: MappingOutletService,
    private outletService: OutletService,
    private routeService: RouteService,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.loadData();
  }

  loadData(): void {
    this.isLoading = true;

    // Load outlets
    this.outletService.list().subscribe({
      next: (outletData) => {
        this.outlets = outletData.filter(o => o.outlet_status === 'ACTIVE');

        // Load Routes
        this.routeService.list().subscribe({
          next: (routeData) => {
            this.routes = routeData;
            this.loadMappings();
          },
          error: () => {
            this.isLoading = false;
            this.snackBar.open('Gagal memuat data route', 'Tutup', { duration: 3000 });
          }
        });
      },
      error: () => {
        this.isLoading = false;
        this.snackBar.open('Gagal memuat data outlet', 'Tutup', { duration: 3000 });
      }
    });
  }

  loadMappings(): void {
    this.mappingOutletService.list().subscribe({
      next: (data) => {
        this.mappings = data;
        this.applyFilter();
        this.isLoading = false;
      },
      error: () => {
        this.isLoading = false;
        this.snackBar.open('Gagal memuat data mapping outlet', 'Tutup', { duration: 3000 });
      }
    });
  }

  applyFilter(): void {
    if (this.filterRouteId) {
      this.filteredMappings = this.mappings.filter(m => m.route_id === this.filterRouteId);
    } else {
      this.filteredMappings = [...this.mappings];
    }
  }

  onWeekSelected(week: { weekStart: string; weekEnd: string }): void {
    this.weekStart = week.weekStart;
    this.weekEnd = week.weekEnd;
  }

  onSubmit(): void {
    if (!this.selectedOutletId || !this.selectedRouteId || !this.weekStart || !this.weekEnd) {
      return;
    }

    const payload: MappingOutlet = {
      outlet_id: this.selectedOutletId,
      route_id: this.selectedRouteId,
      week_start: this.weekStart,
      week_end: this.weekEnd
    };

    this.mappingOutletService.create(payload).subscribe({
      next: () => {
        this.selectedOutletId = null;
        this.selectedRouteId = null;
        this.snackBar.open('Mapping Outlet berhasil ditambahkan', 'Tutup', { duration: 3000 });
        this.loadMappings();
      },
      error: (err) => {
        const msg = err?.error?.error || 'Gagal menambahkan mapping outlet';
        this.snackBar.open(msg, 'Tutup', { duration: 4000 });
      }
    });
  }
}
