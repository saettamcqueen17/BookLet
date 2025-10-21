import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CatalogoPersonale } from './catalogo-personale';

describe('CatalogoPersonale', () => {
  let component: CatalogoPersonale;
  let fixture: ComponentFixture<CatalogoPersonale>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CatalogoPersonale]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CatalogoPersonale);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
