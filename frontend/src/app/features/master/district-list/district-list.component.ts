import { Component, OnInit } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Territory, TerritoryService } from '../../../core/services/territory.service';
import { District, DistrictService } from '../../../core/services/district.service';

@Component({
  selector: 'app-district-list',
  templateUrl: './district-list.component.html',
  styleUrls: ['./district-list.component.scss']
})
export class DistrictListComponent implements OnInit {
  districts: District[] = [];
  filteredDistricts: District[] = [];
  territories: Territory[] = [];
  isLoading = true;

  // Filter
  filterTerritoryId: number | null = null;

  // Create Form
  newName = '';
  newTerritoryId: number | null = null;

  // Edit Form
  editingId: number | null = null;
  editName = '';
  editTerritoryId: number | null = null;

  constructor(
    private districtService: DistrictService,
    private territoryService: TerritoryService,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.loadData();
  }

  loadData(): void {
    this.isLoading = true;
    this.territoryService.list().subscribe({
      next: (territoryData) => {
        this.territories = territoryData;
        this.loadDistricts();
      },
      error: () => {
        this.isLoading = false;
        this.snackBar.open('Gagal memuat data territory', 'Tutup', { duration: 3000 });
      }
    });
  }

  loadDistricts(): void {
    this.districtService.list().subscribe({
      next: (data) => {
        this.districts = data;
        this.applyFilter();
        this.isLoading = false;
      },
      error: () => {
        this.isLoading = false;
        this.snackBar.open('Gagal memuat data district', 'Tutup', { duration: 3000 });
      }
    });
  }

  applyFilter(): void {
    if (this.filterTerritoryId) {
      this.filteredDistricts = this.districts.filter(d => d.territory_id === this.filterTerritoryId);
    } else {
      this.filteredDistricts = [...this.districts];
    }
  }

  onCreate(): void {
    if (!this.newName.trim() || !this.newTerritoryId) return;

    this.districtService.create({ name: this.newName, territory_id: this.newTerritoryId }).subscribe({
      next: () => {
        this.newName = '';
        this.newTerritoryId = null;
        this.snackBar.open('District berhasil ditambahkan', 'Tutup', { duration: 3000 });
        this.loadDistricts();
      },
      error: (err) => {
        const msg = err?.error?.error || 'Gagal menambahkan district';
        this.snackBar.open(msg, 'Tutup', { duration: 3000 });
      }
    });
  }

  onStartEdit(d: District): void {
    if (d.id) {
      this.editingId = d.id;
      this.editName = d.name;
      this.editTerritoryId = d.territory_id;
    }
  }

  onCancelEdit(): void {
    this.editingId = null;
    this.editName = '';
    this.editTerritoryId = null;
  }

  onSaveEdit(id: number): void {
    if (!this.editName.trim() || !this.editTerritoryId) return;

    this.districtService.update(id, { name: this.editName, territory_id: this.editTerritoryId }).subscribe({
      next: () => {
        this.editingId = null;
        this.editName = '';
        this.editTerritoryId = null;
        this.snackBar.open('District berhasil diperbarui', 'Tutup', { duration: 3000 });
        this.loadDistricts();
      },
      error: (err) => {
        const msg = err?.error?.error || 'Gagal memperbarui district';
        this.snackBar.open(msg, 'Tutup', { duration: 3000 });
      }
    });
  }

  onDelete(id: number): void {
    if (confirm('Apakah Anda yakin ingin menghapus district ini?')) {
      this.districtService.delete(id).subscribe({
        next: () => {
          this.snackBar.open('District berhasil dihapus', 'Tutup', { duration: 3000 });
          this.loadDistricts();
        },
        error: (err) => {
          const msg = err?.error?.error || 'Gagal menghapus district';
          this.snackBar.open(msg, 'Tutup', { duration: 3000 });
        }
      });
    }
  }
}
