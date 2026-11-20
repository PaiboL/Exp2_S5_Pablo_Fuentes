import { TestBed } from "@angular/core/testing";
import { provideHttpClient, withInterceptors } from "@angular/common/http";
import {
  provideHttpClientTesting,
  HttpTestingController,
} from "@angular/common/http/testing";
import { provideRouter } from "@angular/router";
import { FormControl } from "@angular/forms";
import { AuthFacade, authInterceptor, strongPassword } from "./core";
import { ForoFacade } from "./foro.facade";
describe("Contrato HTTP y estado de presentación", () => {
  let h: HttpTestingController;
  beforeEach(() => {
    sessionStorage.clear();
    TestBed.configureTestingModule({
      providers: [
        provideRouter([]),
        provideHttpClient(withInterceptors([authInterceptor])),
        provideHttpClientTesting(),
      ],
    });
    h = TestBed.inject(HttpTestingController);
  });
  afterEach(() => {
    h.verify();
    sessionStorage.clear();
  });
  it("informa fallo de conexión y finaliza la carga", () => {
    const f = TestBed.inject(ForoFacade);
    f.load();
    h.expectOne("/api/categorias").flush([]);
    h.expectOne((r) => r.url === "/api/temas").error(
      new ProgressEvent("error"),
    );
    expect(f.loading()).toBeFalse();
    expect(f.error()).toContain("conectar");
  });
  it("envía búsqueda y categoría codificadas", () => {
    const f = TestBed.inject(ForoFacade);
    f.load("Java & SQL", "2", 1);
    h.expectOne("/api/categorias").flush([]);
    const r = h.expectOne((r) => r.url === "/api/temas");
    expect(r.request.params.get("q")).toBe("Java & SQL");
    expect(r.request.params.get("categoriaId")).toBe("2");
    expect(r.request.params.get("page")).toBe("1");
    r.flush({ content: [], totalElements: 0, totalPages: 0, number: 1 });
    expect(f.error()).toBe("");
    expect(f.loading()).toBeFalse();
  });
  it("login conserva usuario y adjunta Bearer a solicitudes protegidas", () => {
    const a = TestBed.inject(AuthFacade);
    a.login({ email: "a@b.cl", password: "x" }).subscribe();
    const login = h.expectOne("/api/auth/login");
    expect(login.request.headers.has("Authorization")).toBeFalse();
    login.flush({
      accessToken: "prueba",
      expiresIn: 900,
      usuario: { id: 1, nombre: "Admin", email: "a@b.cl", rol: "ADMIN" },
    });
    expect(a.isAdmin()).toBeTrue();
    TestBed.inject(ForoFacade).load();
    const cat = h.expectOne("/api/categorias");
    expect(cat.request.headers.get("Authorization")).toBe("Bearer prueba");
    cat.flush([]);
    h.expectOne((r) => r.url === "/api/temas").flush({
      content: [],
      totalElements: 0,
      totalPages: 0,
      number: 0,
    });
  });
  it("descarta una sesión vencida", () => {
    sessionStorage.setItem(
      "nexo-session",
      JSON.stringify({
        until: 1,
        token: "antiguo",
        user: { id: 1, rol: "ADMIN" },
      }),
    );
    const a = TestBed.inject(AuthFacade);
    expect(a.valid()).toBeFalse();
    expect(a.isAdmin()).toBeFalse();
  });
  it("exige mayúscula minúscula número símbolo y longitud", () => {
    for (const p of [
      "Corta1!",
      "sinmayusculas1!",
      "SINMINUSCULAS1!",
      "SinNumeros!!!",
      "SinSimbolos123",
      "A1!" + "á".repeat(40),
    ])
      expect(strongPassword(new FormControl(p))).not.toBeNull();
    expect(strongPassword(new FormControl("Correcta2026!"))).toBeNull();
  });
});
