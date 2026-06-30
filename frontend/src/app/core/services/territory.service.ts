import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';
import { Area } from './area.service';

export interface Territory {
  id?: number;
  area_id: number;
  area?: Area;
  name: string;
  created_at?: string;
  updated_at?: string;
}

@Injectable({
  providedIn: 'root'
})
export class TerritoryService {
  constructor(private apiService: ApiService) {}

  list(): Observable<Territory[]> {
    return this.apiService.get<Territory[]>('/master/territories');
  }

  get(id: number): Observable<Territory> {
    return this.apiService.get<Territory>(`/master/territories/${id}`);
  }

  create(territory: Territory): Observable<Territory> {
    return this.apiService.post<Territory>('/master/territories', territory);
  }

  update(id: number, territory: Territory): Observable<Territory> {
    return this.apiService.put<Territory>(`/master/territories/${id}`, territory);
  }

  delete(id: number): Observable<any> {
    return this.apiService.delete<any>(`/master/territories/${id}`);
  }
}
