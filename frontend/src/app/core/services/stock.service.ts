import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';
import { Employee } from './employee.service';
import { Product } from './product.service';

export interface StockRokokItem {
  id?: number;
  stock_rokok_id?: number;
  product_id: number;
  product?: Product;
  qty_dus_init: number;
  qty_bal_init: number;
  qty_slf_init: number;
  qty_bks_init: number;
}

export interface StockRokok {
  id?: number;
  sales_id: number;
  sales?: Employee;
  date_used: string; // YYYY-MM-DD
  status: 'DRAFT' | 'READY' | 'PULLED' | 'CLOSED';
  created_by?: number;
  creator?: Employee;
  created_at?: string;
  updated_at?: string;
  items?: StockRokokItem[];
}

export interface StockDetailResponse {
  stock: StockRokok;
  items: StockRokokItem[];
}

@Injectable({
  providedIn: 'root'
})
export class StockService {
  constructor(private apiService: ApiService) {}

  list(): Observable<StockRokok[]> {
    return this.apiService.get<StockRokok[]>('/stock/rokok');
  }

  getDetail(id: number): Observable<StockDetailResponse> {
    return this.apiService.get<StockDetailResponse>(`/stock/rokok/${id}`);
  }

  create(stock: any): Observable<StockRokok> {
    return this.apiService.post<StockRokok>('/stock/rokok', stock);
  }

  updateStatus(id: number, status: 'DRAFT' | 'READY' | 'PULLED' | 'CLOSED'): Observable<any> {
    return this.apiService.put<any>(`/stock/rokok/${id}/status`, { status });
  }
}
