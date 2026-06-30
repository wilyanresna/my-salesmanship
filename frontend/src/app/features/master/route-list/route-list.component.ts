import { Component, OnInit } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { District, DistrictService } from '../../../core/services/district.service';
import { Route, RouteService } from '../../../core/services/route.service';

@Component({
  selector: 'app-route-list',
  templateUrl: './route-list.component.html',
  styleUrls: ['./route-list.component.scss']
})
export class RouteListComponent implements OnInit {
  routes: Route[] = [];
  filteredRoutes: Route[] = [];
  districts: District[] = [];
  isLoading = true;

  // Days list for mapping
  daysList = [
    { value: 1, label: 'Senin' },
    { value: 2, label: 'Selasa' },
    { value: 3, label: 'Rabu' },
    { value: 4, label: 'Kamis' },
    { value: 5, label: 'Jumat' },
    { value: 6, label: 'Sabtu' },
    { value: 7, label: 'Minggu' }
  ];

  // Filter
  filterDistrictId: number | null = null;

  // Create Form
  newName = '';
  newDistrictId: number | null = null;
  newDayOfWeek = 1;

  // Edit Form
  editingId: number | null = null;
  editName = '';
  editDistrictId: number | null = null;
  editDayOfWeek = 1;

  constructor(
    private routeService: RouteService,
    private districtService: DistrictService,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.loadData();
  }

  loadData(): void {
    this.isLoading = true;
    this.districtService.list().subscribe({
      next: (districtData) => {
        this.districts = districtData;
        this.loadRoutes();
      },
      error: () => {
        this.isLoading = false;
        this.snackBar.open('Gagal memuat data district', 'Tutup', { duration: 3000 });
      }
    });
  }

  loadRoutes(): void {
    this.routeService.list().subscribe({
      next: (data) => {
        this.routes = data;
        this.applyFilter();
        this.isLoading = false;
      },
      error: () => {
        this.isLoading = false;
        this.snackBar.open('Gagal memuat data route', 'Tutup', { duration: 3000 });
      }
    });
  }

  applyFilter(): void {
    if (this.filterDistrictId) {
      this.filteredRoutes = this.routes.filter(r => r.district_id === this.filterDistrictId);
    } else {
      this.filteredRoutes = [...this.routes];
    }
  }

  getDayLabel(dayNum: number): string {
    const day = this.daysList.find(d => d.value === dayNum);
    return day ? day.label : 'Unknown';
  }

  onCreate(): void {
    if (!this.newName.trim() || !this.newDistrictId || !this.newDayOfWeek) return;

    this.routeService.create({ 
      name: this.newName, 
      district_id: this.newDistrictId,
      day_of_week: this.newDayOfWeek 
    }).subscribe({
      next: () => {
        this.newName = '';
        this.newDistrictId = null;
        this.newDayOfWeek = 1;
        this.snackBar.open('Route berhasil ditambahkan', 'Tutup', { duration: 3000 });
        this.loadRoutes();
      },
      error: (err) => {
        const msg = err?.error?.error || 'Gagal menambahkan route';
        this.snackBar.open(msg, 'Tutup', { duration: 3000 });
      }
    });
  }

  onStartEdit(r: Route): void {
    if (r.id) {
      this.editingId = r.id;
      this.editName = r.name;
      this.editDistrictId = r.district_id;
      this.editDayOfWeek = r.day_of_week;
    }
  }

  onCancelEdit(): void {
    this.editingId = null;
    this.editName = '';
    this.editDistrictId = null;
    this.editDayOfWeek = 1;
  }

  onSaveEdit(id: number): void {
    if (!this.editName.trim() || !this.editDistrictId || !this.editDayOfWeek) return;

    this.routeService.update(id, { 
      name: this.editName, 
      district_id: this.editDistrictId,
      day_of_week: this.editDayOfWeek
    }).subscribe({
      next: () => {
        this.editingId = null;
        this.editName = '';
        this.editDistrictId = null;
        this.editDayOfWeek = 1;
        this.snackBar.open('Route berhasil diperbarui', 'Tutup', { duration: 3000 });
        this.loadRoutes();
      },
      error: (err) => {
        const msg = err?.error?.error || 'Gagal memperbarui route';
        this.snackBar.open(msg, 'Tutup', { duration: 3000 });
      }
    });
  }

  onDelete(id: number): void {
    if (confirm('Apakah Anda yakin ingin menghapus route ini?')) {
      this.routeService.delete(id).subscribe({
        next: () => {
          this.snackBar.open('Route berhasil dihapus', 'Tutup', { duration: 3000 });
          this.loadRoutes();
        },
        error: (err) => {
          const msg = err?.error?.error || 'Gagal menghapus route';
          this.snackBar.open(msg, 'Tutup', { duration: 3000 });
        }
      });
    }
  }
}
