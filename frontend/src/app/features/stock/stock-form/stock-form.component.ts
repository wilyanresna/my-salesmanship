import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, FormArray } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Employee, EmployeeService } from '../../../core/services/employee.service';
import { Product, ProductService } from '../../../core/services/product.service';

@Component({
  selector: 'app-stock-form',
  templateUrl: './stock-form.component.html',
  styleUrls: ['./stock-form.component.scss']
})
export class StockFormComponent implements OnInit {
  stockForm!: FormGroup;
  salesmen: Employee[] = [];
  products: Product[] = [];
  isLoading = true;

  constructor(
    private fb: FormBuilder,
    private employeeService: EmployeeService,
    private productService: ProductService,
    public dialogRef: MatDialogRef<StockFormComponent>
  ) {}

  ngOnInit(): void {
    this.stockForm = this.fb.group({
      sales_id: [null, [Validators.required]],
      date_used: ['', [Validators.required]],
      items: this.fb.array([])
    });

    this.loadData();
  }

  get items(): FormArray {
    return this.stockForm.get('items') as FormArray;
  }

  loadData(): void {
    this.employeeService.list().subscribe({
      next: (employees) => {
        this.salesmen = employees.filter(e => e.position === 'SALES' && e.is_active);
        
        this.productService.list().subscribe({
          next: (prodList) => {
            this.products = prodList.filter(p => p.is_active);
            this.initItems();
            this.isLoading = false;
          },
          error: () => {
            this.isLoading = false;
            this.dialogRef.close();
          }
        });
      },
      error: () => {
        this.isLoading = false;
        this.dialogRef.close();
      }
    });
  }

  initItems(): void {
    this.products.forEach(p => {
      this.items.push(this.fb.group({
        product_id: [p.id],
        product_name: [p.name],
        sku: [p.sku],
        uom_bal: [p.uom_bal],
        uom_slf: [p.uom_slf],
        uom_bks: [p.uom_bks],
        qty_dus_init: [0, [Validators.required, Validators.min(0)]],
        qty_bal_init: [0, [Validators.required, Validators.min(0)]],
        qty_slf_init: [0, [Validators.required, Validators.min(0)]],
        qty_bks_init: [0, [Validators.required, Validators.min(0)]]
      }));
    });
  }

  calculateTotalBungkus(group: any): number {
    const d = group.get('qty_dus_init')?.value || 0;
    const b = group.get('qty_bal_init')?.value || 0;
    const s = group.get('qty_slf_init')?.value || 0;
    const k = group.get('qty_bks_init')?.value || 0;

    const uomBal = group.get('uom_bal')?.value || 1;
    const uomSlf = group.get('uom_slf')?.value || 1;
    const uomBks = group.get('uom_bks')?.value || 1;

    const totalFromDus = d * uomBal * uomSlf * uomBks;
    const totalFromBal = b * uomSlf * uomBks;
    const totalFromSlf = s * uomBks;

    return totalFromDus + totalFromBal + totalFromSlf + k;
  }

  onSubmit(): void {
    if (this.stockForm.invalid) {
      return;
    }

    const val = this.stockForm.value;
    
    // Format date_used to YYYY-MM-DD
    const dateUsedObj = new Date(val.date_used);
    const dateUsedStr = `${dateUsedObj.getFullYear()}-${String(dateUsedObj.getMonth() + 1).padStart(2, '0')}-${String(dateUsedObj.getDate()).padStart(2, '0')}`;

    // Filter items where total quantity is greater than 0
    const rawItems = this.items.controls.map(c => c.value);
    const filteredItems = rawItems.filter(item => {
      const d = item.qty_dus_init || 0;
      const b = item.qty_bal_init || 0;
      const s = item.qty_slf_init || 0;
      const k = item.qty_bks_init || 0;
      return (d + b + s + k) > 0;
    }).map(item => ({
      product_id: item.product_id,
      qty_dus_init: item.qty_dus_init,
      qty_bal_init: item.qty_bal_init,
      qty_slf_init: item.qty_slf_init,
      qty_bks_init: item.qty_bks_init
    }));

    if (filteredItems.length === 0) {
      alert('Minimal satu barang harus dialokasikan');
      return;
    }

    this.dialogRef.close({
      sales_id: val.sales_id,
      date_used: dateUsedStr,
      items: filteredItems
    });
  }

  onCancel(): void {
    this.dialogRef.close();
  }
}
