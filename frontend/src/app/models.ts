export interface User {
  id: number;
  nombre: string;
  email: string;
  rol: string;
}
export interface Category {
  id: number;
  nombre: string;
  descripcion: string;
}
export interface Topic {
  id: number;
  titulo: string;
  contenido: string;
  autorId: number;
  categoriaId: number;
  categoria: string;
  creadoEn: string;
  bloqueado: boolean;
}
export interface Comment {
  id: number;
  contenido: string;
  autorId: number;
  temaId: number;
  creadoEn: string;
  bloqueado: boolean;
}
export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
}
export interface Decision {
  id: number;
  tipo: string;
  recursoId: number;
  motivo: string;
  bloqueado: boolean;
  fecha: string;
  actorId: number;
}
