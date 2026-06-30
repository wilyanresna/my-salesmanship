import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';
import { Territory } from './territory.service';

export interface District {
  id?: number;
  territory_id: number;
  territory?: Territory;
  name: string;
  created_at?: string;
  updated_at?: string;
}

@Injectable({
  providedIn: 'root'
})
export class DistrictService {
  constructor(private apiService: ApiService) {}

  list(): Observable<District[]> {
    return this.apiService.get<District[]>('/master/districts');
  }

  get(id: number): Observable<District> {
    return this.apiService.get<District>(`/master/districts/${id}`);
  }

  create(district: District): Observable<District> {
    return this.apiService.post<District>('/master/districts', district);
  }

  update(id: number, district: District): Observable<District> {
    return this.apiService.put<District>(`/master/districts/${id}`, district);
  }

  delete(id: number): Observable<any> {
    return this.apiService.delete<any>(`/master/districts/${id}`);
  }
}
