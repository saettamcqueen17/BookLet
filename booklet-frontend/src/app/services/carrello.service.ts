import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface CarrelloItemDTO {
  isbn: string;
  quantita: number;
  titolo : string;
  prezzoUnitario : number;
}

export interface CarrelloDTO {
  oggetti: CarrelloItemDTO[];
  totale: number;
  numeroTotaleOggetti : number ;
}

export interface AggiungiItemRequest {
  isbn: string;
  quantita: number; // allineato al backend
}

export interface AggiornaQuantitaRequest {
  quantita: number; // allineato al backend
}

@Injectable({ providedIn: 'root' })
export class CarrelloService {
  private apiUrl = `${environment.apiBase}/api/me/carrello`;

  constructor(private http: HttpClient) {}

  getCarrello(): Observable<CarrelloDTO> {
    return this.http.get<CarrelloDTO>(this.apiUrl);
  }

  aggiungiAlCarrello(isbn: string, quantita = 1): Observable<CarrelloDTO> {
    const body: AggiungiItemRequest = { isbn, quantita: quantita };
    return this.http.post<CarrelloDTO>(`${this.apiUrl}/items`, body);
  }

  aggiornaQuantita(isbn: string, quantita: number): Observable<CarrelloDTO> {
    const body: AggiornaQuantitaRequest = { quantita: quantita };
    return this.http.patch<CarrelloDTO>(`${this.apiUrl}/items/${encodeURIComponent(isbn)}`, body);
  }

  svuotaCarrello(): Observable<CarrelloDTO> {
    return this.http.delete<CarrelloDTO>(this.apiUrl);
  }

  rimuoviDalCarrello(isbn: string): Observable<CarrelloDTO> {
    return this.http.delete<CarrelloDTO>(`${this.apiUrl}/items/${encodeURIComponent(isbn)}`);
  }
}
