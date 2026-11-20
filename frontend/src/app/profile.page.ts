import { Component, inject, signal } from "@angular/core";
import { ReactiveFormsModule, FormBuilder, Validators } from "@angular/forms";
import { AuthFacade, message } from "./core";
import { finalize } from "rxjs";
@Component({
  standalone: true,
  imports: [ReactiveFormsModule],
  template: `<section class="panel editor">
    <span class="eyebrow">TU ESPACIO</span>
    <h1>Mi perfil</h1>
    <p>Actualiza cómo te presentas en la comunidad.</p>
    <span class="pill">{{
      auth.user()?.rol === "ADMIN"
        ? "Administrador"
        : auth.user()?.rol === "MODERATOR"
          ? "Moderador"
          : "Miembro"
    }}</span>
    @if (error()) {
      <div class="alert alert-danger mt-3" role="alert">{{ error() }}</div>
    }
    @if (notice()) {
      <div class="alert alert-success mt-3" role="status">{{ notice() }}</div>
    }
    <form [formGroup]="form" (ngSubmit)="save()">
      <label for="perfil-nombre">Nombre</label
      ><input
        id="perfil-nombre"
        class="form-control"
        formControlName="nombre"
        maxlength="100"
      /><label for="perfil-email">Correo electrónico</label
      ><input
        id="perfil-email"
        class="form-control"
        type="email"
        formControlName="email"
        maxlength="150"
      />
      @if (form.touched && form.invalid) {
        <p class="invalid">Revisa el nombre y el correo electrónico.</p>
      }
      <button class="btn btn-primary mt-4" [disabled]="busy()">
        Guardar perfil
      </button>
    </form>
  </section>`,
})
export class ProfilePage {
  auth = inject(AuthFacade);
  private fb = inject(FormBuilder);
  error = signal("");
  notice = signal("");
  busy = signal(false);
  form = this.fb.nonNullable.group({
    nombre: [
      this.auth.user()?.nombre || "",
      [
        Validators.required,
        Validators.maxLength(100),
        Validators.pattern(/.*\S.*/),
      ],
    ],
    email: [
      this.auth.user()?.email || "",
      [Validators.required, Validators.email, Validators.maxLength(150)],
    ],
  });
  save() {
    this.form.markAllAsTouched();
    if (this.form.invalid) return;
    this.busy.set(true);
    this.error.set("");
    this.auth
      .profile(this.form.getRawValue())
      .pipe(finalize(() => this.busy.set(false)))
      .subscribe({
        next: () => this.notice.set("Perfil actualizado."),
        error: (e) => this.error.set(message(e)),
      });
  }
}
