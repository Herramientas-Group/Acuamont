export interface Perfil {
    id?: number;
    nombre: string;
    estado: number;
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
    token: string;
    usuario: string;
    nombre: string;
    perfil: string;
    opciones: Opcion[];
}
