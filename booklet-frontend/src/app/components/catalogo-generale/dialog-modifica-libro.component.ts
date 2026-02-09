import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, FormsModule } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { HttpClient } from '@angular/common/http';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { CommonModule } from '@angular/common';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-dialog-modifica-libro',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    FormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule
  ],
  templateUrl: './dialog-modifica-libro.component.html',
  styleUrls: ['./dialog-modifica-libro.component.css']
})
export class DialogModificaLibroComponent implements OnInit {

  form!: FormGroup;
  isbnDaCercare: string = '';
  libroCaricato = false;
  erroreCaricamento: string | null = null;

  constructor(
    private fb: FormBuilder,
    private http: HttpClient,
    private ref: MatDialogRef<DialogModificaLibroComponent>
  ) {
    this.form = this.fb.group({
      isbn: [{value: '', disabled: true}],
      titolo: [''],
      autore: [''],
      casaEditrice: [''],
      genere: [''],
      immagineLibro: [''],
      disponibilita: [0],
      prezzo: [0]
    });
  }

  ngOnInit() {
  }

  cercaLibro() {
    const isbn = this.isbnDaCercare.trim();
    if (!isbn) {
      this.erroreCaricamento = 'Inserisci un ISBN valido';
      return;
    }

    this.erroreCaricamento = null;

    this.http.get<any>(`${environment.apiBase}/api/catalogo/generale`)
      .subscribe({
        next: (libri) => {
          const libro = libri.find((l: any) => l.isbn === isbn);
          if (libro) {
            this.form.patchValue({
              isbn: libro.isbn,
              titolo: libro.titolo || '',
              autore: libro.autore || '',
              casaEditrice: libro.casaEditrice || '',
              genere: libro.genere || '',
              immagineLibro: libro.immagineLibro || '',
              disponibilita: libro.disponibilita || 0,
              prezzo: libro.prezzo || 0
            });
            this.libroCaricato = true;
          } else {
            this.erroreCaricamento = `Libro con ISBN "${isbn}" non trovato`;
          }
        },
        error: (err) => {
          console.error('Errore caricamento libro:', err);
          this.erroreCaricamento = 'Errore durante il caricamento del libro';
        }
      });
  }

  resetRicerca() {
    this.erroreCaricamento = null;
    this.libroCaricato = false;
    this.isbnDaCercare = '';
  }

  salva() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const raw = this.form.getRawValue();
    const payload = {
      isbn: raw.isbn,
      titolo: (raw.titolo ?? '').trim(),
      autore: (raw.autore ?? '').trim(),
      casaEditrice: (raw.casaEditrice ?? '').trim(),
      genere: (raw.genere ?? '').trim(),
      immagineLibro: (raw.immagineLibro ?? '').trim(),
      disponibilita: raw.disponibilita ?? 0,
      prezzo: raw.prezzo ?? 0
    };

    this.http.put(`${environment.apiBase}/api/catalogo/${payload.isbn}`, payload).subscribe({
      next: () => this.ref.close('updated'),
      error: err => {
        console.error('Errore modifica libro:', err);
        alert('Errore: ' + (err?.error?.message ?? err?.error ?? err?.message ?? err));
      }
    });
  }

  annulla() {
    this.ref.close();
  }
}
