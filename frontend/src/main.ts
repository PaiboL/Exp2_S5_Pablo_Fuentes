import { bootstrapApplication } from "@angular/platform-browser";
import { provideRouter, Routes } from "@angular/router";
import { provideHttpClient, withInterceptors } from "@angular/common/http";
import { AppComponent } from "./app/app.component";
import { logged, admin, authInterceptor } from "./app/core";
const routes: Routes = [
  {
    path: "login",
    loadComponent: () => import("./app/auth.page").then((m) => m.AuthPage),
    data: { mode: "login" },
  },
  {
    path: "registro",
    loadComponent: () => import("./app/auth.page").then((m) => m.AuthPage),
    data: { mode: "register" },
  },
  {
    path: "recuperar",
    loadComponent: () => import("./app/auth.page").then((m) => m.AuthPage),
    data: { mode: "recover" },
  },
  {
    path: "foro",
    loadComponent: () => import("./app/forum.page").then((m) => m.ForumPage),
    canActivate: [logged],
  },
  {
    path: "temas/nuevo",
    loadComponent: () => import("./app/editor.page").then((m) => m.EditorPage),
    canActivate: [logged],
  },
  {
    path: "temas/:id/editar",
    loadComponent: () => import("./app/editor.page").then((m) => m.EditorPage),
    canActivate: [logged],
  },
  {
    path: "temas/:id",
    loadComponent: () => import("./app/topic.page").then((m) => m.TopicPage),
    canActivate: [logged],
  },
  {
    path: "perfil",
    loadComponent: () =>
      import("./app/profile.page").then((m) => m.ProfilePage),
    canActivate: [logged],
  },
  {
    path: "admin",
    loadComponent: () => import("./app/admin.page").then((m) => m.AdminPage),
    canActivate: [admin],
  },
  { path: "", pathMatch: "full", redirectTo: "foro" },
  { path: "**", redirectTo: "foro" },
];
bootstrapApplication(AppComponent, {
  providers: [
    provideRouter(routes),
    provideHttpClient(withInterceptors([authInterceptor])),
  ],
}).catch(console.error);
