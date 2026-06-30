import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';
import { District } from './district.service';

export interface Route {
  id?: number;
  district_id: number;
  district?: District;
  name: string;
  day_of_week: number; // 1-7
  created_at?: string;
  updated_at?: string;
}

@Injectable({
  providedIn: 'root'
})
export class RouteService {
  constructor(private apiService: ApiService) {}

  list(): Observable<Route[]> {
    return this.apiService.get<Route[]>('/master/routes');
  }

  get(id: number): Observable<Route> {
    return this.apiService.get<Route>(`/master/routes/${id}`);
  }

  create(route: Route): Observable<Route> {
    return this.apiService.post<Route>('/master/routes', route);
  }

  update(id: number, route: Route): Observable<Route> {
    return this.apiService.put<Route>(`/master/routes/${id}`, route);
  }

  delete(id: number): Observable<any> {
    return this.apiService.delete<any>(`/master/routes/${id}`);
  }
}
