import { Cliente } from './cliente';
import { Producto } from './producto';

export interface SerieComprobante {
    id: number;
    nombre: string;
    serie: string;
    correlativo_actual: number;
    estado: number;
}

export interface FormaPago {
    id: number;
    nombre: string;
    estado: number;
}

export interface Venta {
    id: number;
    serieComprobante: SerieComprobante;
    correlativo: number;
    cliente: Cliente;
    usuario: { id: number; nombre: string; usuario: string };
    fecha: string;
    total: number;
    formaPago: FormaPago;
    deuda: number;
    estado: number;
    detalleVentas?: DetalleVenta[];
    cuotas?: Cuota[];
}

export interface DetalleVenta {
    id?: number;
    producto: Producto;
    cantidad: number;
    precioUnitario: number;
    subtotal: number;
}

export interface Cuota {
    id: number;
    numeroCuota: number;
    monto: number;
    saldo: number;
    fechaVencimiento: string;
    estado: number;
}

export interface Pago {
    id: number;
    cuota: { id: number; numeroCuota: number };
    montoPagado: number;
    fechaPago: string;
    metodoPago: string;
    comentario: string;
}

export interface VentaDTO {
    clienteId: number;
    usuarioId: number;
    serieComprobanteId: number;
    formaPagoId: number;
    detalles: DetalleVentaDTO[];
    montoInicial?: number;
    planDeCuotas?: CuotasProgramadasDTO[];
}

export interface DetalleVentaDTO {
    productoId: number;
    cantidad: number;
}

export interface CuotasProgramadasDTO {
    monto: number;
    fechaVencimiento: string;
}

export interface PagosDTO {
    cuotaId: number;
    montoPagado: number;
    comentario: string;
    metodoPago: string;
}
