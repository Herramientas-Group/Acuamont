export interface Producto {
    id?: number;
    nombre: string;
    descripcion: string;
    precioCompra: number;
    precioVenta: number;
    stock: number;
    stockSeguridad: number;
    imagen?: string | null;
    categoria?: { id: number; nombre: string; estado: number };
    estado: number;
}
