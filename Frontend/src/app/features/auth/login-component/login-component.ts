import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth-service';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-login-component',
  standalone: true,
  imports: [ReactiveFormsModule, FormsModule, CommonModule],
  templateUrl: './login-component.html'
})
export class LoginComponent implements OnInit {
  loginForm!: FormGroup;
  errorMessage = '';
  paso2FA = false;
  codigo2FA = '';
  private credencialesGuardadas: { usuario: string; clave: string } | null = null;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit(): void {
    this.loginForm = this.fb.group({
      usuario: ['', [Validators.required, Validators.minLength(3)]],
      clave: ['', [Validators.required]],
    });
  }

  onSubmit(): void {
    if (this.loginForm.invalid) return;
    this.errorMessage = '';

    if (this.paso2FA) {
      this.verificar2FA();
      return;
    }

    const { usuario, clave } = this.loginForm.value;
    this.credencialesGuardadas = { usuario, clave };

    this.authService.login({ usuario, clave }).subscribe({
      next: () => {
        this.router.navigate(['/admin/dashboard']);
      },
      error: (err) => {
        const mensaje = err.error?.message || '';
        if (mensaje === 'Se requiere el token de seguridad') {
          this.paso2FA = true;
          this.errorMessage = '';
          this.cdr.detectChanges();
        } else {
          this.errorMessage = 'Usuario o contraseña incorrectos';
          this.cdr.detectChanges();
        }
      }
    });
  }

  private verificar2FA(): void {
    if (!this.credencialesGuardadas || this.codigo2FA.length < 6) return;

    this.authService.login({
      ...this.credencialesGuardadas,
      token: this.codigo2FA
    }).subscribe({
      next: () => {
        this.router.navigate(['/admin/dashboard']);
      },
      error: (err) => {
        this.errorMessage = 'Código de seguridad incorrecto';
        this.codigo2FA = '';
        this.cdr.detectChanges();
      }
    });
  }

  volverAlLogin(): void {
    this.paso2FA = false;
    this.codigo2FA = '';
    this.errorMessage = '';
    this.credencialesGuardadas = null;
    this.cdr.detectChanges();
  }
}
