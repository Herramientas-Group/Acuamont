import { Component, OnInit, ChangeDetectorRef, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TableModule } from 'primeng/table';
import { SelectModule } from 'primeng/select';
import { InputTextModule } from 'primeng/inputtext';
import { DialogModule } from 'primeng/dialog';
import { SortEvent } from 'primeng/api';
import { Perfil, Opcion } from '../../../shared/interfaces/perfil';
import { PerfilService } from '../../../core/services/perfil-service';
import { LoaderService } from '../../../core/services/loader-service';

@Component({
  selector: 'app-perfiles-component',
  standalone: true,
  imports: [CommonModule, FormsModule, TableModule, SelectModule, InputTextModule, DialogModule],
  templateUrl: './perfiles-component.html',
})
export class PerfilesComponent implements OnInit {
  @ViewChild('dt') dt: any;

  perfiles: Perfil[] = [];
  filteredPerfiles: Perfil[] = [];

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
  formPerfil: { id?: number; nombre: string; descripcion: string } = {
    nombre: '', descripcion: ''
  };

  modalPermisosVisible = false;
  opciones: Opcion[] = [];
  opcionesSeleccionadas: Set<number> = new Set();
  perfilIdPermisos: number | null = null;
  perfilNombrePermisos: string = '';

  constructor(
    private perfilService: PerfilService,
    private loaderService: LoaderService,
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit(): void {
    this.obtenerPerfiles();
  }

  obtenerPerfiles(): void {
    this.perfilService.listarPerfiles().subscribe({
      next: (data) => {
        this.perfiles = data;
        this.filteredPerfiles = [...data];
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
      setTimeout(() => { this.resetting = false; }, 0);
    }
  }

  private sortTableData(event: SortEvent): void {
    this.filteredPerfiles.sort((data1, data2) => {
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
    this.filteredPerfiles = this.perfiles.filter(p => {
      const coincideNombre = !this.filtroNombre ||
        p.nombre.toLowerCase().includes(this.filtroNombre.toLowerCase());
      const coincideEstado = this.filtroEstado === null || p.estado === this.filtroEstado;
      return coincideNombre && coincideEstado;
    });
  }

  limpiarFiltros(): void {
    this.filtroNombre = '';
    this.filtroEstado = null;
    this.aplicarFiltros();
  }

  abrirModal(perfil?: Perfil): void {
    if (perfil) {
      this.editando = true;
      this.formPerfil = {
        id: perfil.id,
        nombre: perfil.nombre,
        descripcion: perfil.descripcion || ''
      };
    } else {
      this.editando = false;
      this.formPerfil = { nombre: '', descripcion: '' };
    }
    this.modalVisible = true;
  }

  guardarPerfil(): void {
    if (!this.formPerfil.nombre.trim()) return;
    this.perfilService.guardarPerfil(this.formPerfil).subscribe({
      next: () => {
        this.modalVisible = false;
        this.obtenerPerfiles();
      },
      error: (err) => console.error(err)
    });
  }

  toggleEstado(id: number): void {
    this.perfilService.cambiarEstado(id).subscribe({
      next: () => this.obtenerPerfiles(),
      error: (err) => console.error(err)
    });
  }

  eliminarPerfil(id: number): void {
    if (confirm('¿Estás seguro de eliminar este perfil?')) {
      this.perfilService.eliminarPerfil(id).subscribe({
        next: () => this.obtenerPerfiles(),
        error: (err) => {
          console.error(err);
          alert(err.error?.message || 'No se puede eliminar el perfil porque tiene usuarios activos asignados.');
        }
      });
    }
  }

  abrirModalPermisos(perfil: Perfil): void {
    this.perfilIdPermisos = perfil.id!;
    this.perfilNombrePermisos = perfil.nombre;
    this.opciones = [];
    this.opcionesSeleccionadas = new Set();
    this.modalPermisosVisible = true;
    this.perfilService.listarOpciones().subscribe({
      next: (opciones) => {
        this.opciones = opciones;
        this.cdr.detectChanges();
        this.perfilService.obtenerPerfil(perfil.id!).subscribe({
          next: (data) => {
            this.opcionesSeleccionadas = new Set<number>(data.opciones || data.opcionIds || []);
            this.cdr.detectChanges();
          },
          error: () => {}
        });
      },
      error: () => {}
    });
  }

  toggleOpcion(id: number): void {
    if (this.opcionesSeleccionadas.has(id)) {
      this.opcionesSeleccionadas.delete(id);
    } else {
      this.opcionesSeleccionadas.add(id);
    }
  }

  guardarPermisos(): void {
    if (!this.perfilIdPermisos) return;
    const payload = {
      id: this.perfilIdPermisos,
      opciones: Array.from(this.opcionesSeleccionadas).map(id => ({ id }))
    };
    this.perfilService.guardarPerfil(payload).subscribe({
      next: () => {
        this.modalPermisosVisible = false;
        this.obtenerPerfiles();
      },
      error: (err) => console.error(err)
    });
  }
}
