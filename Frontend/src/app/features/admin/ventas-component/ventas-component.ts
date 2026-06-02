import { Component, OnInit, ChangeDetectorRef, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TableModule } from 'primeng/table';
import { SelectModule } from 'primeng/select';
import { InputTextModule } from 'primeng/inputtext';
import { InputNumberModule } from 'primeng/inputnumber';
import { DialogModule } from 'primeng/dialog';
import { AutoCompleteModule } from 'primeng/autocomplete';
import { TagModule } from 'primeng/tag';
import { ConfirmDialogModule } from 'primeng/confirmdialog';
import { SortEvent, ConfirmationService } from 'primeng/api';
import { Venta, SerieComprobante, FormaPago, Cuota, Pago, VentaDTO, CuotasProgramadasDTO } from '../../../shared/interfaces/venta';
import { Producto } from '../../../shared/interfaces/producto';
import { VentasService } from '../../../core/services/ventas-service';
import { ClientesService } from '../../../core/services/clientes-service';
import { ProductoService } from '../../../core/services/producto-service';
import { AuthService } from '../../../core/services/auth-service';
import { LoaderService } from '../../../core/services/loader-service';

interface DetalleForm {
  producto: Producto;
  cantidad: number;
  precioUnitario: number;
  subtotal: number;
}

interface CuotaForm {
  monto: number;
  fechaVencimiento: string;
}

@Component({
  selector: 'app-ventas-component',
  standalone: true,
  imports: [
    CommonModule, FormsModule, TableModule, SelectModule, InputTextModule,
    InputNumberModule, DialogModule, AutoCompleteModule,
    TagModule, ConfirmDialogModule
  ],
  providers: [ConfirmationService],
  templateUrl: './ventas-component.html'
})
export class VentasComponent implements OnInit {
  @ViewChild('dt') dt: any;

  estadosFiltro = [
    { label: 'Todos', value: null },
    { label: 'Activo', value: 1 },
    { label: 'Pendiente', value: 0 },
    { label: 'Anulado', value: 2 }
  ];

  isSorted: boolean | null = null;
  private resetting = false;

  ventas: Venta[] = [];
  filteredVentas: Venta[] = [];
  filtroNombre: string = '';
  filtroEstado: number | null = null;

  formasPago: FormaPago[] = [];
  seriesComprobante: SerieComprobante[] = [];
  todosProductos: Producto[] = [];

  modalVentaVisible = false;
  clienteId: number | null = null;
  documento: string = '';
  nombreCliente: string = '';
  serieComprobanteId: number | null = null;
  correlativoVisual: string = '';
  formaPagoId: number | null = null;
  detalles: DetalleForm[] = [];
  montoInicial: number | null = null;
  planDeCuotas: CuotaForm[] = [];
  nuevaCuotaMonto: number | null = null;
  nuevaCuotaFecha: string = '';
  buscandoCliente = false;

  busquedaProducto: string = '';
  productosSugeridos: Producto[] = [];
  productoSeleccionado: Producto | null = null;

  esCredito = false;
  guardando = false;
  selectedVentaId: number | null = null;

  cuotasModalVisible = false;
  cuotasVentaId: number | null = null;
  cuotasClienteNombre: string = '';
  cuotasVentaTotal: number = 0;
  cuotas: Cuota[] = [];

  pagosModalVisible = false;
  pagosVentaId: number | null = null;
  pagosClienteNombre: string = '';
  pagos: Pago[] = [];

  registrarPagoModalVisible = false;
  selectedCuota: Cuota | null = null;
  pagoMetodo: string = '';
  pagoComentario: string = '';

  constructor(
    private ventasService: VentasService,
    private clientesService: ClientesService,
    private productoService: ProductoService,
    private authService: AuthService,
    private loaderService: LoaderService,
    private confirmationService: ConfirmationService,
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit(): void {
    this.cargarDatosIniciales();
  }

  cargarDatosIniciales(): void {
    this.ventasService.listarVentas().subscribe({
      next: (data) => {
        this.ventas = data;
        this.filteredVentas = [...data];
        this.isSorted = null;
        this.cdr.detectChanges();
      },
      error: (err) => console.error(err)
    });
    this.ventasService.listarFormasPago().subscribe({
      next: (data) => { this.formasPago = data; this.cdr.detectChanges(); },
      error: (err) => console.error(err)
    });
    this.ventasService.listarSeriesComprobante().subscribe({
      next: (data) => { this.seriesComprobante = data; this.cdr.detectChanges(); },
      error: (err) => console.error(err)
    });
    this.productoService.listarProductos().subscribe({
      next: (data) => { this.todosProductos = data.filter(p => p.estado === 1); this.cdr.detectChanges(); },
      error: (err) => console.error(err)
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
    this.filteredVentas.sort((data1, data2) => {
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
    this.filteredVentas = this.ventas.filter(v => {
      const coincideNombre = !this.filtroNombre ||
        (v.cliente?.nombre?.toLowerCase().includes(this.filtroNombre.toLowerCase()) ?? false);
      const coincideEstado = this.filtroEstado === null || v.estado === this.filtroEstado;
      return coincideNombre && coincideEstado;
    });
  }

  limpiarFiltros(): void {
    this.filtroNombre = '';
    this.filtroEstado = null;
    this.aplicarFiltros();
  }

  getTotalVenta(): number {
    return this.detalles.reduce((sum, d) => sum + (d.cantidad * d.precioUnitario), 0);
  }

  abrirModalNuevaVenta(): void {
    this.selectedVentaId = null;
    this.clienteId = null;
    this.documento = '';
    this.nombreCliente = '';
    this.serieComprobanteId = null;
    this.correlativoVisual = '';
    this.formaPagoId = null;
    this.detalles = [];
    this.montoInicial = null;
    this.planDeCuotas = [];
    this.nuevaCuotaMonto = null;
    this.nuevaCuotaFecha = '';
    this.busquedaProducto = '';
    this.productoSeleccionado = null;
    this.esCredito = false;
    this.modalVentaVisible = true;
  }

  onSerieComprobanteChange(): void {
    const serie = this.seriesComprobante.find(s => s.id === this.serieComprobanteId);
    if (serie) {
      this.correlativoVisual = `${serie.serie} - ${serie.correlativo_actual + 1}`;
    } else {
      this.correlativoVisual = '';
    }
  }

  onFormaPagoChange(): void {
    const fp = this.formasPago.find(f => f.id === this.formaPagoId);
    this.esCredito = fp?.nombre?.toLowerCase().includes('credito') ?? false;
    if (!this.esCredito) {
      this.montoInicial = null;
      this.planDeCuotas = [];
    }
  }

  buscarClientePorDocumento(): void {
    const doc = this.documento.trim();
    if (!doc) return;
    this.buscandoCliente = true;
    this.clientesService.buscarPorDocumento(doc).subscribe({
      next: (res) => {
        if (res.success && res.data) {
          this.clienteId = res.data.id!;
          this.nombreCliente = res.data.nombre;
        }
        this.buscandoCliente = false;
      },
      error: () => {
        this.buscandoCliente = false;
      }
    });
  }

  filtrarProductos(event: any): void {
    const query = event.query?.toLowerCase() || '';
    this.productosSugeridos = this.todosProductos.filter(p =>
      p.nombre.toLowerCase().includes(query) &&
      !this.detalles.some(d => d.producto.id === p.id)
    );
  }

  onProductoSelect(event: any): void {
    const prod: Producto = event.value || event;
    if (!prod?.id) return;
    if (this.detalles.some(d => d.producto.id === prod.id)) return;
    this.detalles.push({
      producto: prod,
      cantidad: 1,
      precioUnitario: prod.precioVenta,
      subtotal: prod.precioVenta
    });
    this.busquedaProducto = '';
    this.productoSeleccionado = null;
    this.productosSugeridos = [];
    this.cdr.detectChanges();
  }

  actualizarSubtotal(detalle: DetalleForm): void {
    detalle.subtotal = detalle.cantidad * detalle.precioUnitario;
  }

  eliminarDetalle(index: number): void {
    this.detalles.splice(index, 1);
  }

  agregarCuota(): void {
    if (!this.nuevaCuotaMonto || !this.nuevaCuotaFecha) return;
    this.planDeCuotas.push({
      monto: this.nuevaCuotaMonto,
      fechaVencimiento: this.nuevaCuotaFecha
    });
    this.nuevaCuotaMonto = null;
    this.nuevaCuotaFecha = '';
  }

  eliminarCuota(index: number): void {
    this.planDeCuotas.splice(index, 1);
  }

  guardarVenta(): void {
    if (!this.clienteId || !this.serieComprobanteId || !this.formaPagoId || this.detalles.length === 0) return;

    const usuarioId = this.authService.getUsuarioId();
    if (!usuarioId) { console.error('No se encontró el usuario logueado'); return; }

    const dto: VentaDTO = {
      clienteId: this.clienteId,
      usuarioId: usuarioId,
      serieComprobanteId: this.serieComprobanteId,
      formaPagoId: this.formaPagoId,
      detalles: this.detalles.map(d => ({ productoId: d.producto.id!, cantidad: d.cantidad }))
    };

    if (this.esCredito) {
      dto.montoInicial = this.montoInicial ?? 0;
      dto.planDeCuotas = this.planDeCuotas.map(c => ({
        monto: c.monto,
        fechaVencimiento: c.fechaVencimiento
      }));
    }

    this.guardando = true;
    const request = this.selectedVentaId
      ? this.ventasService.actualizarVenta(this.selectedVentaId, dto)
      : this.ventasService.guardarVenta(dto);

    request.subscribe({
      next: () => {
        this.modalVentaVisible = false;
        this.guardando = false;
        this.cargarDatosIniciales();
      },
      error: (err) => {
        console.error(err);
        this.guardando = false;
      }
    });
  }

  confirmarAnularVenta(venta: Venta): void {
    this.confirmationService.confirm({
      message: `¿Estás seguro de anular la venta #${venta.id}?`,
      header: 'Anular Venta',
      icon: 'pi pi-exclamation-triangle',
      acceptLabel: 'Sí, anular',
      rejectLabel: 'Cancelar',
      accept: () => {
        this.ventasService.eliminarVenta(venta.id).subscribe({
          next: () => this.cargarDatosIniciales(),
          error: (err) => console.error(err)
        });
      }
    });
  }

  abrirModalCuotas(venta: Venta): void {
    this.cuotasVentaId = venta.id;
    this.cuotasClienteNombre = venta.cliente?.nombre ?? '';
    this.cuotasVentaTotal = venta.total;
    this.cuotas = [];
    this.cuotasModalVisible = true;
    this.ventasService.listarCuotas(venta.id).subscribe({
      next: (data) => { this.cuotas = data; this.cdr.detectChanges(); },
      error: (err) => console.error(err)
    });
  }

  abrirModalPagos(venta: Venta): void {
    this.pagosVentaId = venta.id;
    this.pagosClienteNombre = venta.cliente?.nombre ?? '';
    this.pagos = [];
    this.pagosModalVisible = true;
    this.ventasService.listarPagos(venta.id).subscribe({
      next: (data) => { this.pagos = data; this.cdr.detectChanges(); },
      error: (err) => console.error(err)
    });
  }

  abrirModalRegistrarPago(cuota: Cuota): void {
    this.selectedCuota = cuota;
    this.pagoMetodo = '';
    this.pagoComentario = '';
    this.registrarPagoModalVisible = true;
  }

  registrarPago(): void {
    if (!this.selectedCuota || !this.pagoMetodo) return;
    this.ventasService.registrarPago({
      cuotaId: this.selectedCuota.id,
      montoPagado: this.selectedCuota.saldo,
      comentario: this.pagoComentario,
      metodoPago: this.pagoMetodo
    }).subscribe({
      next: () => {
        this.registrarPagoModalVisible = false;
        if (this.cuotasVentaId) {
          this.ventasService.listarCuotas(this.cuotasVentaId).subscribe({
            next: (data) => { this.cuotas = data; this.cdr.detectChanges(); this.cargarDatosIniciales(); },
            error: (err) => console.error(err)
          });
        }
      },
      error: (err) => console.error(err)
    });
  }

  descargarPDF(id: number): void {
    this.ventasService.descargarBoleta(id);
  }

  enviarCorreo(event: Event, id: number): void {
    event.stopPropagation();
    this.ventasService.enviarBoletaCorreo(id).subscribe({
      error: (err) => console.error(err)
    });
  }

  getEstadoSeverity(estado: number): 'success' | 'warn' | 'danger' | 'info' {
    if (estado === 1) return 'success';
    if (estado === 0) return 'warn';
    if (estado === 2) return 'danger';
    return 'info';
  }

  getEstadoLabel(estado: number): string {
    if (estado === 1) return 'Activo';
    if (estado === 0) return 'Pendiente';
    if (estado === 2) return 'Anulado';
    return 'Desconocido';
  }

  getEstadoCuota(estado: number): string {
    return estado === 1 ? 'Pagado' : 'Pendiente';
  }

  getCuotaSeverity(estado: number): 'success' | 'warn' {
    return estado === 1 ? 'success' : 'warn';
  }

  formatMonto(value: number): string {
    return `S/ ${value.toFixed(2)}`;
  }
}
