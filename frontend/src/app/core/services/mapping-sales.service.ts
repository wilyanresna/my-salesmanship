import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';
import { Employee } from './employee.service';
import { District } from './district.service';

export interface MappingSales {
  id?: number;
  spv_id: number;
  spv?: Employee;
  sales_id: number;
  sales?: Employee;
  district_id: number;
  district?: District;
  week_start: string; // YYYY-MM-DD
  week_end: string;   // YYYY-MM-DD
  created_at?: string;
}

@Injectable({
  providedIn: 'root'
})
export class MappingSalesService {
  constructor(private apiService: ApiService) {}

  list(): Observable<MappingSales[]> {
    return this.apiService.get<MappingSales[]>('/mapping/sales');
  }

  create(mapping: MappingSales): Observable<MappingSales> {
    return this.apiService.post<MappingSales>('/mapping/sales', mapping);
  }
}
