import { inject, Injectable } from "@angular/core";
import { HttpClient, HttpParams } from "@angular/common/http";
import { Category, Topic, Comment, Page, Decision, User } from "./models";
@Injectable({ providedIn: "root" })
export class ForoApiService {
  private http = inject(HttpClient);
  categories() {
    return this.http.get<Category[]>("/api/categorias");
  }
  topics(q = "", category = "", page = 0) {
    let p = new HttpParams().set("q", q).set("page", page).set("size", 8);
    if (category) p = p.set("categoriaId", category);
    return this.http.get<Page<Topic>>("/api/temas", { params: p });
  }
  topic(id: number) {
    return this.http.get<Topic>("/api/temas/" + id);
  }
  saveTopic(id: number | null, data: object) {
    return id
      ? this.http.put<Topic>("/api/temas/" + id, data)
      : this.http.post<Topic>("/api/temas", data);
  }
  deleteTopic(id: number) {
    return this.http.delete("/api/temas/" + id);
  }
  comments(id: number, page = 0) {
    return this.http.get<Page<Comment>>(
      `/api/temas/${id}/comentarios?page=${page}&size=20`,
    );
  }
  saveComment(topic: number, id: number | null, contenido: string) {
    return id
      ? this.http.put<Comment>("/api/comentarios/" + id, { contenido })
      : this.http.post<Comment>(`/api/temas/${topic}/comentarios`, {
          contenido,
        });
  }
  deleteComment(id: number) {
    return this.http.delete("/api/comentarios/" + id);
  }
  users() {
    return this.http.get<Page<User>>("/api/usuarios?size=100");
  }
  adminTopics() {
    return this.http.get<Topic[]>("/api/contenido-admin/temas");
  }
  adminComments() {
    return this.http.get<Comment[]>("/api/contenido-admin/comentarios");
  }
  decisions() {
    return this.http.get<Decision[]>("/api/moderaciones");
  }
  block(tipo: string, recursoId: number, motivo: string) {
    return this.http.post<Decision>("/api/moderaciones", {
      tipo,
      recursoId,
      motivo,
    });
  }
  updateDecision(id: number, motivo: string, bloqueado: boolean) {
    return this.http.put<Decision>("/api/moderaciones/" + id, {
      motivo,
      bloqueado,
    });
  }
  removeDecision(id: number) {
    return this.http.delete("/api/moderaciones/" + id);
  }
  role(id: number, rol: string) {
    return this.http.put("/api/usuarios/" + id + "/rol", { rol });
  }
  deleteUser(id: number) {
    return this.http.delete("/api/usuarios/" + id);
  }
  saveCategory(id: number | null, data: object) {
    return id
      ? this.http.put<Category>("/api/categorias/" + id, data)
      : this.http.post<Category>("/api/categorias", data);
  }
  deleteCategory(id: number) {
    return this.http.delete("/api/categorias/" + id);
  }
}
