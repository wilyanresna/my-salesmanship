import { Component, OnInit } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Employee, EmployeeService } from '../../../core/services/employee.service';
import { Territory, TerritoryService } from '../../../core/services/territory.service';
import { MappingSpv, MappingSpvService } from '../../../core/services/mapping-spv.service';

@Component({
  selector: 'app-mapping-spv',
  templateUrl: './mapping-spv.component.html',
  styleUrls: ['./mapping-spv.component.scss']
})
export class MappingSpvComponent implements OnInit {
  mappings: MappingSpv[] = [];
  spvs: Employee[] = [];
  territories: Territory[] = [];
  isLoading = true;

  // Form Fields
  selectedSpvId: number | null = null;
  selectedTerritoryId: number | null = null;
  weekStart = '';
  weekEnd = '';

  constructor(
    private mappingSpvService: MappingSpvService,
    private employeeService: EmployeeService,
    private territoryService: TerritoryService,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.loadData();
  }

  loadData(): void {
    this.isLoading = true;
    
    // Load Supervisors
    this.employeeService.list().subscribe({
      next: (employees) => {
        this.spvs = employees.filter(e => e.position === 'SPV' && e.is_active);
        
        // Load Territories
        this.territoryService.list().subscribe({
          next: (territoriesData) => {
            this.territories = territoriesData;
            this.loadMappings();
          },
          error: () => {
            this.isLoading = false;
            this.snackBar.open('Gagal memuat data territory', 'Tutup', { duration: 3000 });
          }
        });
      },
      error: () => {
        this.isLoading = false;
        this.snackBar.open('Gagal memuat data supervisor', 'Tutup', { duration: 3000 });
      }
    });
  }

  loadMappings(): void {
    this.mappingSpvService.list().subscribe({
      next: (data) => {
        this.mappings = data;
        this.isLoading = false;
      },
      error: () => {
        this.isLoading = false;
        this.snackBar.open('Gagal memuat data mapping spv', 'Tutup', { duration: 3000 });
      }
    });
  }

  onWeekSelected(week: { weekStart: string; weekEnd: string }): void {
    this.weekStart = week.weekStart;
    this.weekEnd = week.weekEnd;
  }

  onSubmit(): void {
    if (!this.selectedSpvId || !this.selectedTerritoryId || !this.weekStart || !this.weekEnd) {
      return;
    }

    const payload: MappingSpv = {
      spv_id: this.selectedSpvId,
      territory_id: this.selectedTerritoryId,
      week_start: this.weekStart,
      week_end: this.weekEnd
    };

    this.mappingSpvService.create(payload).subscribe({
      next: () => {
        this.selectedSpvId = null;
        this.selectedTerritoryId = null;
        this.snackBar.open('Mapping SPV berhasil ditambahkan', 'Tutup', { duration: 3000 });
        this.loadMappings();
      },
      error: (err) => {
        const msg = err?.error?.error || 'Gagal menambahkan mapping SPV';
        this.snackBar.open(msg, 'Tutup', { duration: 4000 });
      }
    });
  }
}
