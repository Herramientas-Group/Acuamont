import { Component, OnInit, ViewChild, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TableModule } from 'primeng/table';
import { SelectModule } from 'primeng/select';
import { InputTextModule } from 'primeng/inputtext';
import { DialogModule } from 'primeng/dialog';
import { ConfirmationService, SortEvent } from 'primeng/api';
import { Proveedor } from '../../../shared/interfaces/proveedor';
import { ProveedoresService } from '../../../core/services/proveedores-service';
import { LoaderService } from '../../../core/services/loader-service';
import { ConfirmDialog } from 'primeng/confirmdialog';

@Component({
  selector: 'app-proveedores-component',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    TableModule,
    SelectModule,
    InputTextModule,
    DialogModule,
    ConfirmDialog,
  ],
  templateUrl: './proveedores-component.html',
})
export class ProveedoresComponent implements OnInit {
  @ViewChild('dt') dt: any;

  proveedores: Proveedor[] = [];
  filteredProveedores: Proveedor[] = [];

  filtroNombre: string = '';
  filtroEstado: number | null = null;

  estadosFiltro = [
    { label: 'Todos', value: null },
    { label: 'Activo', value: 1 },
    { label: 'Inactivo', value: 0 },
  ];

  isSorted: boolean | null = null;
  private resetting = false;

  modalVisible = false;
  editando = false;
  formProveedor: Proveedor = { nombre: '', documento: '', telefono: '', correo: '', estado: 1 };
  buscandoDocumento = false;

  constructor(
    private proveedoresService: ProveedoresService,
    private loaderService: LoaderService,
    private confirmationService: ConfirmationService,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit(): void {
    this.obtenerProveedores();
  }

  obtenerProveedores(): void {
    this.proveedoresService.listarProveedores().subscribe({
      next: (data) => {
        this.proveedores = data;
        this.filteredProveedores = [...data];
        this.isSorted = null;
        this.loaderService.hide();
        this.cdr.markForCheck();
      },
      error: (err) => {
        console.error(err);
        this.loaderService.hide();
        this.cdr.markForCheck();
      },
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
      this.aplicarFiltros();
      this.dt.reset();
      setTimeout(() => {
        this.resetting = false;
      }, 0);
    }
  }

  private sortTableData(event: SortEvent): void {
    this.filteredProveedores.sort((data1, data2) => {
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

  aplicarFiltros(): void {
    this.isSorted = null;
    this.filteredProveedores = this.proveedores.filter((p) => {
      const coincideNombre =
        !this.filtroNombre || p.nombre.toLowerCase().includes(this.filtroNombre.toLowerCase());
      const coincideEstado = this.filtroEstado === null || p.estado === this.filtroEstado;
      return coincideNombre && coincideEstado;
    });
  }

  limpiarFiltros(): void {
    this.filtroNombre = '';
    this.filtroEstado = null;
    this.aplicarFiltros();
  }

  abrirModal(proveedor?: Proveedor): void {
    if (proveedor) {
      this.editando = true;
      this.formProveedor = { ...proveedor };
    } else {
      this.editando = false;
      this.formProveedor = { nombre: '', documento: '', telefono: '', correo: '', estado: 1 };
    }
    // ✅ Diferir la apertura del dialog para que PrimeNG no colisione con el ciclo actual
    Promise.resolve().then(() => {
      this.modalVisible = true;
      this.cdr.markForCheck();
    });
  }

  cerrarModal(): void {
    // ✅ Diferir el cierre para evitar el conflicto con [(visible)] de PrimeNG
    Promise.resolve().then(() => {
      this.modalVisible = false;
      this.cdr.markForCheck();
    });
  }

  buscarDocumento(): void {
    const doc = this.formProveedor.documento?.trim();
    if (!doc) return;

    this.buscandoDocumento = true;
    this.proveedoresService.buscarPorDocumento(doc).subscribe({
      next: (res) => {
        if (res.success && res.data) {
          this.formProveedor.nombre = res.data.nombre;
          this.formProveedor.telefono = res.data.telefono;
          this.formProveedor.correo = res.data.correo;
        }
        this.buscandoDocumento = false;
        this.cdr.markForCheck();
      },
      error: () => {
        this.buscandoDocumento = false;
        this.cdr.markForCheck();
      },
    });
  }

  guardarProveedor(): void {
    if (!this.formProveedor.nombre.trim() || !this.formProveedor.documento.trim()) return;

    this.proveedoresService.guardarProveedor(this.formProveedor).subscribe({
      next: () => {
        this.cerrarModal();
        this.obtenerProveedores();
      },
      error: (err) => console.error(err),
    });
  }

  eliminarProveedor(id: number): void {
    this.confirmationService.confirm({
      message: `¿Estás seguro de eliminar al proveedor con ID # ${id}?`, // Ajustado el mensaje
      header: 'Eliminar Proveedor',
      icon: 'pi pi-exclamation-triangle',

      acceptLabel: 'Sí, eliminar',
      acceptButtonStyleClass: 'p-button-danger p-button-text',

      rejectLabel: 'Cancelar',
      rejectButtonStyleClass: 'p-button-secondary p-button-text',

      accept: () => {
        this.proveedoresService.eliminarProveedor(id).subscribe({
          next: () => this.obtenerProveedores(),
          error: (err) => console.error(err),
        });
      },
    });
  }

  toggleEstado(id: number): void {
    this.proveedoresService.cambiarEstado(id).subscribe({
      next: () => this.obtenerProveedores(),
      error: (err) => console.error(err),
    });
  }
}
