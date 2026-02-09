import { Component } from '@angular/core';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { HttpClient } from '@angular/common/http';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-dialog-aggiungi-libro',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule
  ],
  templateUrl: './dialog-aggiungi-libro.component.html'
})
export class DialogAggiungiLibroComponent {

  form: any;

  constructor(
    private fb: FormBuilder,
    private http: HttpClient,
    private ref: MatDialogRef<DialogAggiungiLibroComponent>
  ) {
    this.form = this.fb.group({
      titolo: [''],
      autore: [''],
      isbn: [''],
      casaEditrice: [''],
      genere: [''],
      immagineLibro: [''],
      disponibilita: [1],
      prezzo: [0]
    });
  }


  salva() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const raw = this.form.getRawValue();
    const payload = {
      titolo: (raw.titolo ?? '').trim(),
      autore: (raw.autore ?? '').trim(),
      isbn: (raw.isbn ?? '').trim(),
      casaEditrice: (raw.casaEditrice ?? '').trim(),
      genere: (raw.genere ?? '').trim(),
      immagineLibro: (raw.immagineLibro ?? '').trim(),
      disponibilita: raw.disponibilita ?? 1,
      prezzo: raw.prezzo ?? 0
    };

    this.http.post('http://localhost:8080/api/catalogo', payload).subscribe({
      next: () => this.ref.close('added'),
      error: err => {
        console.error('Errore aggiunta libro:', err);
        alert('Errore: ' + (err?.error?.message ?? err?.error ?? err?.message ?? err));
      }
    });
  }


  annulla() {
    this.ref.close();
  }
}
