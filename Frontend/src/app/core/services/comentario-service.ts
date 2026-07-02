import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { Comentario } from '../../shared/interfaces/comentario';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class ComentarioService {
  private apiUrl = `${environment.apiUrl}/comentarios/api`;

  constructor(private http: HttpClient) { }

  listarComentarios(): Observable<Comentario[]> {
    return this.http.get<{ success: boolean; data: Comentario[] }>(`${this.apiUrl}/listar`)
      .pipe(map(res => res.data));
  }

  guardarComentario(formData: FormData): Observable<any> {
    return this.http.post(`${this.apiUrl}/guardar`, formData);
  }
}
