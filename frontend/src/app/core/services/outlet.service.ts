import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';

export interface Outlet {
  id?: number;
  name: string;
  owner_name?: string;
  phone?: string;
  address?: string;
  lat?: number;
  lng?: number;
  barcode: string;
  outlet_status: 'ACTIVE' | 'INACTIVE' | 'POTENTIAL';
  call_cycle: 'DAILY' | 'WEEKLY' | 'BIWEEKLY';
  created_at?: string;
  updated_at?: string;
}

@Injectable({
  providedIn: 'root'
})
export class OutletService {
  constructor(private apiService: ApiService) {}

  list(): Observable<Outlet[]> {
    return this.apiService.get<Outlet[]>('/master/outlets');
  }

  get(id: number): Observable<Outlet> {
    return this.apiService.get<Outlet>(`/master/outlets/${id}`);
  }

  create(outlet: Outlet): Observable<Outlet> {
    return this.apiService.post<Outlet>('/master/outlets', outlet);
  }

  update(id: number, outlet: Outlet): Observable<Outlet> {
    return this.apiService.put<Outlet>(`/master/outlets/${id}`, outlet);
  }
}
