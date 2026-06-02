import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ComentariosComponent } from './comentarios-component';

describe('ComentariosComponent', () => {
  let component: ComentariosComponent;
  let fixture: ComponentFixture<ComentariosComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ComentariosComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(ComentariosComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
