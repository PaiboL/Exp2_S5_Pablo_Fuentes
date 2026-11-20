import { Component, inject } from "@angular/core";
import { CommonModule } from "@angular/common";
import { FormsModule } from "@angular/forms";
import { RouterLink } from "@angular/router";
import { ForoFacade } from "./foro.facade";
@Component({
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: "./forum.page.html",
})
export class ForumPage {
  f = inject(ForoFacade);
  q = "";
  category = "";
  page = 0;
  constructor() {
    this.search();
  }
  search() {
    this.page = 0;
    this.load();
  }
  load() {
    this.f.load(this.q, this.category, this.page);
  }
  select(id: number) {
    this.category = String(id);
    this.search();
  }
  change(delta: number) {
    this.page += delta;
    this.load();
  }
}
