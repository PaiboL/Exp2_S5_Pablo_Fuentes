import { inject, Injectable, signal } from "@angular/core";
import { forkJoin, finalize, Subscription } from "rxjs";
import { ForoApiService } from "./foro-api.service";
import { Category, Topic } from "./models";
import { message } from "./core";
@Injectable({ providedIn: "root" })
export class ForoFacade {
  private pending?: Subscription;
  private api = inject(ForoApiService);
  categories = signal<Category[]>([]);
  topics = signal<Topic[]>([]);
  loading = signal(false);
  error = signal("");
  total = signal(0);
  pages = signal(0);
  load(q = "", category = "", page = 0) {
    this.pending?.unsubscribe();
    this.loading.set(true);
    this.error.set("");
    this.pending = forkJoin({
      categories: this.api.categories(),
      topics: this.api.topics(q, category, page),
    })
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: (r) => {
          this.categories.set(r.categories);
          this.topics.set(r.topics.content);
          this.total.set(r.topics.totalElements);
          this.pages.set(r.topics.totalPages);
        },
        error: (e) => this.error.set(message(e)),
      });
  }
}
