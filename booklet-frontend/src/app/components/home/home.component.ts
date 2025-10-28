// booklet-frontend/src/app/components/home/home.component.ts
import { Component, OnInit } from '@angular/core';
import {CommonModule, NgIf} from '@angular/common';
import {LibroCardComponent} from '../shared/libro-card.component';
import {MatIconModule} from '@angular/material/icon';

@Component({
  selector: 'app-home',
  standalone : true,
  templateUrl: './home.component.html',
  imports: [
    CommonModule,LibroCardComponent,
    MatIconModule,
    NgIf,

  ],
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {
  caricamento: boolean = true;
  catalogoGenerale: any[] = [];

  ngOnInit(): void {
    // sostituire con chiamata reale al servizio per popolare catalogoGenerale
    setTimeout(() => {
      this.catalogoGenerale = []; // riempi con dati reali
      this.caricamento = false;
    }, 500);
  }
}

