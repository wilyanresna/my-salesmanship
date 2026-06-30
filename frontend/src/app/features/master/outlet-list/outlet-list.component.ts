import { Component, OnInit, ViewChild } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Outlet, OutletService } from '../../../core/services/outlet.service';
import { OutletFormComponent } from '../outlet-form/outlet-form.component';

@Component({
  selector: 'app-outlet-list',
  templateUrl: './outlet-list.component.html',
  styleUrls: ['./outlet-list.component.scss']
})
export class OutletListComponent implements OnInit {
  displayedColumns: string[] = ['name', 'barcode', 'owner_name', 'phone', 'call_cycle', 'outlet_status', 'actions'];
  dataSource = new MatTableDataSource<Outlet>([]);
  isLoading = true;

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  constructor(
    private outletService: OutletService,
    private dialog: MatDialog,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.loadOutlets();
  }

  loadOutlets(): void {
    this.isLoading = true;
    this.outletService.list().subscribe({
      next: (data) => {
        this.dataSource.data = data;
        this.dataSource.paginator = this.paginator;
        this.dataSource.sort = this.sort;
        this.isLoading = false;
      },
      error: (err) => {
        this.isLoading = false;
        this.snackBar.open('Gagal memuat data outlet', 'Tutup', { duration: 3000 });
      }
    });
  }

  applyFilter(event: Event): void {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();
  }

  openAddDialog(): void {
    const dialogRef = this.dialog.open(OutletFormComponent, {
      width: '600px',
      data: {}
    });

    dialogRef.afterClosed().subscribe((result: Outlet) => {
      if (result) {
        this.outletService.create(result).subscribe({
          next: () => {
            this.snackBar.open('Outlet berhasil ditambahkan', 'Tutup', { duration: 3000 });
            this.loadOutlets();
          },
          error: (err) => {
            const msg = err?.error?.error || 'Gagal menambahkan outlet';
            this.snackBar.open(msg, 'Tutup', { duration: 4000 });
          }
        });
      }
    });
  }

  openEditDialog(outlet: Outlet): void {
    const dialogRef = this.dialog.open(OutletFormComponent, {
      width: '600px',
      data: { outlet }
    });

    dialogRef.afterClosed().subscribe((result: Outlet) => {
      if (result && outlet.id) {
        this.outletService.update(outlet.id, result).subscribe({
          next: () => {
            this.snackBar.open('Outlet berhasil diperbarui', 'Tutup', { duration: 3000 });
            this.loadOutlets();
          },
          error: (err) => {
            const msg = err?.error?.error || 'Gagal memperbarui outlet';
            this.snackBar.open(msg, 'Tutup', { duration: 4000 });
          }
        });
      }
    });
  }
}
