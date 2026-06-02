import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { Producto } from '../../shared/interfaces/producto';
import { CarritoItem } from '../../shared/interfaces/carrito';

@Injectable({
  providedIn: 'root',
})
export class CarritoService {
  private itemsSubject = new BehaviorSubject<CarritoItem[]>([]);
  items$: Observable<CarritoItem[]> = this.itemsSubject.asObservable();

  constructor() {
    const saved = localStorage.getItem('carrito');
    if (saved) {
      try {
        this.itemsSubject.next(JSON.parse(saved));
      } catch { }
    }
  }

  private persist(): void {
    localStorage.setItem('carrito', JSON.stringify(this.itemsSubject.value));
  }

  getItems(): CarritoItem[] {
    return this.itemsSubject.value;
  }

  getCount(): number {
    return this.itemsSubject.value.reduce((sum, item) => sum + item.cantidad, 0);
  }

  getTotal(): number {
    return this.itemsSubject.value.reduce((sum, item) => sum + item.producto.precioVenta * item.cantidad, 0);
  }

  agregar(producto: Producto, cantidad: number = 1): void {
    const items = this.getItems();
    const existente = items.find(i => i.producto.id === producto.id);
    if (existente) {
      existente.cantidad += cantidad;
    } else {
      items.push({ producto, cantidad });
    }
    this.itemsSubject.next([...items]);
    this.persist();
  }

  actualizarCantidad(productoId: number, cantidad: number): void {
    const items = this.getItems();
    const item = items.find(i => i.producto.id === productoId);
    if (item) {
      item.cantidad = cantidad;
      this.itemsSubject.next([...items]);
      this.persist();
    }
  }

  eliminar(productoId: number): void {
    const items = this.getItems().filter(i => i.producto.id !== productoId);
    this.itemsSubject.next(items);
    this.persist();
  }

  vaciar(): void {
    this.itemsSubject.next([]);
    this.persist();
  }
}
