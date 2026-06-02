export interface ReporteUtilidadVenta {
  documento: string;
  cliente: string;
  fecha: string;
  totalVenta: number;
  utilidad: number;
}

export interface ReporteUtilidadUsuario {
  usuario: string;
  cantidadVentas: number;
  utilidad: number;
}

export interface ReporteUtilidadProducto {
  producto: string;
  cantidadVendida: number;
  totalVenta: number;
  utilidad: number;
}
