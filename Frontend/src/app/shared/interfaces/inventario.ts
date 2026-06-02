export interface TipoMovimiento {
  id: number;
  nombre: string;
  estado: number;
}

export interface AjusteInventario {
  id: number;
  fecha: string;
  producto: { id: number; nombre: string };
  tipoMovimiento: TipoMovimiento;
  cantidad: number;
  comentario: string;
}

export interface AjusteInventarioDTO {
  productoId: number;
  tipoMovimientoId: number;
  cantidad: number;
  comentario: string;
}

export interface MovimientoProducto {
  fecha: string;
  documento: string;
  precioVenta: number;
  cantidad: number;
  subtotal: number;
}
