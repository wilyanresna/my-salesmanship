import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';

export interface Product {
  id: number;
  name: string;
  sku: string;
  price: number;
  uom_bal: number;
  uom_slf: number;
  uom_bks: number;
  is_active: boolean;
  created_at?: string;
  updated_at?: string;
}

@Injectable({
  providedIn: 'root'
})
export class ProductService {
  constructor(private apiService: ApiService) {}

  list(): Observable<Product[]> {
    return this.apiService.get<Product[]>('/master/products');
  }
}
