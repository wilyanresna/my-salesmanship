import { Component, OnInit } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Area, AreaService } from '../../../core/services/area.service';

@Component({
  selector: 'app-area-list',
  templateUrl: './area-list.component.html',
  styleUrls: ['./area-list.component.scss']
})
export class AreaListComponent implements OnInit {
  areas: Area[] = [];
  isLoading = true;
  editingId: number | null = null;
  editName = '';
  newName = '';

  constructor(
    private areaService: AreaService,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.loadAreas();
  }

  loadAreas(): void {
    this.isLoading = true;
    this.areaService.list().subscribe({
      next: (data) => {
        this.areas = data;
        this.isLoading = false;
      },
      error: () => {
        this.isLoading = false;
        this.snackBar.open('Gagal memuat data area', 'Tutup', { duration: 3000 });
      }
    });
  }

  onCreate(): void {
    if (!this.newName.trim()) return;

    this.areaService.create({ name: this.newName }).subscribe({
      next: () => {
        this.newName = '';
        this.snackBar.open('Area berhasil ditambahkan', 'Tutup', { duration: 3000 });
        this.loadAreas();
      },
      error: (err) => {
        const msg = err?.error?.error || 'Gagal menambahkan area';
        this.snackBar.open(msg, 'Tutup', { duration: 3000 });
      }
    });
  }

  onStartEdit(area: Area): void {
    if (area.id) {
      this.editingId = area.id;
      this.editName = area.name;
    }
  }

  onCancelEdit(): void {
    this.editingId = null;
    this.editName = '';
  }

  onSaveEdit(areaId: number): void {
    if (!this.editName.trim()) return;

    this.areaService.update(areaId, { name: this.editName }).subscribe({
      next: () => {
        this.editingId = null;
        this.editName = '';
        this.snackBar.open('Area berhasil diperbarui', 'Tutup', { duration: 3000 });
        this.loadAreas();
      },
      error: (err) => {
        const msg = err?.error?.error || 'Gagal memperbarui area';
        this.snackBar.open(msg, 'Tutup', { duration: 3000 });
      }
    });
  }

  onDelete(areaId: number): void {
    if (confirm('Apakah Anda yakin ingin menghapus area ini?')) {
      this.areaService.delete(areaId).subscribe({
        next: () => {
          this.snackBar.open('Area berhasil dihapus', 'Tutup', { duration: 3000 });
          this.loadAreas();
        },
        error: (err) => {
          const msg = err?.error?.error || 'Gagal menghapus area';
          this.snackBar.open(msg, 'Tutup', { duration: 3000 });
        }
      });
    }
  }
}
