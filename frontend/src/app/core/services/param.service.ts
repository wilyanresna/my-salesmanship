import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';

export interface Param {
  id: number;
  group_name: string;
  key: string;
  value: string;
  description: string;
  is_active: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class ParamService {
  constructor(private apiService: ApiService) {}

  list(): Observable<Param[]> {
    return this.apiService.get<Param[]>('/master/params');
  }
}
