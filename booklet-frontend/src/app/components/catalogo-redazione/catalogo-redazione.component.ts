import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';

import { AuthService } from '../../services/auth.service';
import { AuthStateService } from '../../services/AuthStatusService';
import { RedazioneService } from '../../services/redazione.service';

import { SchedaRedazione } from '../../models/catalogo-redazione';
import { SchedaRedazioneComponent } from './scheda-redazione.component';

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

  private auth = inject(AuthService);
  private authState = inject(AuthStateService);
  private redazioneService = inject(RedazioneService);

  async ngOnInit() {

    console.log("Catalogo Redazione — ngOnInit() PARTITO");

    // 🔥 Aspetto che AuthService sia stabilizzato
    await this.auth.isLoggedIn();

    const roles = await this.auth.getRolesAsync();
    console.log("Ruoli:", roles);

    this.ruoloRedazione = roles.includes("REDAZIONE");

    this.redazioneService.getCatalogo().subscribe(res => {

      if (this.ruoloRedazione) {
        this.schede = res; // tutti i libri
      } else {
        this.schede = res.filter(x => x.visibile); // solo visibili
      }

      console.log("Catalogo finale:", this.schede);
    });

  }


  modificaRecensione(s: SchedaRedazione) {
    console.log("Modifica:", s);
  }

  toggleVisibile(s: SchedaRedazione) {
    const nuovo = !s.visibile;
    this.redazioneService.updateVisibile(s.isbn, nuovo).subscribe({
      next: () => s.visibile = nuovo,
      error: err => console.error("Errore toggle:", err)
    });
  }

  rimuovi(isbn: string) {
    this.redazioneService.rimuoviLibro(isbn).subscribe({
      next: () => this.schede = this.schede.filter(s => s.isbn !== isbn),
      error: err => console.error("Errore rimozione:", err)
    });
  }
}
