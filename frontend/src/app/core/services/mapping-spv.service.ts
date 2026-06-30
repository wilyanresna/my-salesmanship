import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';
import { Employee } from './employee.service';
import { Territory } from './territory.service';

export interface MappingSpv {
  id?: number;
  spv_id: number;
  spv?: Employee;
  territory_id: number;
  territory?: Territory;
  week_start: string; // YYYY-MM-DD
  week_end: string;   // YYYY-MM-DD
  created_at?: string;
}

@Injectable({
  providedIn: 'root'
})
export class MappingSpvService {
  constructor(private apiService: ApiService) {}

  list(): Observable<MappingSpv[]> {
    return this.apiService.get<MappingSpv[]>('/mapping/spv');
  }

  create(mapping: MappingSpv): Observable<MappingSpv> {
    return this.apiService.post<MappingSpv>('/mapping/spv', mapping);
  }
}
