import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';

export interface Employee {
  id?: number;
  name: string;
  nik: string;
  username: string;
  password?: string;
  position: 'SPV' | 'SALES';
  phone?: string;
  address?: string;
  is_active: boolean;
  created_at?: string;
  updated_at?: string;
}

@Injectable({
  providedIn: 'root'
})
export class EmployeeService {
  constructor(private apiService: ApiService) {}

  list(): Observable<Employee[]> {
    return this.apiService.get<Employee[]>('/master/employees');
  }

  get(id: number): Observable<Employee> {
    return this.apiService.get<Employee>(`/master/employees/${id}`);
  }

  create(employee: Employee): Observable<Employee> {
    return this.apiService.post<Employee>('/master/employees', employee);
  }

  update(id: number, employee: Employee): Observable<Employee> {
    return this.apiService.put<Employee>(`/master/employees/${id}`, employee);
  }
}
