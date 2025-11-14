import { Component, signal , OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { map, distinctUntilChanged, filter, switchMap } from 'rxjs/operators';
import { CatalogoPersonaleService } from '../../services/catalogoPersonale.service';
import { CatalogoPersonaleContainerDTO } from '../../models/catalogo-personale';
import { PersonalBookCardComponent } from './personal-card.component';

@Component({
  selector: 'app-catalogo-personale',
  standalone: true,
  imports: [CommonModule, RouterModule, PersonalBookCardComponent],
  templateUrl: './catalogo-personale.component.html',
  styleUrls: ['./catalogo-personale.component.css']
})
export class CatalogoPersonaleComponent implements OnInit {
  catalogo = signal<CatalogoPersonaleContainerDTO | null>(null);
  loading = signal<boolean>(true);
  error = signal<string | null>(null);

  constructor(private readonly route: ActivatedRoute, private readonly service: CatalogoPersonaleService) {
    this.route.paramMap
      .pipe(
        takeUntilDestroyed(),
        map((params) => params.get('utenteId')),
        filter((id): id is string => !!id),
        distinctUntilChanged(),
        switchMap((id) => {
          this.loading.set(true);
          this.error.set(null);
          return this.service.getCatalogo();
        })
      )
      .subscribe({
        next: (data) => {
          this.catalogo.set(data);
          this.loading.set(false);
        },
        error: (err) => {
          console.error('Errore nel recupero del catalogo personale', err);
          this.error.set('Impossibile caricare il catalogo personale richiesto.');
          this.loading.set(false);
        }
      });
  }


    ngOnInit(): void {
      this.loading.set(true);
      this.error.set(null);

      this.service.getCatalogo().subscribe({
        next: (data) => {
          this.catalogo.set(data);
          this.loading.set(false);
        },
        error: (err) => {
          console.error('Errore nel caricamento del catalogo personale:', err);
          this.error.set('Errore nel caricamento del catalogo personale');
          this.loading.set(false);
        }
      })
    }




  get totaleLibri(): number {
    return this.catalogo()?.totaleLibri ?? 0;
  }
}
