import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ConfirmationService, MessageService } from 'primeng/api';

import { UsuarioComponent } from './usuario-component';

describe('UsuarioComponent', () => {
  let component: UsuarioComponent;
  let fixture: ComponentFixture<UsuarioComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UsuarioComponent],
      providers: [ConfirmationService, MessageService],
    }).compileComponents();

    fixture = TestBed.createComponent(UsuarioComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
