import { inject, Injectable, signal, computed } from "@angular/core";
import {
  HttpClient,
  HttpErrorResponse,
  HttpInterceptorFn,
} from "@angular/common/http";
import { Router, CanActivateFn } from "@angular/router";
import { catchError, tap, throwError } from "rxjs";
import { AbstractControl, ValidationErrors } from "@angular/forms";
import { User } from "./models";
export function message(e: unknown): string {
  const h = e as HttpErrorResponse;
  return h.status === 0
    ? "No podemos conectar con el servidor. Intenta nuevamente."
    : h.error?.error ||
        h.error?.mensaje ||
        "No se pudo completar la operación.";
}
export function strongPassword(c: AbstractControl): ValidationErrors | null {
  const p = c.value || "";
  return p.length >= 10 &&
    p.length <= 64 &&
    new TextEncoder().encode(p).length <= 72 &&
    /[A-Z]/.test(p) &&
    /[a-z]/.test(p) &&
    /[0-9]/.test(p) &&
    /[^a-zA-Z0-9]/.test(p)
    ? null
    : { password: true };
}
@Injectable({ providedIn: "root" })
export class AuthFacade {
  private http = inject(HttpClient);
  private router = inject(Router);
  user = signal<User | null>(null);
  token = signal("");
  until = 0;
  isAdmin = computed(() => this.user()?.rol === "ADMIN");
  constructor() {
    try {
      const s = JSON.parse(sessionStorage.getItem("nexo-session") || "null");
      if (s && s.until > Date.now()) {
        this.user.set(s.user);
        this.token.set(s.token);
        this.until = s.until;
      }
    } catch {
      sessionStorage.removeItem("nexo-session");
    }
  }
  private persist() {
    sessionStorage.setItem(
      "nexo-session",
      JSON.stringify({
        user: this.user(),
        token: this.token(),
        until: this.until,
      }),
    );
  }
  valid() {
    if (this.until > Date.now() && this.token()) return true;
    this.user.set(null);
    this.token.set("");
    sessionStorage.removeItem("nexo-session");
    return false;
  }
  login(data: object) {
    return this.http
      .post<{
        accessToken: string;
        expiresIn: number;
        usuario: User;
      }>("/api/auth/login", data)
      .pipe(
        tap((r) => {
          this.user.set(r.usuario);
          this.token.set(r.accessToken);
          this.until = Date.now() + r.expiresIn * 1000;
          this.persist();
        }),
      );
  }
  register(data: object) {
    return this.http.post<User>("/api/auth/registro", data);
  }
  profile(data: object) {
    return this.http.put<User>("/api/usuarios/" + this.user()!.id, data).pipe(
      tap((u) => {
        this.user.set(u);
        this.persist();
      }),
    );
  }
  logout() {
    this.user.set(null);
    this.token.set("");
    this.until = 0;
    sessionStorage.removeItem("nexo-session");
    void this.router.navigateByUrl("/login");
  }
}
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const auth = inject(AuthFacade);
  const open = req.url.startsWith("/api/auth/");
  const r =
    auth.token() && !open
      ? req.clone({ setHeaders: { Authorization: "Bearer " + auth.token() } })
      : req;
  return next(r).pipe(
    catchError((e) => {
      if (e.status === 401 && !open) auth.logout();
      return throwError(() => e);
    }),
  );
};
export const logged: CanActivateFn = () => {
  const a = inject(AuthFacade);
  return a.valid() || inject(Router).createUrlTree(["/login"]);
};
export const admin: CanActivateFn = () => {
  const a = inject(AuthFacade);
  return (a.valid() && a.isAdmin()) || inject(Router).createUrlTree(["/foro"]);
};
