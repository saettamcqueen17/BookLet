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

  // 🔹 DICHIARO la proprietà, senza assegnarla
  form: any;

  constructor(
    private fb: FormBuilder,          // 🔥 fb ora esiste al momento dell’assegnazione
    private http: HttpClient,
    private ref: MatDialogRef<DialogAggiungiLibroComponent>
  ) {
    // 🔹 INIZIALIZZO il form qui (dove fb è DEFINITO)
    this.form = this.fb.group({
      titolo: [''],
      autore: [''],
      isbn: [''],
      casaEditrice: [''],
      genere: [''],
      immagineUrl: [''],
      disponibilita: [1],
      prezzo :[0]
    });
  }

  salva() {
    this.http.post('http://localhost:8080/api/catalogo', this.form.value)

      .subscribe({
      next: () => this.ref.close('added'),
      error: err => alert("Errore: " + err.error)
    });
  }

  annulla() {
    this.ref.close();
  }
}
