import { Component, OnInit } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Employee, EmployeeService } from '../../../core/services/employee.service';
import { District, DistrictService } from '../../../core/services/district.service';
import { MappingSales, MappingSalesService } from '../../../core/services/mapping-sales.service';

@Component({
  selector: 'app-mapping-sales',
  templateUrl: './mapping-sales.component.html',
  styleUrls: ['./mapping-sales.component.scss']
})
export class MappingSalesComponent implements OnInit {
  mappings: MappingSales[] = [];
  spvs: Employee[] = [];
  salesmen: Employee[] = [];
  districts: District[] = [];
  isLoading = true;

  // Form Fields
  selectedSpvId: number | null = null;
  selectedSalesId: number | null = null;
  selectedDistrictId: number | null = null;
  weekStart = '';
  weekEnd = '';

  constructor(
    private mappingSalesService: MappingSalesService,
    private employeeService: EmployeeService,
    private districtService: DistrictService,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.loadData();
  }

  loadData(): void {
    this.isLoading = true;

    // Load employees
    this.employeeService.list().subscribe({
      next: (employees) => {
        this.spvs = employees.filter(e => e.position === 'SPV' && e.is_active);
        this.salesmen = employees.filter(e => e.position === 'SALES' && e.is_active);

        // Load Districts
        this.districtService.list().subscribe({
          next: (districtData) => {
            this.districts = districtData;
            this.loadMappings();
          },
          error: () => {
            this.isLoading = false;
            this.snackBar.open('Gagal memuat data district', 'Tutup', { duration: 3000 });
          }
        });
      },
      error: () => {
        this.isLoading = false;
        this.snackBar.open('Gagal memuat data karyawan', 'Tutup', { duration: 3000 });
      }
    });
  }

  loadMappings(): void {
    this.mappingSalesService.list().subscribe({
      next: (data) => {
        this.mappings = data;
        this.isLoading = false;
      },
      error: () => {
        this.isLoading = false;
        this.snackBar.open('Gagal memuat data mapping sales', 'Tutup', { duration: 3000 });
      }
    });
  }

  onWeekSelected(week: { weekStart: string; weekEnd: string }): void {
    this.weekStart = week.weekStart;
    this.weekEnd = week.weekEnd;
  }

  onSubmit(): void {
    if (!this.selectedSpvId || !this.selectedSalesId || !this.selectedDistrictId || !this.weekStart || !this.weekEnd) {
      return;
    }

    const payload: MappingSales = {
      spv_id: this.selectedSpvId,
      sales_id: this.selectedSalesId,
      district_id: this.selectedDistrictId,
      week_start: this.weekStart,
      week_end: this.weekEnd
    };

    this.mappingSalesService.create(payload).subscribe({
      next: () => {
        this.selectedSalesId = null;
        this.selectedDistrictId = null;
        this.snackBar.open('Mapping Sales berhasil ditambahkan', 'Tutup', { duration: 3000 });
        this.loadMappings();
      },
      error: (err) => {
        const msg = err?.error?.error || 'Gagal menambahkan mapping Sales';
        this.snackBar.open(msg, 'Tutup', { duration: 4000 });
      }
    });
  }
}
