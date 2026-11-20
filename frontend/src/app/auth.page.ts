import { Component, inject, signal } from "@angular/core";
import { CommonModule } from "@angular/common";
import { ReactiveFormsModule, FormBuilder, Validators } from "@angular/forms";
import { ActivatedRoute, Router, RouterLink } from "@angular/router";
import { HttpClient } from "@angular/common/http";
import { finalize } from "rxjs";
import { AuthFacade, message, strongPassword } from "./core";
@Component({
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: "./auth.page.html",
})
export class AuthPage {
  auth = inject(AuthFacade);
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private http = inject(HttpClient);
  private fb = inject(FormBuilder);
  mode = this.route.snapshot.data["mode"] as string;
  busy = signal(false);
  error = signal("");
  notice = signal("");
  sent = signal(false);
  form = this.fb.nonNullable.group({
    nombre: ["", [Validators.required, Validators.maxLength(100)]],
    email: [
      "",
      [Validators.required, Validators.email, Validators.maxLength(150)],
    ],
    password: ["", [strongPassword]],
    confirm: ["", [Validators.required]],
    token: ["", [Validators.required, Validators.maxLength(128)]],
  });
  constructor() {
    if (this.mode === "login") {
      this.form.controls.password.setValidators([
        Validators.required,
        Validators.maxLength(64),
      ]);
    }
  }
  bad(name: "nombre" | "email" | "password" | "confirm" | "token") {
    const c = this.form.controls[name];
    return c.touched && c.invalid;
  }
  submit() {
    this.error.set("");
    const f = this.form.controls;
    const fields =
      this.mode === "register"
        ? ["nombre", "email", "password", "confirm"]
        : this.mode === "login"
          ? ["email", "password"]
          : this.sent()
            ? ["token", "password", "confirm"]
            : ["email"];
    let ok = true;
    for (const name of fields) {
      const c = f[name as keyof typeof f];
      c.markAsTouched();
      if (c.invalid) ok = false;
    }
    const v = this.form.getRawValue();
    if ((this.mode === "register" || this.sent()) && v.password !== v.confirm) {
      this.error.set("Las contraseñas no coinciden.");
      ok = false;
    }
    if (!ok) return;
    this.busy.set(true);
    const obs =
      this.mode === "login"
        ? this.auth.login({ email: v.email, password: v.password })
        : this.mode === "register"
          ? this.auth.register({
              nombre: v.nombre,
              email: v.email,
              password: v.password,
            })
          : this.sent()
            ? this.http.post("/api/auth/restablecer", {
                token: v.token,
                password: v.password,
              })
            : this.http.post("/api/auth/recuperar", { email: v.email });
    obs.pipe(finalize(() => this.busy.set(false))).subscribe({
      next: () => {
        if (this.mode === "login") void this.router.navigateByUrl("/foro");
        else if (this.mode === "register") {
          this.notice.set("Cuenta creada. Ya puedes iniciar sesión.");
          this.form.reset();
        } else if (!this.sent()) {
          this.sent.set(true);
          this.notice.set(
            "Si la cuenta existe, recibirás instrucciones en su correo.",
          );
        } else {
          this.notice.set("Contraseña actualizada. Ya puedes iniciar sesión.");
          this.sent.set(false);
          this.form.reset();
        }
      },
      error: (e) => this.error.set(message(e)),
    });
  }
}
