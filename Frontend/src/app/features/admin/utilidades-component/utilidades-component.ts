import { Component, OnInit, ChangeDetectorRef, ViewChild } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TableModule } from 'primeng/table';
import { InputTextModule } from 'primeng/inputtext';
import { ChartModule, UIChart } from 'primeng/chart';
import { ReporteUtilidadVenta, ReporteUtilidadUsuario, ReporteUtilidadProducto } from '../../../shared/interfaces/utilidades';
import { UtilidadesService } from '../../../core/services/utilidades-service';

@Component({
  selector: 'app-utilidades-component',
  standalone: true,
  imports: [CommonModule, FormsModule, TableModule, InputTextModule, ChartModule],
  providers: [DatePipe],
  templateUrl: './utilidades-component.html'
})
export class UtilidadesComponent implements OnInit {
  @ViewChild('chartVentas') chartVentas!: UIChart;
  @ViewChild('chartUsuarios') chartUsuarios!: UIChart;
  @ViewChild('chartProductos') chartProductos!: UIChart;

  activeTab = 0;

  ventas: ReporteUtilidadVenta[] = [];
  usuarios: ReporteUtilidadUsuario[] = [];
  productos: ReporteUtilidadProducto[] = [];

  ventasInicio = '';
  ventasFin = '';
  usuariosInicio = '';
  usuariosFin = '';
  productosInicio = '';
  productosFin = '';

  ventasChartData: any = {};
  ventasChartOptions: any = {};
  usuariosChartData: any = {};
  usuariosChartOptions: any = {};
  productosChartData: any = {};
  productosChartOptions: any = {};

  private colors = ['#0D72C5', '#059669', '#D97706', '#7C3AED', '#DB2777', '#0891B2', '#DC2626', '#65A30D', '#0EA5E9', '#F97316'];

  constructor(
    private utilidadesService: UtilidadesService,
    private cdr: ChangeDetectorRef,
    private datePipe: DatePipe
  ) { }

  ngOnInit(): void {
    this.cargarVentas();
    this.cargarUsuarios();
    this.cargarProductos();
  }

  cargarVentas(): void {
    this.utilidadesService.getUtilidadVentas().subscribe({
      next: (data) => {
        this.ventas = data;
        this.prepararChartVentas();
        this.cdr.detectChanges();
      },
      error: (err) => console.error(err)
    });
  }

  filtrarVentas(): void {
    this.utilidadesService.getUtilidadVentas(this.ventasInicio, this.ventasFin).subscribe({
      next: (data) => {
        this.ventas = data;
        this.prepararChartVentas();
        this.cdr.detectChanges();
      },
      error: (err) => console.error(err)
    });
  }

  limpiarFiltrosVentas(): void {
    this.ventasInicio = '';
    this.ventasFin = '';
    this.cargarVentas();
  }

  private prepararChartVentas(): void {
    const labels = this.ventas.map(v => v.documento);
    this.ventasChartData = {
      labels,
      datasets: [
        {
          label: 'Total Venta',
          data: this.ventas.map(v => v.totalVenta),
          backgroundColor: '#0D72C5',
          borderRadius: 4
        },
        {
          label: 'Utilidad',
          data: this.ventas.map(v => v.utilidad),
          backgroundColor: '#059669',
          borderRadius: 4
        }
      ]
    };
    this.ventasChartOptions = {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: { position: 'top' }
      },
      scales: {
        y: { beginAtZero: true }
      }
    };
  }

  cargarUsuarios(): void {
    this.utilidadesService.getUtilidadUsuarios().subscribe({
      next: (data) => {
        this.usuarios = data;
        this.prepararChartUsuarios();
        this.cdr.detectChanges();
      },
      error: (err) => console.error(err)
    });
  }

  filtrarUsuarios(): void {
    this.utilidadesService.getUtilidadUsuarios(this.usuariosInicio, this.usuariosFin).subscribe({
      next: (data) => {
        this.usuarios = data;
        this.prepararChartUsuarios();
        this.cdr.detectChanges();
      },
      error: (err) => console.error(err)
    });
  }

  limpiarFiltrosUsuarios(): void {
    this.usuariosInicio = '';
    this.usuariosFin = '';
    this.cargarUsuarios();
  }

  private prepararChartUsuarios(): void {
    const labels = this.usuarios.map(u => u.usuario);
    this.usuariosChartData = {
      labels,
      datasets: [
        {
          label: 'Utilidad Total',
          data: this.usuarios.map(u => u.utilidad),
          backgroundColor: this.usuarios.map((_, i) => this.colors[i % this.colors.length]),
          borderRadius: 4
        }
      ]
    };
    this.usuariosChartOptions = {
      indexAxis: 'y',
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: { display: false }
      },
      scales: {
        x: { beginAtZero: true }
      }
    };
  }

  cargarProductos(): void {
    this.utilidadesService.getUtilidadProductos().subscribe({
      next: (data) => {
        this.productos = data;
        this.prepararChartProductos();
        this.cdr.detectChanges();
      },
      error: (err) => console.error(err)
    });
  }

  filtrarProductos(): void {
    this.utilidadesService.getUtilidadProductos(this.productosInicio, this.productosFin).subscribe({
      next: (data) => {
        this.productos = data;
        this.prepararChartProductos();
        this.cdr.detectChanges();
      },
      error: (err) => console.error(err)
    });
  }

  limpiarFiltrosProductos(): void {
    this.productosInicio = '';
    this.productosFin = '';
    this.cargarProductos();
  }

  private prepararChartProductos(): void {
    const top10 = this.productos.slice(0, 10);
    const labels = top10.map(p => p.producto);
    this.productosChartData = {
      labels,
      datasets: [
        {
          label: 'Cantidad Vendida',
          data: top10.map(p => p.cantidadVendida),
          backgroundColor: this.colors[0],
          borderRadius: 4,
          order: 2
        },
        {
          label: 'Utilidad',
          data: top10.map(p => p.utilidad),
          backgroundColor: this.colors[1],
          borderRadius: 4,
          order: 1
        }
      ]
    };
    this.productosChartOptions = {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: { position: 'top' }
      },
      scales: {
        y: { beginAtZero: true }
      }
    };
  }
}
