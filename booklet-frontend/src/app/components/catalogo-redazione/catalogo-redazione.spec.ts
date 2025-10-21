import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CatalogoRedazione } from './catalogo-redazione';

describe('CatalogoRedazione', () => {
  let component: CatalogoRedazione;
  let fixture: ComponentFixture<CatalogoRedazione>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CatalogoRedazione]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CatalogoRedazione);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
