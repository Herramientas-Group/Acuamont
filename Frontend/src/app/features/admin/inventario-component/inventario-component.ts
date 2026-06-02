import { Component, OnInit, ChangeDetectorRef, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TableModule } from 'primeng/table';
import { InputTextModule } from 'primeng/inputtext';
import { InputNumberModule } from 'primeng/inputnumber';
import { SelectModule } from 'primeng/select';
import { DialogModule } from 'primeng/dialog';

import { SortEvent } from 'primeng/api';
import { Producto } from '../../../shared/interfaces/producto';
import { TipoMovimiento, AjusteInventario, AjusteInventarioDTO, MovimientoProducto } from '../../../shared/interfaces/inventario';
import { InventarioService } from '../../../core/services/inventario-service';
import { LoaderService } from '../../../core/services/loader-service';

@Component({
  selector: 'app-inventario-component',
  standalone: true,
  imports: [CommonModule, FormsModule, TableModule, InputTextModule, InputNumberModule, SelectModule, DialogModule],
  templateUrl: './inventario-component.html'
})
export class InventarioComponent implements OnInit {
  @ViewChild('dt') dt: any;

  productos: Producto[] = [];
  filteredProductos: Producto[] = [];

  isSorted: boolean | null = null;
  private resetting = false;

  modalMovimientosVisible = false;
  modalAjustesVisible = false;
  modalRegistrarAjusteVisible = false;

  movimientos: MovimientoProducto[] = [];
  ajustes: AjusteInventario[] = [];
  productoSeleccionado: Producto | null = null;

  tiposMovimiento: TipoMovimiento[] = [];
  formAjuste: AjusteInventarioDTO = {
    productoId: 0,
    tipoMovimientoId: 0,
    cantidad: 0,
    comentario: ''
  };

  constructor(
    private inventarioService: InventarioService,
    private loaderService: LoaderService,
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit(): void {
    this.obtenerProductos();
    this.inventarioService.listarTiposMovimientos().subscribe({
      next: (data) => {
        this.tiposMovimiento = data;
        this.cdr.detectChanges();
      },
      error: (err) => console.error(err)
    });
  }

  obtenerProductos(): void {
    this.inventarioService.listarProductos().subscribe({
      next: (data) => {
        this.productos = data;
        this.filteredProductos = [...data];
        this.isSorted = null;
        this.loaderService.hide();
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error(err);
        this.loaderService.hide();
      }
    });
  }

  customSort(event: SortEvent): void {
    if (this.resetting) return;

    if (this.isSorted == null) {
      this.isSorted = true;
      this.sortTableData(event);
    } else if (this.isSorted === true) {
      this.isSorted = false;
      this.sortTableData(event);
    } else {
      this.isSorted = null;
      this.resetting = true;
      this.filteredProductos = [...this.productos];
      this.dt.reset();
      setTimeout(() => {
        this.resetting = false;
      }, 0);
    }
  }

  private sortTableData(event: SortEvent): void {
    this.filteredProductos.sort((data1, data2) => {
      const value1 = (data1 as any)[event.field!];
      const value2 = (data2 as any)[event.field!];
      let result: number;

      if (value1 == null && value2 != null) result = -1;
      else if (value1 != null && value2 == null) result = 1;
      else if (value1 == null && value2 == null) result = 0;
      else if (typeof value1 === 'string' && typeof value2 === 'string')
        result = value1.localeCompare(value2);
      else result = value1 < value2 ? -1 : value1 > value2 ? 1 : 0;

      return event.order! * result;
    });
  }

  getStockStatus(stock: number, stockSeguridad: number): string {
    if (stock === 0) return 'critical';
    if (stock < stockSeguridad) return 'warning';
    return 'ok';
  }

  getInitial(nombre: string): string {
    return nombre ? nombre.charAt(0).toUpperCase() : '?';
  }

  getAvatarColor(nombre: string): string {
    const colors = ['#0D72C5', '#059669', '#D97706', '#DC2626', '#7C3AED', '#DB2777', '#0891B2'];
    let hash = 0;
    for (let i = 0; i < nombre.length; i++) {
      hash = nombre.charCodeAt(i) + ((hash << 5) - hash);
    }
    return colors[Math.abs(hash) % colors.length];
  }

  verMovimientos(producto: Producto): void {
    this.productoSeleccionado = producto;
    this.inventarioService.obtenerMovimientos(producto.id!).subscribe({
      next: (data) => {
        this.movimientos = data;
        this.modalMovimientosVisible = true;
        this.cdr.detectChanges();
      },
      error: (err) => console.error(err)
    });
  }

  get totalMovimientosCantidad(): number {
    return this.movimientos.reduce((sum, m) => sum + m.cantidad, 0);
  }

  get totalMovimientosSubtotal(): number {
    return this.movimientos.reduce((sum, m) => sum + m.subtotal, 0);
  }

  verAjustes(producto: Producto): void {
    this.productoSeleccionado = producto;
    this.inventarioService.obtenerAjustes(producto.id!).subscribe({
      next: (data) => {
        this.ajustes = data;
        this.modalAjustesVisible = true;
        this.cdr.detectChanges();
      },
      error: (err) => console.error(err)
    });
  }

  abrirRegistrarAjuste(producto: Producto): void {
    this.productoSeleccionado = producto;
    this.formAjuste = {
      productoId: producto.id!,
      tipoMovimientoId: 0,
      cantidad: 0,
      comentario: ''
    };
    this.modalRegistrarAjusteVisible = true;
  }

  guardarAjuste(): void {
    if (!this.formAjuste.tipoMovimientoId || !this.formAjuste.cantidad) return;

    this.inventarioService.guardarAjuste(this.formAjuste).subscribe({
      next: () => {
        this.modalRegistrarAjusteVisible = false;
        this.obtenerProductos();
      },
      error: (err) => console.error(err)
    });
  }
}
