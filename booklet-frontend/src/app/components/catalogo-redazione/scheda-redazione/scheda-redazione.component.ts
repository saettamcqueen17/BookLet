import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { SchedaRedazione } from '../../../models/catalogo-redazione';

@Component({
  selector: 'app-scheda-redazione',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatButtonModule,
    MatSlideToggleModule
  ],
  templateUrl: './scheda-redazione.component.html',
  styleUrls: ['./scheda-redazione.component.css']
})
export class SchedaRedazioneComponent {
  @Input() isRedazione: boolean = false;

  @Input() scheda!: SchedaRedazione;

  @Output() modifica = new EventEmitter<SchedaRedazione>();
  @Output() toggle = new EventEmitter<SchedaRedazione>();
  @Output() rimuovi = new EventEmitter<string>();
}

