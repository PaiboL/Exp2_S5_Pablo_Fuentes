import { Component, inject, signal } from "@angular/core";
import { CommonModule } from "@angular/common";
import { ReactiveFormsModule, FormControl, Validators } from "@angular/forms";
import { ActivatedRoute, Router, RouterLink } from "@angular/router";
import { ForoApiService } from "./foro-api.service";
import { AuthFacade, message } from "./core";
import { Topic, Comment } from "./models";
import { finalize, forkJoin } from "rxjs";
@Component({
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: "./topic.page.html",
})
export class TopicPage {
  api = inject(ForoApiService);
  auth = inject(AuthFacade);
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  id = Number(this.route.snapshot.paramMap.get("id"));
  topic = signal<Topic | null>(null);
  comments = signal<Comment[]>([]);
  error = signal("");
  notice = signal("");
  busy = signal(false);
  editing: number | null = null;
  page = 0;
  pages = signal(0);
  comment = new FormControl("", {
    nonNullable: true,
    validators: [
      Validators.required,
      Validators.maxLength(2000),
      Validators.pattern(/[\s\S]*\S[\s\S]*/),
    ],
  });
  constructor() {
    this.load();
  }
  own(id: number) {
    return (
      this.auth.user()?.id === id ||
      ["ADMIN", "MODERATOR"].includes(this.auth.user()?.rol || "")
    );
  }
  load() {
    this.error.set("");
    forkJoin({
      topic: this.api.topic(this.id),
      comments: this.api.comments(this.id, this.page),
    }).subscribe({
      next: (r) => {
        this.topic.set(r.topic);
        this.comments.set(r.comments.content);
        this.pages.set(r.comments.totalPages);
      },
      error: (e) => this.error.set(message(e)),
    });
  }
  save() {
    this.comment.markAsTouched();
    if (this.comment.invalid) return;
    this.busy.set(true);
    this.api
      .saveComment(this.id, this.editing, this.comment.value)
      .pipe(finalize(() => this.busy.set(false)))
      .subscribe({
        next: () => {
          this.comment.reset();
          this.editing = null;
          this.page = 0;
          this.notice.set("Comentario guardado.");
          this.load();
        },
        error: (e) => this.error.set(message(e)),
      });
  }
  edit(c: Comment) {
    this.editing = c.id;
    this.comment.setValue(c.contenido);
    document.getElementById("comentario")?.focus();
  }
  cancel() {
    this.editing = null;
    this.comment.reset();
  }
  deleteComment(id: number) {
    if (!confirm("¿Eliminar este comentario?")) return;
    this.api.deleteComment(id).subscribe({
      next: () => {
        this.notice.set("Comentario eliminado.");
        this.load();
      },
      error: (e) => this.error.set(message(e)),
    });
  }
  deleteTopic() {
    if (!confirm("¿Eliminar el tema y todos sus comentarios?")) return;
    this.api
      .deleteTopic(this.id)
      .subscribe({
        next: () => void this.router.navigateByUrl("/foro"),
        error: (e) => this.error.set(message(e)),
      });
  }
  change(delta: number) {
    this.page += delta;
    this.load();
  }
}
