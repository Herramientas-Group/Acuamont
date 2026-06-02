import { Component, OnInit, ChangeDetectorRef, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TableModule } from 'primeng/table';
import { SelectModule } from 'primeng/select';
import { InputTextModule } from 'primeng/inputtext';
import { DialogModule } from 'primeng/dialog';
import { SortEvent } from 'primeng/api';
import { UsuarioService } from '../../../core/services/usuario-service';
import { LoaderService } from '../../../core/services/loader-service';
import { Usuario, Perfil } from '../../../shared/interfaces/perfil';

@Component({
  selector: 'app-usuarios',
  standalone: true,
  imports: [CommonModule, FormsModule, TableModule, SelectModule, InputTextModule, DialogModule],
  templateUrl: './usuario-component.html'
})
export class UsuarioComponent implements OnInit {
  @ViewChild('dt') dt: any;

  usuarios: Usuario[] = [];
  filteredUsuarios: Usuario[] = [];

  filtroNombre: string = '';
  filtroEstado: number | null = null;
  filtroPerfil: string | null = null;

  estadosFiltro = [
    { label: 'Todos', value: null },
    { label: 'Activo', value: 1 },
    { label: 'Inactivo', value: 0 }
  ];

  perfilesFiltro: { label: string; value: string | null }[] = [];

  isSorted: boolean | null = null;
  private resetting = false;

  modalVisible = false;
  editando = false;
  formUsuario: { id?: number; nombre: string; usuario: string; clave: string; correo: string; perfil: Perfil | null } = {
    nombre: '', usuario: '', clave: '', correo: '', perfil: null
  };
  perfiles: Perfil[] = [];

  modal2FAVisible = false;
  qrCodeSrc = '';
  secretKey = '';
  codigo2FA = '';
  usuario2FAId: number | null = null;

  constructor(
    private usuarioService: UsuarioService,
    private loaderService: LoaderService,
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit(): void {
    this.obtenerUsuarios();
  }

  obtenerUsuarios(): void {
    this.usuarioService.listarUsuarios().subscribe({
      next: (data) => {
        this.usuarios = data;
        this.filteredUsuarios = [...data];
        this.isSorted = null;
        this.perfilesFiltro = [
          { label: 'Todos', value: null },
          ...Array.from(new Set(data.map(u => u.perfil?.nombre).filter(Boolean))).map(nombre => ({
            label: nombre,
            value: nombre
          }))
        ];
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
    this.filteredUsuarios.sort((data1, data2) => {
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
    this.filteredUsuarios = this.usuarios.filter(user => {
      const coincideNombre = !this.filtroNombre ||
        user.nombre.toLowerCase().includes(this.filtroNombre.toLowerCase());
      const coincideEstado = this.filtroEstado === null || user.estado === this.filtroEstado;
      const coincidePerfil = !this.filtroPerfil || user.perfil?.nombre === this.filtroPerfil;
      return coincideNombre && coincideEstado && coincidePerfil;
    });
  }

  limpiarFiltros(): void {
    this.filtroNombre = '';
    this.filtroEstado = null;
    this.filtroPerfil = null;
    this.aplicarFiltros();
  }

  abrirModal(usuario?: Usuario): void {
    if (usuario) {
      this.editando = true;
      this.formUsuario = {
        id: usuario.id,
        nombre: usuario.nombre,
        usuario: usuario.usuario,
        clave: '',
        correo: usuario.correo,
        perfil: usuario.perfil
      };
    } else {
      this.editando = false;
      this.formUsuario = { nombre: '', usuario: '', clave: '', correo: '', perfil: null };
    }
    this.modalVisible = true;
    this.usuarioService.listarPerfiles().subscribe({
      next: (perfiles) => {
        this.perfiles = perfiles;
        this.cdr.detectChanges();
      },
      error: () => {}
    });
  }

  guardarUsuario(): void {
    const payload: any = {
      nombre: this.formUsuario.nombre,
      usuario: this.formUsuario.usuario,
      correo: this.formUsuario.correo,
      perfil: this.formUsuario.perfil
    };
    if (this.editando) {
      payload.id = this.formUsuario.id;
      if (this.formUsuario.clave) {
        payload.clave = this.formUsuario.clave;
      }
    } else {
      payload.clave = this.formUsuario.clave;
    }

    this.usuarioService.guardarUsuario(payload).subscribe({
      next: () => {
        this.modalVisible = false;
        this.obtenerUsuarios();
      },
      error: (err) => console.error(err)
    });
  }

  toggleEstado(id: number): void {
    this.usuarioService.cambiarEstado(id).subscribe({
      next: () => this.obtenerUsuarios(),
      error: (err) => console.error(err)
    });
  }

  eliminarUsuario(id: number): void {
    if (confirm('¿Estás seguro de que deseas eliminar este usuario?')) {
      this.usuarioService.eliminarUsuario(id).subscribe(() => this.obtenerUsuarios());
    }
  }

  abrirModal2FA(usuario: Usuario): void {
    this.usuario2FAId = usuario.id!;
    this.qrCodeSrc = '';
    this.secretKey = '';
    this.codigo2FA = '';
    this.modal2FAVisible = true;
    this.generar2FA();
  }

  generar2FA(): void {
    if (!this.usuario2FAId) return;
    this.usuarioService.generar2FA(this.usuario2FAId).subscribe({
      next: (res) => {
        this.qrCodeSrc = (res as any).qrCode || (res as any).qrCodeUri || '';
        this.secretKey = (res as any).secret || '';
        this.cdr.detectChanges();
      },
      error: (err) => console.error(err)
    });
  }

  verificar2FA(): void {
    if (!this.usuario2FAId || !this.codigo2FA || !this.secretKey) return;
    this.usuarioService.verificar2FA({
      id: this.usuario2FAId,
      codigo: this.codigo2FA,
      secreto: this.secretKey
    }).subscribe({
      next: () => {
        this.modal2FAVisible = false;
        this.obtenerUsuarios();
      },
      error: (err) => console.error(err)
    });
  }
}
