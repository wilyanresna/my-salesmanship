import { Component, OnInit } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Area, AreaService } from '../../../core/services/area.service';
import { Territory, TerritoryService } from '../../../core/services/territory.service';

@Component({
  selector: 'app-territory-list',
  templateUrl: './territory-list.component.html',
  styleUrls: ['./territory-list.component.scss']
})
export class TerritoryListComponent implements OnInit {
  territories: Territory[] = [];
  filteredTerritories: Territory[] = [];
  areas: Area[] = [];
  isLoading = true;

  // Filter
  filterAreaId: number | null = null;

  // Create Form
  newName = '';
  newAreaId: number | null = null;

  // Edit Form
  editingId: number | null = null;
  editName = '';
  editAreaId: number | null = null;

  constructor(
    private territoryService: TerritoryService,
    private areaService: AreaService,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.loadData();
  }

  loadData(): void {
    this.isLoading = true;
    this.areaService.list().subscribe({
      next: (areaData) => {
        this.areas = areaData;
        this.loadTerritories();
      },
      error: () => {
        this.isLoading = false;
        this.snackBar.open('Gagal memuat data area', 'Tutup', { duration: 3000 });
      }
    });
  }

  loadTerritories(): void {
    this.territoryService.list().subscribe({
      next: (data) => {
        this.territories = data;
        this.applyFilter();
        this.isLoading = false;
      },
      error: () => {
        this.isLoading = false;
        this.snackBar.open('Gagal memuat data territory', 'Tutup', { duration: 3000 });
      }
    });
  }

  applyFilter(): void {
    if (this.filterAreaId) {
      this.filteredTerritories = this.territories.filter(t => t.area_id === this.filterAreaId);
    } else {
      this.filteredTerritories = [...this.territories];
    }
  }

  onCreate(): void {
    if (!this.newName.trim() || !this.newAreaId) return;

    this.territoryService.create({ name: this.newName, area_id: this.newAreaId }).subscribe({
      next: () => {
        this.newName = '';
        this.newAreaId = null;
        this.snackBar.open('Territory berhasil ditambahkan', 'Tutup', { duration: 3000 });
        this.loadTerritories();
      },
      error: (err) => {
        const msg = err?.error?.error || 'Gagal menambahkan territory';
        this.snackBar.open(msg, 'Tutup', { duration: 3000 });
      }
    });
  }

  onStartEdit(t: Territory): void {
    if (t.id) {
      this.editingId = t.id;
      this.editName = t.name;
      this.editAreaId = t.area_id;
    }
  }

  onCancelEdit(): void {
    this.editingId = null;
    this.editName = '';
    this.editAreaId = null;
  }

  onSaveEdit(id: number): void {
    if (!this.editName.trim() || !this.editAreaId) return;

    this.territoryService.update(id, { name: this.editName, area_id: this.editAreaId }).subscribe({
      next: () => {
        this.editingId = null;
        this.editName = '';
        this.editAreaId = null;
        this.snackBar.open('Territory berhasil diperbarui', 'Tutup', { duration: 3000 });
        this.loadTerritories();
      },
      error: (err) => {
        const msg = err?.error?.error || 'Gagal memperbarui territory';
        this.snackBar.open(msg, 'Tutup', { duration: 3000 });
      }
    });
  }

  onDelete(id: number): void {
    if (confirm('Apakah Anda yakin ingin menghapus territory ini?')) {
      this.territoryService.delete(id).subscribe({
        next: () => {
          this.snackBar.open('Territory berhasil dihapus', 'Tutup', { duration: 3000 });
          this.loadTerritories();
        },
        error: (err) => {
          const msg = err?.error?.error || 'Gagal menghapus territory';
          this.snackBar.open(msg, 'Tutup', { duration: 3000 });
        }
      });
    }
  }
}
