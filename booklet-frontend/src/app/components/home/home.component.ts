// booklet-frontend/src/app/components/home/home.component.ts
import { Component, Inject, OnInit, PLATFORM_ID } from '@angular/core';
import { CommonModule, isPlatformServer, NgIf } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { TransferState, makeStateKey } from '@angular/core';
import { firstValueFrom } from 'rxjs';

import { Libro } from '../../models/libro';
import { CatalogoGeneraleService, Page } from '../../services/catalogoGenerale.service';
import { LibroCardComponent } from '../shared/libro-card.component';

const HOME_CATALOG_STATE_KEY = makeStateKey<Page<Libro>>('home.catalogo-generale');

@Component({
  selector: 'app-home',
  standalone: true,
  templateUrl: './home.component.html',
  imports: [
    CommonModule,
    LibroCardComponent,
    MatIconModule,
    NgIf,

  ],
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {
  caricamento = true;
  catalogoGenerale: Libro[] = [];
  erroreCaricamento = false;

  constructor(
    private readonly catalogoService: CatalogoGeneraleService,
    private readonly transferState: TransferState,
    @Inject(PLATFORM_ID) private readonly platformId: Object,
  ) {
  }

  async ngOnInit(): Promise<void> {
    const cached = this.transferState.get<Page<Libro> | null>(HOME_CATALOG_STATE_KEY, null);
    if (cached) {
      this.catalogoGenerale = cached.content;
      this.caricamento = false;
      this.transferState.remove(HOME_CATALOG_STATE_KEY);
      return;
    }

    try {
      const response = await firstValueFrom(this.catalogoService.lista(0, 24, 'titolo'));
      this.catalogoGenerale = response.content;

      if (isPlatformServer(this.platformId)) {
        this.transferState.set(HOME_CATALOG_STATE_KEY, response);
      }
    } catch (error) {
      console.error('Errore durante il caricamento del catalogo generale', error);
      this.erroreCaricamento = true;
    } finally {
      this.caricamento = false;

    }
  }

}
