# 🐠 Acuamont - Sistema de Gestión de Ventas

Sistema web desarrollado para digitalizar y agilizar el proceso de ventas de la tienda **Acuamont**, especializada en acuarismo. Reemplaza el registro manual de ventas y cálculo de utilidades por un flujo automatizado que genera comprobantes digitales y reportes consolidados en tiempo real.

![Estado](https://img.shields.io/badge/estado-en%20desarrollo-yellow)
![Backend](https://img.shields.io/badge/backend-Spring%20Boot-6DB33F?logo=springboot&logoColor=white)
![Frontend](https://img.shields.io/badge/frontend-Angular-DD0031?logo=angular&logoColor=white)
![DB](https://img.shields.io/badge/base%20de%20datos-PostgreSQL-4169E1?logo=postgresql&logoColor=white)
![Licencia](https://img.shields.io/badge/licencia-académica-lightgrey)

---

## 📑 Tabla de contenidos

- [Sobre el proyecto](#-sobre-el-proyecto)
- [Características principales](#-características-principales)
- [Fuera de alcance](#-fuera-de-alcance)
- [Arquitectura](#️-arquitectura)
- [Stack tecnológico](#️-stack-tecnológico)
- [Estructura del repositorio](#-estructura-del-repositorio)
- [Requisitos previos](#-requisitos-previos)
- [Instalación y ejecución local](#-instalación-y-ejecución-local)
- [Flujo de trabajo con Git (GitHub Flow)](#-flujo-de-trabajo-con-git-github-flow)
- [Integración y Entrega Continua (CI/CD)](#-integración-y-entrega-continua-cicd)
- [Infraestructura y despliegue en la nube](#️-infraestructura-y-despliegue-en-la-nube)
- [Gestión del proyecto](#-gestión-del-proyecto)
- [Equipo de desarrollo](#-equipo-de-desarrollo)
- [Documentación adicional](#-documentación-adicional)
- [Licencia](#-licencia)

---

## 📖 Sobre el proyecto

Actualmente, la tienda Acuamont administra sus ventas y el control de utilidades de forma manual, lo cual genera errores de cálculo, pérdida de información y demoras en la atención al cliente. Este sistema busca:

- Digitalizar el registro de ventas y productos.
- Automatizar la generación de comprobantes de pago.
- Consolidar reportes de ventas y utilidades en tiempo real.
- Sentar las bases técnicas para futuros módulos (inventario, personal, clientes).

**Objetivo general:** desarrollar e implementar un sistema web de ventas que facilite el registro de productos vendidos, la emisión de comprobantes y la generación de reportes de ventas y utilidades de forma eficiente, segura y accesible para el negocio.

---

## ✨ Características principales

- 📋 **Listado de productos** disponible para selección rápida durante la venta.
- 🧾 **Registro de ventas** con cálculo automático de subtotales, totales e impuestos.
- 🖨️ **Generación automática de comprobantes** digitales en formato PDF y ticket, con numeración correlativa.
- 📊 **Reporte de ventas** y **listado de ventas** filtrable por fecha, cliente, trabajador o comprobante.
- 💰 **Reporte de utilidades**, calculado como precio de venta menos el costo registrado del producto.
- 📤 **Exportación de reportes** en formato PDF y Excel.
- 🔐 **Autenticación por credenciales únicas** por usuario y acceso restringido a la base de datos.
- 📱 **Acceso responsivo** desde navegadores de escritorio y dispositivos móviles (Chrome, Firefox, Edge).

## 🚫 Fuera de alcance

La versión actual **no** contempla:

- Pasarelas de pago en línea.
- Módulo de e-commerce para clientes finales.
- Integración directa de facturación electrónica con la SUNAT.

---

## 🏗️ Arquitectura

Acuamont utiliza una **arquitectura desacoplada** (cliente-servidor), con separación clara entre presentación y lógica de negocio, comunicadas vía HTTP/REST.

```
┌─────────────────────┐        HTTP/REST        ┌──────────────────────┐        JDBC        ┌─────────────┐
│   Frontend (Angular) │ ───────────────────────▶ │  Backend (Spring Boot) │ ─────────────────▶ │ PostgreSQL  │
│  core / features /   │ ◀─────────────────────── │ controller / service / │ ◀───────────────── │  Base de    │
│      shared          │        JSON/DTO          │  repository / model    │                    │   datos     │
└─────────────────────┘                          └──────────────────────┘                    └─────────────┘
```

### Frontend (Angular) — principio de Separación de Preocupaciones (SoC)

```
src/app/
├── core/          # Interceptores, guardas de rutas y servicios globales
├── shared/        # Componentes, interfaces y pipes reutilizables
├── features/      # Módulos de negocio (home, auth, admin/productos, admin/servicios)
├── app.routes.ts
├── app.component.ts
└── app.config.ts
```

### Backend (Spring Boot) — arquitectura en capas

```
Backend/
├── src/main/java/com/example/acceso/
│   ├── config/       # Seguridad (CORS), beans de Spring
│   ├── controller/   # Endpoints REST
│   ├── model/        # Entidades JPA (Producto, Venta, etc.)
│   ├── repository/   # Interfaces JpaRepository
│   ├── service/      # Lógica de negocio y cálculos (utilidades)
│   ├── DTO/          # Objetos de transferencia Backend ↔ Frontend
│   └── AccesoApplication.java
├── src/main/resources/
├── acceso.sql
└── pom.xml
```

---

## 🛠️ Stack tecnológico

| Capa | Tecnologías |
|---|---|
| **Frontend** | Angular · TypeScript · HTML5 · CSS3 |
| **Backend** | Java 17+ · Spring Boot · Spring Security · Spring Data JPA / Hibernate · Maven |
| **Base de datos** | PostgreSQL |
| **Control de versiones** | Git + GitHub (GitHub Flow) |
| **Gestión y planificación** | ClickUp · GanttProject |
| **Diseño y prototipado** | Draw.io |
| **CI/CD** | Jenkins (pipeline as code) |
| **Análisis de dependencias / seguridad** | Socket.dev (SCA) |
| **Infraestructura** | Microsoft Azure (Backend/VM) · Vercel (Frontend) · Cloudflare (DNS, proxy y túneles) |
| **IDEs recomendados** | WebStorm · Visual Studio Code · IntelliJ IDEA |

---

## 📂 Estructura del repositorio

```
Acuamont/
├── .vscode/              # Configuración del editor
├── Backend/              # API REST en Spring Boot
├── Frontend/             # SPA en Angular
├── .gitignore
├── backup-acceso.sql     # Backup de la base de datos
└── README.md
```

---

## ✅ Requisitos previos

- **Java** 17 o superior
- **Node.js** (LTS recomendado) y **npm**
- **Angular CLI** (`npm install -g @angular/cli`)
- **Maven**
- **PostgreSQL** (local o accesible remotamente)
- **Git**

---

## 🚀 Instalación y ejecución local

### 1. Clonar el repositorio

```bash
git clone https://github.com/Herramientas-Group/Acuamont.git
cd Acuamont
```

### 2. Configurar la base de datos

```bash
# Crear la base de datos y cargar el backup incluido
createdb -U postgres acceso
psql -U postgres -d acceso -f backup-acceso.sql
```

Actualiza las credenciales de conexión en `Backend/src/main/resources/application.properties` (usuario, contraseña, URL de conexión).

### 3. Levantar el Backend (Spring Boot)

```bash
cd Backend
./mvnw spring-boot:run
```

El servidor se expone por defecto en `http://localhost:8080`.

### 4. Levantar el Frontend (Angular)

```bash
cd Frontend
npm install
ng serve
```

La aplicación estará disponible en `http://localhost:4200`.

### 5. Iniciar sesión

Ingresa con las credenciales de usuario configuradas en la base de datos para acceder al panel principal (registro de ventas, listado de ventas, reportes de ventas y utilidades).

---

## 🌿 Flujo de trabajo con Git (GitHub Flow)

El equipo trabaja bajo el modelo **GitHub Flow**: la rama `main` siempre se mantiene estable y lista para producción.

- **`main`** → código de producción, protegido contra *push* directo.
- **`feature/...`** → una rama por funcionalidad (ej. `Feature-Cambio-Angular-Frontend`), creada exclusivamente por el rol DevOps.
- **`fix/...`** → ramas dedicadas a la corrección de errores reportados.

**Reglas de integración:**

1. Todo cambio se desarrolla en una rama secundaria, nunca directo en `main`.
2. Se abre un **Pull Request** por cada rama a integrar.
3. El PR requiere **al menos una aprobación** de otro integrante (QA/Tester o DevOps).
4. La fusión se realiza con **Fast-forward Merge** (`git merge --ff-only`) para mantener un historial lineal.
5. Los commits siguen el estándar **Conventional Commits**:

   ```
   <tipo>(<ámbito opcional>): <descripción breve>
   ```

   | Tipo | Uso |
   |---|---|
   | `feat` | Nueva funcionalidad |
   | `fix` | Corrección de errores |
   | `docs` | Cambios de documentación |
   | `style` | Cambios de formato sin impacto en lógica |
   | `refactor` | Reestructuración sin corregir errores ni añadir funciones |
   | `test` | Pruebas nuevas o corregidas |

### Roles y permisos en GitHub

| Integrante | Rol técnico | Permiso |
|---|---|---|
| John Chapoñan Montaño | DevOps / Backend | `admin` (único con permiso para crear ramas) |
| José Seclén Damián | Backend / QA | `write` |
| Cristian Huamán Cruz | Frontend / QA | `write` |
| Pedro Flores Cholán | Frontend / QA | `write` |

### Release Candidate

Una versión se etiqueta como **Release Candidate** (ej. `v1.0.0-beta`) cuando ha superado las pruebas de integración y usabilidad. A partir de ese punto solo se aceptan *bug fixes*; ninguna nueva funcionalidad ingresa hasta declarar la versión estable.

---

## 🔄 Integración y Entrega Continua (CI/CD)

El pipeline se ejecuta con **Jenkins** ante cada *push* o Pull Request, con ejecución condicional según la carpeta modificada (`/Backend` o `/Frontend`).

**Etapas de Integración Continua (CI):**

1. Checkout del código y preparación del entorno.
2. Compilación de Backend (Maven) y Frontend (npm).
3. Ejecución de pruebas unitarias y de seguridad:

| Vulnerabilidad mitigada | Capa | Enfoque |
|---|---|---|
| Vulnerabilidades en dependencias (Supply Chain) | Full-stack | Análisis de `pom.xml` y `package.json` con **Socket.dev** |
| Inyección SQL | Backend | Validación de parámetros parametrizados por el ORM |
| IDOR (Insecure Direct Object Reference) | Backend | Verificación de permisos a nivel de controlador |
| Fallos de autenticación / JWT | Backend | Auditoría de generación, firma y expiración de tokens |
| Cross-Site Scripting (XSS) | Frontend | Sanitización de entradas antes de renderizar |
| Exposición de tokens | Frontend | Validación del interceptor HTTP y dominios autorizados |

Si alguna prueba falla o Socket.dev detecta una dependencia crítica vulnerable, el pipeline se bloquea automáticamente.

**Etapa de Entrega Continua (CD):** se ejecuta únicamente cuando el cambio se fusiona en `main`.

1. Se genera el artefacto `.jar` validado.
2. Se transfiere vía **SCP/SSH** hacia la máquina virtual de **Microsoft Azure**.
3. Se reinicia el servicio (`systemctl restart acuamont-backend.service`).
4. En paralelo, **Vercel** detecta la fusión y despliega automáticamente el Frontend en Angular.

---

## ☁️ Infraestructura y despliegue en la nube

| Componente | Plataforma | Propósito |
|---|---|---|
| Backend + Base de datos | **Microsoft Azure** (VM) | Control total del entorno para servicios en segundo plano y persistencia de datos |
| Frontend | **Vercel** | Despliegue optimizado de la SPA con CDN global |
| DNS / Proxy / Túneles | **Cloudflare** | Protección de la IP del servidor y exposición segura de Jenkins sin abrir puertos |
| CI local | **Jenkins** (host local) | Evita el consumo de recursos de la VM de producción |

---

## 📋 Gestión del proyecto

- **Planificación y cronograma:** GanttProject (Project Charter, Diagrama de Gantt, WBS — ver `/Anexos` en la documentación).
- **Colaboración, tareas y seguimiento de incidentes:** [ClickUp](https://clickup.com) — tableros Kanban, vista de Carga de Trabajo, Dashboards analíticos y formulario estandarizado de reporte de incidencias.
- **Diseño y prototipado de interfaces:** Draw.io (mockups de login, menú principal, registro de ventas, listado de ventas, reportes de ventas y utilidades).

---

## 👥 Equipo de desarrollo

| Integrante | Rol |
|---|---|
| **John Anderson Chapoñan Montaño** | Coordinador · Desarrollador Backend / DevOps |
| **José Alberto Seclén Damián** | Desarrollador Backend / QA-Tester |
| **Cristian Jesús Huamán Cruz** | Desarrollador Frontend / QA-Tester |
| **Pedro Andrés Flores Cholán** | Desarrollador Frontend / QA-Tester |

---

## 📄 Licencia

Proyecto desarrollado con fines **académicos** para el curso Herramientas de Desarrollo de la Universidad Tecnológica del Perú. Uso educativo, no comercial.
