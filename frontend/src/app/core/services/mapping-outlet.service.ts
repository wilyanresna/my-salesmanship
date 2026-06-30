import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';
import { Outlet } from './outlet.service';
import { Route } from './route.service';

export interface MappingOutlet {
  id?: number;
  outlet_id: number;
  outlet?: Outlet;
  route_id: number;
  route?: Route;
  week_start: string; // YYYY-MM-DD
  week_end: string;   // YYYY-MM-DD
  created_at?: string;
}

@Injectable({
  providedIn: 'root'
})
export class MappingOutletService {
  constructor(private apiService: ApiService) {}

  list(): Observable<MappingOutlet[]> {
    return this.apiService.get<MappingOutlet[]>('/mapping/outlet');
  }

  create(mapping: MappingOutlet): Observable<MappingOutlet> {
    return this.apiService.post<MappingOutlet>('/mapping/outlet', mapping);
  }
}
