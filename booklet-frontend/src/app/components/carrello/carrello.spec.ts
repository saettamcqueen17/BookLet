import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Carrello } from './carrello';

describe('Carrello', () => {
  let component: Carrello;
  let fixture: ComponentFixture<Carrello>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Carrello]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Carrello);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
