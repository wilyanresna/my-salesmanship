import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { StockRokok, StockRokokItem, StockService } from '../../../core/services/stock.service';

@Component({
  selector: 'app-stock-detail',
  templateUrl: './stock-detail.component.html',
  styleUrls: ['./stock-detail.component.scss']
})
export class StockDetailComponent implements OnInit {
  stock!: StockRokok;
  items: StockRokokItem[] = [];
  isLoading = true;

  constructor(
    private stockService: StockService,
    private snackBar: MatSnackBar,
    public dialogRef: MatDialogRef<StockDetailComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { stockId: number }
  ) {}

  ngOnInit(): void {
    this.loadDetail();
  }

  loadDetail(): void {
    this.isLoading = true;
    this.stockService.getDetail(this.data.stockId).subscribe({
      next: (res) => {
        this.stock = res.stock;
        this.items = res.items;
        this.isLoading = false;
      },
      error: () => {
        this.isLoading = false;
        this.snackBar.open('Gagal memuat detail stok', 'Tutup', { duration: 3000 });
        this.dialogRef.close();
      }
    });
  }

  calculateTotalBungkus(item: StockRokokItem): number {
    const d = item.qty_dus_init || 0;
    const b = item.qty_bal_init || 0;
    const s = item.qty_slf_init || 0;
    const k = item.qty_bks_init || 0;

    const uomBal = item.product?.uom_bal || 1;
    const uomSlf = item.product?.uom_slf || 1;
    const uomBks = item.product?.uom_bks || 1;

    const totalFromDus = d * uomBal * uomSlf * uomBks;
    const totalFromBal = b * uomSlf * uomBks;
    const totalFromSlf = s * uomBks;

    return totalFromDus + totalFromBal + totalFromSlf + k;
  }

  onClose(): void {
    this.dialogRef.close();
  }
}
