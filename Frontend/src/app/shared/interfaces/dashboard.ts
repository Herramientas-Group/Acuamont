export interface DashboardResumen {
  totalUsuarios: number;
  totalCategorias: number;
  totalProductos: number;
  totalVentasDia: number;
  totalVentasMes: number;
  totalDeuda: number;
  cuentasPorCobrar: CuentaPorCobrar[];
  topProductos: ProductoTop[];
}

export interface CuentaPorCobrar {
  idVenta: number;
  cliente: string;
  fecha: string;
  deuda: number;
}

export interface ProductoTop {
  producto: string;
  cantidadVendida: number;
}
