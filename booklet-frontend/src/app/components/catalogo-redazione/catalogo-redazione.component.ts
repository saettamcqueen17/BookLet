import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth.service';
import { RedazioneService } from '../../services/redazione.service';
import { SchedaRedazione } from '../../models/catalogo-redazione';
import { SchedaRedazioneComponent } from './scheda-redazione/scheda-redazione.component';
import { MatDialog } from '@angular/material/dialog';
import { RedazioneReviewDialogComponent } from './redazione-review-dialog.component';



@Component({
  selector: 'app-catalogo-redazione',
  standalone: true,
  templateUrl: './catalogo-redazione.component.html',
  styleUrls: ['./catalogo-redazione.component.css'],
  imports: [CommonModule, SchedaRedazioneComponent]
})
export class CatalogoRedazioneComponent implements OnInit {

  schede: SchedaRedazione[] = [];
  ruoloRedazione = false;
  loading = true;

  private auth = inject(AuthService);
  private redazioneService = inject(RedazioneService);

  private dialog = inject(MatDialog);


  ngOnInit() {
    this.auth.isLoggedIn().then(logged => {
      console.log("logged:", logged);
      this.initAfterLogin(logged);
    });
  }

  private initAfterLogin(logged: boolean) {
    if (!logged) {
      this.loadCatalogForAnonymous();
      return;
    }

    this.auth.getRolesAsync().then(roles => {
      this.ruoloRedazione = roles.includes('REDAZIONE');
      this.loadCatalogForLoggedUser();
    });
  }

  private loadCatalogForAnonymous() {
    this.redazioneService.getCatalogo().subscribe({
      next: res => {
        this.schede = res.filter(x => x.visibile);
        this.loading = false;
      },
      error: _ => this.loading = false
    });
  }

  private loadCatalogForLoggedUser() {
    this.redazioneService.getCatalogo().subscribe({
      next: res => {
        this.schede = this.ruoloRedazione ? res : res.filter(x => x.visibile);
        this.loading = false;
      },
      error: _ => this.loading = false
    });
  }


  modificaRecensione(s: SchedaRedazione) {
    const dialogRef = this.dialog.open(RedazioneReviewDialogComponent, {
      width: '550px',
      data: {
        isbn: s.isbn,
        titolo: s.titolo,
        autore: s.autore,
        rating: s.valutazioneRedazione,
        recensione: s.recensione
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (!result) return;

      this.redazioneService.updateRecensione(
        s.isbn,
        result.recensione,
        result.valutazione
      ).subscribe({
        next: () => {
          // aggiorno la UI localmente
          s.recensione = result.recensione;
          s.valutazioneRedazione = result.valutazione;


        },
        error: err => console.error(err)
      });
    });
  }

  toggleVisibile(s: SchedaRedazione) {
    const nuovo = !s.visibile;
    this.redazioneService.updateVisibile(s.isbn, nuovo).subscribe({
      next: () => s.visibile = nuovo,
      error: err => console.error('Errore toggle:', err)
    });
  }

  rimuovi(isbn: string) {
    this.redazioneService.rimuoviLibro(isbn).subscribe({
      next: () => this.schede = this.schede.filter(s => s.isbn !== isbn),
      error: err => console.error('Errore rimozione:', err)
    });
  }
}
