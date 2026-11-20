import { Component, inject } from "@angular/core";
import { RouterLink, RouterLinkActive, RouterOutlet } from "@angular/router";
import { AuthFacade } from "./core";
@Component({
  selector: "app-root",
  standalone: true,
  imports: [RouterLink, RouterLinkActive, RouterOutlet],
  template: `<a href="#main" class="skip-link">Saltar al contenido</a>
    <header class="site-header">
      <nav class="container nav-inner" aria-label="Navegación principal">
        <a class="brand" [routerLink]="auth.user() ? '/foro' : '/login'"
          ><img src="/logo.svg" width="36" height="36" alt="" />foro<span
            >nexo</span
          ></a
        >
        <div class="nav-links">
          @if (auth.user()) {
            <a routerLink="/foro" routerLinkActive="current">Conversaciones</a>
            @if (auth.isAdmin()) {
              <a routerLink="/admin" routerLinkActive="current"
                >Administración</a
              >
            }
            <a routerLink="/perfil" routerLinkActive="current">Mi perfil</a
            ><button
              class="btn btn-sm btn-outline-dark"
              (click)="auth.logout()"
            >
              Salir
            </button>
          } @else {
            <span class="nav-tag">Aprender es compartir.</span>
          }
        </div>
      </nav>
    </header>
    <main id="main" class="container main-content"><router-outlet /></main>
    <footer class="container site-footer">
      <span>Foro Nexo · Comunidad académica</span
      ><span>Conecta ideas. Construye conocimiento.</span>
    </footer>`,
})
export class AppComponent {
  auth = inject(AuthFacade);
}
