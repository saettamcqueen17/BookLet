import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

import { Observable } from 'rxjs';
import { CatalogoPersonaleContainerDTO } from '../models/catalogo-personale';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class CatalogoPersonaleService {
  private base = `${environment.apiBase}/api/catalogo-personale`;

  constructor(private http: HttpClient) {}

  getCatalogo(utenteId: string): Observable<CatalogoPersonaleContainerDTO> {
    return this.http.get<CatalogoPersonaleContainerDTO>(`${this.base}/${utenteId}`);
  }
}
