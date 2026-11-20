import { Component, inject, signal } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { DatePipe } from "@angular/common";
import { forkJoin, Observable, finalize } from "rxjs";
import { ForoApiService } from "./foro-api.service";
import { AuthFacade, message } from "./core";
import { User, Category, Topic, Comment, Decision } from "./models";
@Component({
  standalone: true,
  imports: [FormsModule, DatePipe],
  templateUrl: "./admin.page.html",
})
export class AdminPage {
  api = inject(ForoApiService);
  auth = inject(AuthFacade);
  tab = "contenido";
  error = signal("");
  notice = signal("");
  busy = signal(false);
  users = signal<User[]>([]);
  categories = signal<Category[]>([]);
  topics = signal<Topic[]>([]);
  comments = signal<Comment[]>([]);
  decisions = signal<Decision[]>([]);
  categoryId: number | null = null;
  nombre = "";
  descripcion = "";
  target: { tipo: string; id: number } | null = null;
  motivo = "";
  constructor() {
    this.load();
  }
  load() {
    this.busy.set(true);
    forkJoin({
      u: this.api.users(),
      c: this.api.categories(),
      t: this.api.adminTopics(),
      m: this.api.adminComments(),
      d: this.api.decisions(),
    })
      .pipe(finalize(() => this.busy.set(false)))
      .subscribe({
        next: (r) => {
          this.users.set(r.u.content);
          this.categories.set(r.c);
          this.topics.set(r.t);
          this.comments.set(r.m);
          this.decisions.set(r.d);
        },
        error: (e) => this.error.set(message(e)),
      });
  }
  run(o: Observable<unknown>, msg: string) {
    this.busy.set(true);
    this.error.set("");
    this.notice.set("");
    o.pipe(finalize(() => this.busy.set(false))).subscribe({
      next: () => {
        this.notice.set(msg);
        this.target = null;
        this.load();
      },
      error: (e) => this.error.set(message(e)),
    });
  }
  choose(tipo: string, id: number) {
    this.target = { tipo, id };
    this.motivo = "";
  }
  block() {
    if (!this.target || !this.motivo.trim() || this.motivo.length > 300) {
      this.error.set("Escribe un motivo de hasta 300 caracteres.");
      return;
    }
    this.run(
      this.api.block(this.target.tipo, this.target.id, this.motivo.trim()),
      "Contenido bloqueado.",
    );
  }
  toggle(d: Decision) {
    this.run(
      this.api.updateDecision(d.id, d.motivo, !d.bloqueado),
      "Estado de moderación actualizado.",
    );
  }
  remove(d: Decision) {
    if (confirm("¿Eliminar esta decisión y restaurar el contenido?"))
      this.run(this.api.removeDecision(d.id), "Decisión eliminada.");
  }
  role(u: User, r: string) {
    this.run(
      this.api.role(u.id, r),
      "Rol actualizado. Se aplicará al iniciar una nueva sesión.",
    );
  }
  deleteUser(u: User) {
    if (confirm("¿Eliminar la cuenta de " + u.nombre + "?"))
      this.run(this.api.deleteUser(u.id), "Cuenta eliminada.");
  }
  edit(c: Category) {
    this.categoryId = c.id;
    this.nombre = c.nombre;
    this.descripcion = c.descripcion;
  }
  clear() {
    this.categoryId = null;
    this.nombre = "";
    this.descripcion = "";
  }
  saveCategory() {
    if (!this.nombre.trim() || !this.descripcion.trim()) {
      this.error.set("Completa nombre y descripción.");
      return;
    }
    this.run(
      this.api.saveCategory(this.categoryId, {
        nombre: this.nombre.trim(),
        descripcion: this.descripcion.trim(),
      }),
      "Categoría guardada.",
    );
    this.clear();
  }
  deleteCategory(c: Category) {
    if (confirm("¿Eliminar la categoría " + c.nombre + "?"))
      this.run(this.api.deleteCategory(c.id), "Categoría eliminada.");
  }
}
