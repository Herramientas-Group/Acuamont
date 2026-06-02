import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UtilidadesComponent } from './utilidades-component';

describe('UtilidadesComponent', () => {
  let component: UtilidadesComponent;
  let fixture: ComponentFixture<UtilidadesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UtilidadesComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(UtilidadesComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
