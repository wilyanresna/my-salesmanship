import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';

export interface Area {
  id?: number;
  name: string;
  created_at?: string;
  updated_at?: string;
}

@Injectable({
  providedIn: 'root'
})
export class AreaService {
  constructor(private apiService: ApiService) {}

  list(): Observable<Area[]> {
    return this.apiService.get<Area[]>('/master/areas');
  }

  get(id: number): Observable<Area> {
    return this.apiService.get<Area>(`/master/areas/${id}`);
  }

  create(area: Area): Observable<Area> {
    return this.apiService.post<Area>('/master/areas', area);
  }

  update(id: number, area: Area): Observable<Area> {
    return this.apiService.put<Area>(`/master/areas/${id}`, area);
  }

  delete(id: number): Observable<any> {
    return this.apiService.delete<any>(`/master/areas/${id}`);
  }
}
