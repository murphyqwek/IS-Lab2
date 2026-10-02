import { Component, inject, signal } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { finalize } from 'rxjs';
import { AuthService } from '../../services/auth.service';
import { ApiErrorService } from '../../services/api-error.service';

@Component({
  selector: 'app-auth-page',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './auth-page.html',
  styleUrl: './auth-page.scss',
})
export class AuthPageComponent {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly auth = inject(AuthService);
  private readonly errors = inject(ApiErrorService);
  readonly registration = this.route.snapshot.data['registration'] === true;
  readonly busy = signal(false);
  readonly message = signal('');
  readonly serverErrors = signal<Record<string, string>>({});
  readonly form = new FormGroup({
    username: new FormControl('', { nonNullable: true, validators: this.registration
      ? [Validators.required, Validators.pattern(/\S/), Validators.minLength(4), Validators.maxLength(50)] : [Validators.required] }),
    password: new FormControl('', { nonNullable: true, validators: this.registration
      ? [Validators.required, Validators.minLength(8), Validators.maxLength(25)] : [Validators.required] }),
    confirmPassword: new FormControl('', { nonNullable: true, validators: this.registration ? [Validators.required] : [] }),
  }, { validators: group => this.registration && group.get('password')?.value !== group.get('confirmPassword')?.value ? { mismatch: true } : null });

  fieldError(name: 'username' | 'password' | 'confirmPassword'): string {
    if (this.serverErrors()[name]) return this.serverErrors()[name];
    const field = this.form.controls[name];
    if (!field.touched) return '';
    if (field.hasError('required') || field.hasError('pattern')) return 'Заполните это поле';
    if (field.hasError('minlength') || field.hasError('maxlength')) return name === 'username'
      ? 'Логин должен содержать от 4 до 50 символов' : 'Пароль должен содержать от 8 до 25 символов';
    if (name === 'confirmPassword' && this.form.hasError('mismatch')) return 'Пароли не совпадают';
    return '';
  }

  clearError(name: string): void {
    this.serverErrors.update(errors => { const result = { ...errors }; delete result[name]; return result; });
    this.message.set('');
  }

  submit(): void {
    if (this.busy()) return;

    this.serverErrors.set({});
    this.message.set('');
    this.form.markAllAsTouched();

    if (this.form.invalid) return;

    this.busy.set(true);

    const { username, password } = this.form.getRawValue();
    const request = this.registration ? this.auth.register({ username, password }) : this.auth.login({ username, password });
    
    request.pipe(finalize(() => this.busy.set(false))).subscribe({
      next: () => {
        const target = this.route.snapshot.queryParamMap.get('returnUrl');
        void this.router.navigateByUrl(target && /^\/(?!\/)/.test(target) && !/^\/(login|register)([/?]|$)/.test(target) ? target : '/tickets');
      },
      error: (error: HttpErrorResponse) => {
        const fields: Record<string, string> = {};
        const body = error.error;
        const raw = body?.errors ?? body?.fieldErrors ?? body;
        if (Array.isArray(raw)) {
          raw.forEach(item => {
            const key = item?.field === 'login' ? 'username' : item?.field;
            if (key && typeof (item.message ?? item.defaultMessage) === 'string') fields[key] = item.message ?? item.defaultMessage;
          });
        } else if (raw && typeof raw === 'object') {
          Object.entries(raw).forEach(([key, value]) => {
            const name = key === 'login' ? 'username' : key;
            if (['username', 'password', 'confirmPassword'].includes(name)) {
              const text = Array.isArray(value) ? value.filter(v => typeof v === 'string').join('. ') : value;
              if (typeof text === 'string') fields[name] = text;
            }
          });
        }
        this.serverErrors.set(fields);
        this.message.set(!this.registration && [401, 403].includes(error.status)
          ? 'Неверный логин или пароль' : this.errors.message(error));
      },
    });
  }
}
