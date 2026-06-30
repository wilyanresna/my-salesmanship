import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { StockRokok, StockService } from '../../../core/services/stock.service';
import { Employee, EmployeeService } from '../../../core/services/employee.service';
import { StockFormComponent } from '../stock-form/stock-form.component';
import { StockDetailComponent } from '../stock-detail/stock-detail.component';

@Component({
  selector: 'app-stock-list',
  templateUrl: './stock-list.component.html',
  styleUrls: ['./stock-list.component.scss']
})
export class StockListComponent implements OnInit {
  stocks: StockRokok[] = [];
  filteredStocks: StockRokok[] = [];
  salesmen: Employee[] = [];
  isLoading = true;

  // Filter values
  filterSalesId: number | null = null;
  filterStatus = '';
  filterDate = '';

  constructor(
    private stockService: StockService,
    private employeeService: EmployeeService,
    private dialog: MatDialog,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.loadData();
  }

  loadData(): void {
    this.isLoading = true;
    this.employeeService.list().subscribe({
      next: (employees) => {
        this.salesmen = employees.filter(e => e.position === 'SALES');
        this.loadStocks();
      },
      error: () => {
        this.isLoading = false;
        this.snackBar.open('Gagal memuat data salesman', 'Tutup', { duration: 3000 });
      }
    });
  }

  loadStocks(): void {
    this.isLoading = true;
    this.stockService.list().subscribe({
      next: (data) => {
        this.stocks = data;
        this.applyFilters();
        this.isLoading = false;
      },
      error: () => {
        this.isLoading = false;
        this.snackBar.open('Gagal memuat data stok rokok', 'Tutup', { duration: 3000 });
      }
    });
  }

  applyFilters(): void {
    this.filteredStocks = this.stocks.filter(s => {
      // Salesman Filter
      if (this.filterSalesId && s.sales_id !== this.filterSalesId) {
        return false;
      }
      // Status Filter
      if (this.filterStatus && s.status !== this.filterStatus) {
        return false;
      }
      // Date Filter
      if (this.filterDate) {
        const sDate = new Date(s.date_used);
        const fDate = new Date(this.filterDate);
        if (sDate.toDateString() !== fDate.toDateString()) {
          return false;
        }
      }
      return true;
    });
  }

  resetFilters(): void {
    this.filterSalesId = null;
    this.filterStatus = '';
    this.filterDate = '';
    this.applyFilters();
  }

  openAddDialog(): void {
    const dialogRef = this.dialog.open(StockFormComponent, {
      width: '800px',
      data: {}
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.stockService.create(result).subscribe({
          next: () => {
            this.snackBar.open('Alokasi stok berhasil dibuat', 'Tutup', { duration: 3000 });
            this.loadStocks();
          },
          error: (err) => {
            const msg = err?.error?.error || 'Gagal membuat alokasi stok';
            this.snackBar.open(msg, 'Tutup', { duration: 4000 });
          }
        });
      }
    });
  }

  viewDetail(stock: StockRokok): void {
    this.dialog.open(StockDetailComponent, {
      width: '760px',
      data: { stockId: stock.id }
    });
  }

  toggleStatus(stock: StockRokok): void {
    const targetStatus = stock.status === 'DRAFT' ? 'READY' : 'DRAFT';
    const message = stock.status === 'DRAFT' 
      ? 'Apakah Anda yakin ingin mempublikasikan stok ini menjadi READY?' 
      : 'Apakah Anda yakin ingin mengembalikan status stok ini menjadi DRAFT?';

    if (confirm(message)) {
      this.stockService.updateStatus(stock.id!, targetStatus).subscribe({
        next: () => {
          this.snackBar.open(`Status stok berhasil diubah ke ${targetStatus}`, 'Tutup', { duration: 3000 });
          this.loadStocks();
        },
        error: (err) => {
          const msg = err?.error?.error || 'Gagal mengubah status stok';
          this.snackBar.open(msg, 'Tutup', { duration: 4000 });
        }
      });
    }
  }
}
