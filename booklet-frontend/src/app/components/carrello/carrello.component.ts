


import { Component, signal } from '@angular/core';
import { CommonModule, CurrencyPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { CarrelloService } from '../../services/carrello.service';
import { CarrelloDTO, OggettoCarrelloDTO } from '../../models/carrello';

@Component({
  selector: 'app-carrello',
  standalone: true,
  imports: [CommonModule, FormsModule, CurrencyPipe, MatButtonModule, MatIconModule, MatFormFieldModule, MatInputModule],
  templateUrl: './carrello.component.html',
  styleUrls: ['./carrello.component.css']
})
export class CarrelloComponent {
  carrello = signal<CarrelloDTO | null>(null);
  loading = signal<boolean>(true);
  error = signal<string | null>(null);

  constructor(private readonly carrelloService: CarrelloService) {
    this.caricaCarrello();
  }

  get oggetti(): OggettoCarrelloDTO[] {
    return this.carrello()?.oggetti ?? [];
  }

  get totale(): number {
    return this.carrello()?.totale ?? 0;
  }

  get numeroTotale(): number {
    return this.carrello()?.numeroTotaleOggetti ?? 0;
  }

  aggiornaQuantita(isbn: string, value: number): void {
    const quantita = Number.isFinite(value) ? Math.max(0, Math.trunc(value)) : 0;
    this.loading.set(true);
    this.error.set(null);
    this.carrelloService
      .aggiornaQuantita(isbn, quantita)
      .pipe(takeUntilDestroyed())
      .subscribe({
        next: (cart) => {
          this.carrello.set(cart);
          this.loading.set(false);
        },
        error: (err) => this.handleError(err)
      });
  }

  rimuovi(isbn: string): void {
    this.loading.set(true);
    this.error.set(null);
    this.carrelloService
      .rimuoviDalCarrello(isbn)
      .pipe(takeUntilDestroyed())
      .subscribe({
        next: (cart) => {
          this.carrello.set(cart);
          this.loading.set(false);
        },
        error: (err) => this.handleError(err)
      });
  }

  svuota(): void {
    if (this.oggetti.length === 0) {
      return;
    }
    this.loading.set(true);
    this.error.set(null);
    this.carrelloService
      .svuotaCarrello()
      .pipe(takeUntilDestroyed())
      .subscribe({
        next: (cart) => {
          this.carrello.set(cart);
          this.loading.set(false);
        },
        error: (err) => this.handleError(err)
      });
  }

  private caricaCarrello(): void {
    this.loading.set(true);
    this.error.set(null);
    this.carrelloService
      .getCarrello()
      .pipe(takeUntilDestroyed())
      .subscribe({
        next: (cart) => {
          this.carrello.set(cart);
          this.loading.set(false);
        },
        error: (err) => this.handleError(err)
      });
  }

  private handleError(err: unknown): void {
    console.error('Errore durante l\'operazione sul carrello', err);
    this.error.set('Si è verificato un problema nel comunicare con il carrello.');
    this.loading.set(false);
  }
}
