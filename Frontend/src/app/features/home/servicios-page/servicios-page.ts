import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NavbarComponent } from '../navbar-component/navbar-component';
import { FooterComponent } from '../footer-component/footer-component';

@Component({
  selector: 'app-servicios-page',
  standalone: true,
  imports: [CommonModule, NavbarComponent, FooterComponent],
  templateUrl: './servicios-page.html',
})
export class ServiciosPage {
  servicios = [
    {
      nombre: 'Peces Ornamentales',
      descripcion: 'Gran variedad de especies tropicales y de agua fría, con asesoría para elegir las más adecuadas según tu acuario.',
      icono: 'https://res.cloudinary.com/dukl00gcz/image/upload/v1780381878/fish_isrhbc.png'
    },
    {
      nombre: 'Instalación de Filtros y Motores',
      descripcion: 'Montaje profesional de sistemas de filtración y oxigenación para mantener el agua limpia y saludable.',
      icono: 'https://res.cloudinary.com/dukl00gcz/image/upload/v1780381878/instalacion_qkmh3a.png'
    },
    {
      nombre: 'Mantenimiento de Acuarios',
      descripcion: 'Servicio de limpieza, cambio de agua y revisión del estado de los peces y accesorios.',
      icono: 'https://res.cloudinary.com/dukl00gcz/image/upload/v1780381877/engranajes_nmt8tu.png'
    },
    {
      nombre: 'Decoración de Acuarios',
      descripcion: 'Asesoría y venta de adornos, plantas naturales o artificiales y rocas para crear ambientes únicos.',
      icono: 'https://res.cloudinary.com/dukl00gcz/image/upload/v1780381876/hojas_wubh1r.png'
    },
    {
      nombre: 'Tratamiento y Medicamentos para Peces',
      descripcion: 'Productos especializados para cuidar la salud y prevenir enfermedades en tus peces.',
      icono: 'https://res.cloudinary.com/dukl00gcz/image/upload/v1780381876/medical_a3doqk.png'
    },
    {
      nombre: 'Asesoría Personalizada',
      descripcion: 'Orientación para principiantes y expertos sobre el cuidado de especies, compatibilidad y diseño de acuarios.',
      icono: 'https://res.cloudinary.com/dukl00gcz/image/upload/v1780381875/asesor_eazjot.png'
    },
  ];
}
