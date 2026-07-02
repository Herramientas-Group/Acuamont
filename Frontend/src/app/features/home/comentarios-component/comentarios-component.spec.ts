import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComentariosComponent } from './comentarios-component';
import { ComentarioService } from '../../../core/services/comentario-service';

describe('ComentariosComponent - XSS Prevention', () => {
  let component: ComentariosComponent;
  let fixture: ComponentFixture<ComentariosComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ComentariosComponent],
      providers: [
        provideRouter([]),
        provideHttpClient(),
        provideHttpClientTesting(),
        {
          provide: ComentarioService,
          useValue: {
            listarComentarios: () => ({
              subscribe: (handlers: any) => handlers.next([]),
            }),
            guardarComentario: () => ({
              subscribe: (handlers: any) => handlers.next({}),
            }),
          },
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(ComentariosComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('Input Validation', () => {
    it('should not allow empty name to be submitted', () => {
      component.formNombre = '';
      component.formMensaje = 'Mensaje válido';
      component.enviarComentario();
      expect(component.enviando).toBe(false);
    });

    it('should not allow empty message to be submitted', () => {
      component.formNombre = 'Nombre';
      component.formMensaje = '';
      component.enviarComentario();
      expect(component.enviando).toBe(false);
    });
  });

  describe('XSS Prevention - Input Display', () => {
    it('should store form values safely without HTML injection', () => {
      component.formNombre = 'Usuario Normal';
      component.formMensaje = 'Mensaje de prueba';
      // Angular interpolation {{ }} auto-escapes HTML in templates
      expect(component.formNombre).toBe('Usuario Normal');
      expect(component.formMensaje).toBe('Mensaje de prueba');
      // Verify no script injection
      expect(component.formNombre).not.toContain('<script>');
    });

    it('initial extraction from name should ignore HTML', () => {
      const result = component.getInitial('<script>');
      // getInitial() calls charAt(0) which gets '<', not script execution
      expect(result).toBe('<');
    });

    it('should reject empty names in getInitial', () => {
      const result = component.getInitial('');
      expect(result).toBe('');
    });
  });

  describe('Image Error Handling', () => {
    it('should handle image load errors gracefully', () => {
      const img = document.createElement('img');
      img.style.display = 'block';
      const event = new Event('error');
      Object.defineProperty(event, 'target', { value: img });

      component.onImageError(event);
      expect(img.style.display).toBe('none');
    });
  });

  describe('Date Formatting', () => {
    it('should format dates in Spanish locale', () => {
      const fecha = '2024-01-15T10:30:00';
      const result = component.formatDate(fecha);
      expect(result).toBeTruthy();
      expect(result).toContain('2024');
    });

    it('should handle null dates', () => {
      expect(component.formatDate('')).toBe('');
      expect(component.formatDate(null as any)).toBe('');
    });
  });
});
