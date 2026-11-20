import { Component, inject, signal } from "@angular/core";
import { ReactiveFormsModule, FormBuilder, Validators } from "@angular/forms";
import { Router, ActivatedRoute, RouterLink } from "@angular/router";
import { ForoApiService } from "./foro-api.service";
import { Category } from "./models";
import { message } from "./core";
import { finalize } from "rxjs";
@Component({
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  template: `<a routerLink="/foro" class="back-link"
      >← Volver a las conversaciones</a
    >
    <section class="panel editor">
      <span class="eyebrow">COMPARTE TU PERSPECTIVA</span>
      <h1>{{ id ? "Editar tema" : "Una nueva conversación" }}</h1>
      <p>Un título claro ayuda a que otras personas encuentren tu pregunta.</p>
      @if (error()) {
        <div role="alert" class="alert alert-danger">{{ error() }}</div>
      }
      <form [formGroup]="form" (ngSubmit)="save()">
        <label for="titulo">Título</label
        ><input
          id="titulo"
          class="form-control"
          formControlName="titulo"
          maxlength="150"
        /><label for="categoria">Categoría</label
        ><select
          id="categoria"
          class="form-select"
          formControlName="categoriaId"
        >
          <option [ngValue]="0">Selecciona una categoría</option>
          @for (c of categories(); track c.id) {
            <option [ngValue]="c.id">{{ c.nombre }}</option>
          }</select
        ><label for="contenido">Tu mensaje</label
        ><textarea
          id="contenido"
          class="form-control"
          rows="9"
          formControlName="contenido"
          maxlength="4000"
        ></textarea>
        <div class="field-help">
          {{ form.controls.contenido.value.length }} / 4000 caracteres
        </div>
        @if (form.touched && form.invalid) {
          <p class="invalid">
            Completa título, mensaje y una categoría válida.
          </p>
        }
        <div class="actions">
          <button class="btn btn-primary" [disabled]="busy()">
            {{
              busy() ? "Guardando…" : id ? "Guardar cambios" : "Publicar tema"
            }}</button
          ><a class="btn btn-outline-dark" routerLink="/foro">Cancelar</a>
        </div>
      </form>
    </section>`,
})
export class EditorPage {
  private api = inject(ForoApiService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private fb = inject(FormBuilder);
  id = Number(this.route.snapshot.paramMap.get("id")) || null;
  categories = signal<Category[]>([]);
  error = signal("");
  busy = signal(false);
  form = this.fb.nonNullable.group({
    titulo: [
      "",
      [
        Validators.required,
        Validators.maxLength(150),
        Validators.pattern(/.*\S.*/),
      ],
    ],
    contenido: [
      "",
      [
        Validators.required,
        Validators.maxLength(4000),
        Validators.pattern(/[\s\S]*\S[\s\S]*/),
      ],
    ],
    categoriaId: [0, [Validators.min(1)]],
  });
  constructor() {
    this.api
      .categories()
      .subscribe({
        next: (r) => this.categories.set(r),
        error: (e) => this.error.set(message(e)),
      });
    if (this.id)
      this.api
        .topic(this.id)
        .subscribe({
          next: (t) => this.form.patchValue(t),
          error: (e) => this.error.set(message(e)),
        });
  }
  save() {
    this.form.markAllAsTouched();
    if (this.form.invalid) return;
    this.busy.set(true);
    this.error.set("");
    this.api
      .saveTopic(this.id, this.form.getRawValue())
      .pipe(finalize(() => this.busy.set(false)))
      .subscribe({
        next: (t) => void this.router.navigate(["/temas", t.id]),
        error: (e) => this.error.set(message(e)),
      });
  }
}
