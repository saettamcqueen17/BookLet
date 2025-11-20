import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RedazioneService } from '../../services/redazione.service';
import { SchedaRedazione } from '../../models/catalogo-redazione';
import { AuthService } from '../../services/auth.service';
import {MatCardModule} from '@angular/material/card';
import {RouterModule} from '@angular/router';
import {MatButtonModule} from '@angular/material/button';

@Component({
  selector: 'app-catalogo-redazione',
  standalone: true,
  imports: [CommonModule, MatCardModule, RouterModule, MatButtonModule],
  templateUrl: './catalogo-redazione.component.html',
  styleUrls: ['./catalogo-redazione.component.css']
})
export class CatalogoRedazioneComponent implements OnInit {
  schede: SchedaRedazione[] = [];

  constructor(
    private api: RedazioneService,
    public auth: AuthService
  ) {}

  ngOnInit(): void {
    this.api.getCatalogo().subscribe({
      next: (res) => this.schede = res,
      error: (err) => console.error('Errore nel caricamento:', err)
    });
  }
}
