import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth.service';
import { RedazioneService } from '../../services/redazione.service';
import { SchedaRedazione } from '../../models/catalogo-redazione';
import { SchedaRedazioneComponent } from './scheda-redazione/scheda-redazione.component';

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
  loading = true;   // all'inizio sto caricando

  private auth = inject(AuthService);
  private redazioneService = inject(RedazioneService);

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
    console.log('Modifica:', s);
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
