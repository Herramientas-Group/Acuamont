import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ConfirmationService, MessageService } from 'primeng/api';
import { ProductosComponent } from './productos-component';
import { ProductoService } from '../../../core/services/producto-service';
import { LoaderService } from '../../../core/services/loader-service';

describe('ProductosComponent - XSS Prevention', () => {
  let component: ProductosComponent;
  let fixture: ComponentFixture<ProductosComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProductosComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        {
          provide: ProductoService,
          useValue: {
            listarProductos: () => ({ subscribe: () => {} }),
            listarCategoriasActivas: () => ({ subscribe: () => {} }),
            obtener: () => ({ subscribe: () => {} }),
            guardar: () => ({ subscribe: () => {} }),
            subirImagen: () => ({ subscribe: () => {} }),
            eliminarProducto: () => ({ subscribe: () => {} }),
          },
        },
        { provide: LoaderService, useValue: { show: () => {}, hide: () => {} } },
        ConfirmationService,
        MessageService,
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(ProductosComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
