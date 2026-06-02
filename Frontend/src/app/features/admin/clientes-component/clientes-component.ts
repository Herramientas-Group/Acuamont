import { Component, OnInit, ChangeDetectorRef, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TableModule } from 'primeng/table';
import { SelectModule } from 'primeng/select';
import { InputTextModule } from 'primeng/inputtext';
import { DialogModule } from 'primeng/dialog';
import { SortEvent } from 'primeng/api';
import { Cliente } from '../../../shared/interfaces/cliente';
import { ClientesService } from '../../../core/services/clientes-service';
import { LoaderService } from '../../../core/services/loader-service';

@Component({
  selector: 'app-clientes-component',
  standalone: true,
  imports: [CommonModule, FormsModule, TableModule, SelectModule, InputTextModule, DialogModule],
  templateUrl: './clientes-component.html'
})
export class ClientesComponent implements OnInit {
  @ViewChild('dt') dt: any;

  clientes: Cliente[] = [];
  filteredClientes: Cliente[] = [];

  filtroNombre: string = '';
  filtroEstado: number | null = null;

  estadosFiltro = [
    { label: 'Todos', value: null },
    { label: 'Activo', value: 1 },
    { label: 'Inactivo', value: 0 }
  ];

  isSorted: boolean | null = null;
  private resetting = false;

  modalVisible = false;
  editando = false;
  formCliente: Cliente = { nombre: '', documento: '', telefono: '', correo: '', estado: 1 };
  buscandoDocumento = false;

  constructor(
    private clientesService: ClientesService,
    private loaderService: LoaderService,
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit(): void {
    this.obtenerClientes();
  }

  obtenerClientes(): void {
    this.clientesService.listarClientes().subscribe({
      next: (data) => {
        this.clientes = data;
        this.filteredClientes = [...data];
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
      this.aplicarFiltros();
      this.dt.reset();
      setTimeout(() => {
        this.resetting = false;
      }, 0);
    }
  }

  private sortTableData(event: SortEvent): void {
    this.filteredClientes.sort((data1, data2) => {
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
    this.filteredClientes = this.clientes.filter(c => {
      const coincideNombre = !this.filtroNombre ||
        c.nombre.toLowerCase().includes(this.filtroNombre.toLowerCase());
      const coincideEstado = this.filtroEstado === null || c.estado === this.filtroEstado;
      return coincideNombre && coincideEstado;
    });
  }

  limpiarFiltros(): void {
    this.filtroNombre = '';
    this.filtroEstado = null;
    this.aplicarFiltros();
  }

  abrirModal(cliente?: Cliente): void {
    if (cliente) {
      this.editando = true;
      this.formCliente = { ...cliente };
    } else {
      this.editando = false;
      this.formCliente = { nombre: '', documento: '', telefono: '', correo: '', estado: 1 };
    }
    this.modalVisible = true;
  }

  buscarDocumento(): void {
    const doc = this.formCliente.documento?.trim();
    if (!doc) return;

    this.buscandoDocumento = true;
    this.clientesService.buscarPorDocumento(doc).subscribe({
      next: (res) => {
        if (res.success && res.data) {
          this.formCliente.nombre = res.data.nombre;
          this.formCliente.telefono = res.data.telefono;
          this.formCliente.correo = res.data.correo;
        }
        this.buscandoDocumento = false;
      },
      error: () => {
        this.buscandoDocumento = false;
      }
    });
  }

  guardarCliente(): void {
    if (!this.formCliente.nombre.trim() || !this.formCliente.documento.trim()) return;

    this.clientesService.guardarCliente(this.formCliente).subscribe({
      next: () => {
        this.modalVisible = false;
        this.obtenerClientes();
      },
      error: (err) => console.error(err)
    });
  }

  eliminarCliente(id: number): void {
    if (confirm('¿Estás seguro de eliminar este cliente?')) {
      this.clientesService.eliminarCliente(id).subscribe({
        next: () => this.obtenerClientes(),
        error: (err) => console.error(err)
      });
    }
  }

  toggleEstado(id: number): void {
    this.clientesService.cambiarEstado(id).subscribe({
      next: () => this.obtenerClientes(),
      error: (err) => console.error(err)
    });
  }
}
