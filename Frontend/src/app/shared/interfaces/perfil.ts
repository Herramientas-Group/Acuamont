export interface Perfil {
    id?: number;
    nombre: string;
    descripcion?: string;
    estado: number;
    opcionIds?: number[];
}

export interface Usuario {
    id?: number;
    nombre: string;
    usuario: string;
    clave?: string;
    correo: string;
    estado: number;
    perfil: Perfil;
}

export interface Opcion {
    id: number;
    nombre: string;
    ruta: string;
    icono: string;
}

export interface LoginResponse {
    id: number;
    token: string;
    usuario: string;
    nombre: string;
    perfil: string;
    opciones: Opcion[];
}

export interface Generar2FAResponse {
  secret: string;
  qrCode: string;
}

export interface Verificar2FARequest {
  id: number;
  codigo: string;
  secreto: string;
}
