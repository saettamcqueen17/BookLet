import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CatalogoGenerale } from './catalogo-generale';

describe('CatalogoGenerale', () => {
  let component: CatalogoGenerale;
  let fixture: ComponentFixture<CatalogoGenerale>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CatalogoGenerale]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CatalogoGenerale);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
