-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Base de datos:                acceso
-- --------------------------------------------------------

-- Crear la base de datos (en PostgreSQL no existe IF NOT EXISTS de forma nativa)
CREATE DATABASE acceso;

-- Conectar a la base de datos
\c acceso;

-- Volcando estructura para tabla acceso.ajustes_inventario
CREATE TABLE IF NOT EXISTS ajustes_inventario (
    id SERIAL PRIMARY KEY,
    cantidad INTEGER NOT NULL,
    comentario VARCHAR(250),
    fecha TIMESTAMP(6) NOT NULL,
    id_producto BIGINT NOT NULL,
    id_tipo_movimiento BIGINT NOT NULL
);

-- Volcando estructura para tabla acceso.categorias
CREATE TABLE IF NOT EXISTS categorias (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    estado INTEGER NOT NULL
);

-- Volcando datos para la tabla acceso.categorias
INSERT INTO categorias (id, nombre, estado) VALUES
    (1, 'Peces', 1),
    (2, 'Alimentos', 1),
    (3, 'Decoraciones', 1),
    (4, 'Medicamentos', 1),
    (5, 'Extra1', 2),
    (7, 'Categoria de prueba', 2)
ON CONFLICT (id) DO NOTHING;

-- Volcando estructura para tabla acceso.clientes
CREATE TABLE IF NOT EXISTS clientes (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    documento VARCHAR(255) NOT NULL UNIQUE,
    telefono VARCHAR(9) NOT NULL,
    estado INTEGER NOT NULL,
    correo VARCHAR(255) NOT NULL
);

-- Volcando datos para la tabla acceso.clientes
INSERT INTO clientes (id, nombre, documento, telefono, estado, correo) VALUES
    (1, 'CLIENTES VARIOS', '00000000', '', 1, ''),
    (2, 'IRMA DEL CARMEN CORIA SANCHEZ', '60051938', '913048859', 2, 'prueba@email.com'),
    (3, 'JOHN ANDERSON CHAPOÑAN MONTAÑO', '60051937', '934679698', 1, 'johnandersonchaponanm@gmail.com'),
    (4, 'GABRIELA ALEXANDRA TABOADA MIMBELA', '70497204', '931680722', 1, 'gtaboadamimbela@gmail.com'),
    (5, 'CORPORACION JARD S.A.C.', '20613407279', '999888777', 1, 'corporacion@email.com'),
    (8, 'Cliente Prueba', '12345678', '', 2, ''),
    (9, 'LUIS ANTONIO CHAFLOQUE AVELLANEDA', '41681372', '', 1, ''),
    (10, 'MAKRO SUPERMAYORISTA S.A.', '20492092313', '', 1, ''),
    (11, 'COMPAÑIA FOOD RETAIL S.A.C.', '20608300393', '', 1, ''),
    (12, 'JOSE ALBERTO SECLEN DAMIAN', '75108944', '', 1, 'josedamianseclen10@gmail.com'),
    (13, 'JULIO DANIEL PISFIL PUYEN', '60051947', '', 1, '')
ON CONFLICT (id) DO NOTHING;

-- Volcando estructura para tabla acceso.comentarios
CREATE TABLE IF NOT EXISTS comentarios (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    comentario TEXT NOT NULL,
    imagen_url VARCHAR(255),
    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Volcando datos para la tabla acceso.comentarios
INSERT INTO comentarios (id, nombre, comentario, imagen_url, fecha) VALUES
    (1, 'María Campos', 'Instalamos un acuario en nuestro restaurante y todos los clientes 10 comentan. La calidad de los peces y accesorios es excelente.', 'https://res.cloudinary.com/dukl00gcz/image/upload/v1765310805/comentarios_acuamont/xy5gtszeeyqwmow2ioql.webp', '2025-12-09 20:06:44'),
    (5, 'Luis Martínes', 'Acuamont me ayudó a montar mi primer acuario con todo lo que necesitaba. Los peces están sanos y el equipo me dio excelentes consejos.', 'https://res.cloudinary.com/dukl00gcz/image/upload/v1765311243/comentarios_acuamont/d0dafhqjmfanewt3tkho.jpg', '2025-12-09 20:14:02')
ON CONFLICT (id) DO NOTHING;

-- Volcando estructura para tabla acceso.forma_pago
CREATE TABLE IF NOT EXISTS forma_pago (
    id SERIAL PRIMARY KEY,
    estado INTEGER NOT NULL,
    nombre VARCHAR(100) NOT NULL
);

-- Volcando datos para la tabla acceso.forma_pago
INSERT INTO forma_pago (id, estado, nombre) VALUES
    (1, 1, 'Contado'),
    (2, 1, 'Credito')
ON CONFLICT (id) DO NOTHING;

-- Volcando estructura para tabla acceso.opciones
CREATE TABLE IF NOT EXISTS opciones (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    ruta VARCHAR(100) NOT NULL,
    icono VARCHAR(50)
);

-- Volcando datos para la tabla acceso.opciones
INSERT INTO opciones (id, nombre, ruta, icono) VALUES
    (1, 'Dashboard', '/', 'bi-house-fill'),
    (2, 'Gestión de Usuarios', '/usuarios/listar', 'bi-person-fill-gear'),
    (3, 'Gestión de Perfiles', '/perfiles/listar', 'bi-file-lock-fill'),
    (4, 'Gestión de Categorias', '/categorias/listar', 'bi-bookmark-plus-fill'),
    (5, 'Gestion de Productos', '/productos/listar', 'bi-archive-fill'),
    (6, 'Gestión de Web', '/slides/listar', 'bi-globe-americas-fill'),
    (7, 'Gestión de Clientes', '/clientes/listar', 'bi-people-fill'),
    (8, 'Gestión de Ventas', '/ventas/listar', 'bi-cart-plus-fill'),
    (9, 'Gestión Inventario', '/inventario/listar', 'bi-box-seam-fill'),
    (10, 'Gestión Proveedores', '/proveedores/listar', 'bi-building-fill-gear'),
    (11, 'Reportes Utilidades', '/reportes/listar', 'bi-file-bar-graph-fill')
ON CONFLICT (id) DO NOTHING;

-- Volcando estructura para tabla acceso.perfiles
CREATE TABLE IF NOT EXISTS perfiles (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    descripcion VARCHAR(255),
    estado INTEGER NOT NULL
);

-- Volcando datos para la tabla acceso.perfiles
INSERT INTO perfiles (id, nombre, descripcion, estado) VALUES
    (1, 'Administrador', 'Acceso total al sistema.', 1),
    (2, 'Editor', 'Puede gestionar usuarios pero no perfiles.', 1),
    (3, 'Supervisor', 'Solo puede visualizar información.', 1)
ON CONFLICT (id) DO NOTHING;

-- Volcando estructura para tabla acceso.perfil_opcion
CREATE TABLE IF NOT EXISTS perfil_opcion (
    id_perfil BIGINT NOT NULL,
    id_opcion BIGINT NOT NULL,
    PRIMARY KEY (id_perfil, id_opcion)
);

-- Volcando datos para la tabla acceso.perfil_opcion
INSERT INTO perfil_opcion (id_perfil, id_opcion) VALUES
    (1, 1), (2, 1), (3, 1),
    (1, 2), (2, 2), (3, 2),
    (1, 3),
    (1, 4), (3, 4),
    (1, 5), (3, 5),
    (1, 6), (1, 7), (1, 8), (1, 9), (1, 10), (1, 11)
ON CONFLICT (id_perfil, id_opcion) DO NOTHING;

-- Volcando estructura para tabla acceso.productos
CREATE TABLE IF NOT EXISTS productos (
    id SERIAL PRIMARY KEY,
    descripcion VARCHAR(255) NOT NULL,
    estado INTEGER NOT NULL,
    imagen TEXT,
    nombre VARCHAR(100) NOT NULL,
    precio_compra DOUBLE PRECISION NOT NULL,
    precio_venta DOUBLE PRECISION NOT NULL,
    stock INTEGER NOT NULL,
    stock_seguridad INTEGER NOT NULL,
    id_categoria BIGINT,
    CONSTRAINT productos_check_1 CHECK (precio_compra >= 0),
    CONSTRAINT productos_check_2 CHECK (precio_venta >= 0),
    CONSTRAINT productos_check_3 CHECK (stock >= 0),
    CONSTRAINT productos_check_4 CHECK (stock_seguridad >= 0)
);

-- Volcando datos para la tabla acceso.productos
INSERT INTO productos (id, descripcion, estado, imagen, nombre, precio_compra, precio_venta, stock, stock_seguridad, id_categoria) VALUES
    (1, 'Hogar divertido y vibrante para tus peces. Réplica de la famosa casa piña, ¡el escondite perfecto!', 1, '["5d9ce63a-0291-4178-9c7f-cf3e84f52b7a_73cfc240-9f9e-4dd7-906c-dc8a26a8910e_piña.jpg"]', 'Piña Bob Esponja', 12, 25, 0, 2, 3),
    (2, 'Medusa simulada que flota y añade un efecto hipnótico y color neón a tu acuario.', 1, '["083f4e11-424a-445d-9fff-f876da2ae677_e23c31c8-ff92-4de3-9028-58a9a9583ebe_6fa597f8-3469-4b79-8240-14f3a6b1527a_IMG-20250903-WA0008.jpg"]', 'Medusa Pequeña', 6, 15, 8, 2, 3),
    (5, 'Avioneta', 1, '["e45c1a47-3d7e-4384-a48d-6af25f694af6_b1eb5e57-19dd-458c-880b-dfe7b379fba4_07ce4954-83a6-4122-a599-0491137bc447_IMG-20250903-WA0012.jpg"]', 'Avioneta', 12, 22, 2, 1, 3)
ON CONFLICT (id) DO NOTHING;

-- Volcando estructura para tabla acceso.proveedor
CREATE TABLE IF NOT EXISTS proveedor (
    id SERIAL PRIMARY KEY,
    correo VARCHAR(255) UNIQUE,
    documento VARCHAR(11) NOT NULL,
    estado INTEGER NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    telefono VARCHAR(9)
);

-- Volcando datos para la tabla acceso.proveedor
INSERT INTO proveedor (id, correo, documento, estado, nombre, telefono) VALUES
    (1, 'foodretail@email.com', '20608300393', 1, 'COMPAÑIA FOOD RETAIL S.A.C.', '999999999'),
    (2, '', '20462509236', 1, 'UNIVERSIDAD TECNOLOGICA DEL PERU S.A.C. O UTP S.A.C.', '')
ON CONFLICT (id) DO NOTHING;

-- Volcando estructura para tabla acceso.red_social
CREATE TABLE IF NOT EXISTS red_social (
    id SERIAL PRIMARY KEY,
    estado INTEGER,
    icono VARCHAR(255),
    nombre VARCHAR(255),
    orden VARCHAR(255),
    url VARCHAR(255)
);

-- Volcando datos para la tabla acceso.red_social
INSERT INTO red_social (id, estado, icono, nombre, orden, url) VALUES
    (1, 1, 'bi bi-facebook', 'Facebook', '1', 'https://www.facebook.com/profile.php?id=61567198078219'),
    (3, 1, ' bi-instagram', 'Instagram', '2', 'https://www.instagram.com/acuamont26/'),
    (4, 1, 'bi-whatsapp', 'Whatsapp', '3', 'https://wa.me/qr/WLZJEEQAJETRH1')
ON CONFLICT (id) DO NOTHING;

-- Volcando estructura para tabla acceso.series_comprobante
CREATE TABLE IF NOT EXISTS series_comprobante (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    serie VARCHAR(100) NOT NULL,
    correlativo_actual INTEGER NOT NULL,
    estado INTEGER NOT NULL
);

-- Volcando datos para la tabla acceso.series_comprobante
INSERT INTO series_comprobante (id, nombre, serie, correlativo_actual, estado) VALUES
    (1, 'Nota de Venta', 'N0001', 3, 1),
    (2, 'Boleta', 'B0001', 4, 1),
    (3, 'Factura', 'F0001', 1, 1)
ON CONFLICT (id) DO NOTHING;

-- Volcando estructura para tabla acceso.tipo_movimiento
CREATE TABLE IF NOT EXISTS tipo_movimiento (
    id SERIAL PRIMARY KEY,
    estado INTEGER NOT NULL,
    nombre VARCHAR(100) NOT NULL
);

-- Volcando datos para la tabla acceso.tipo_movimiento
INSERT INTO tipo_movimiento (id, estado, nombre) VALUES
    (1, 1, 'Entrada'),
    (2, 1, 'Salida')
ON CONFLICT (id) DO NOTHING;

-- Volcando estructura para tabla acceso.usuarios
CREATE TABLE IF NOT EXISTS usuarios (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    usuario VARCHAR(50) NOT NULL UNIQUE,
    clave VARCHAR(255),
    correo VARCHAR(255),
    estado INTEGER NOT NULL DEFAULT 1,
    id_perfil BIGINT,
    secreto_2fa VARCHAR(255),
    usa_2fa BOOLEAN NOT NULL
);

-- Volcando datos para la tabla acceso.usuarios
INSERT INTO usuarios (id, nombre, usuario, clave, correo, estado, id_perfil, secreto_2fa, usa_2fa) VALUES
    (8, 'Daryl', 'admin', '$2a$10$OZuN1MJlw/01gIodlwqaQOKk.d5XhfbWAD8X2adyG9pkKtpDlVN1O', 'luis@ejemplo.com', 1, 1, 'FSIXHJDEMO2HJ4TS2P627OYCA5VM32SOT6S6X2BLVBJLCFNXRKJP5LZ76DEVXNT4', true),
    (10, 'María Supervisor', 'supervisor', '$2a$10$OZuN1MJlw/01gIodlwqaQOKk.d5XhfbWAD8X2adyG9pkKtpDlVN1O', 'supervisor@ejemplo.com', 1, 3, NULL, false),
    (11, 'Carlos Analista', 'analista', '$2a$10$N9qo8uLOickgx2ZMRZoMye5aZl8ZzO8Fns2h0eCZgP2h7ZWCpU9/y', 'analista@ejemplo.com', 1, 2, NULL, false),
    (14, 'Luis Antonio', 'luis', '$2a$10$bDRnfg7TQgcBeV.e0cd.ZuNfDUGfPRPhp62tfLVtycqwV/unM0VWm', 'luis@ejemplo.com', 1, 1, NULL, false),
    (15, 'Blanca Rosa', 'blanca', '$2a$10$UTJNtLoen3wHnh1WMF756uBNJo9Gm4Hlmm8XuiFTOrJy5wdnt1d3C', 'blanca@ejemplo.com', 1, 2, NULL, false),
    (16, 'Test', 'test', '$2a$10$e3qInURZfl7HMtOwrbOfretobdJCk1170mjv1DGM7RF/1kwtKF98O', 'test@email.com', 1, 1, NULL, false)
ON CONFLICT (id) DO NOTHING;

-- Volcando estructura para tabla acceso.cuotas
CREATE TABLE IF NOT EXISTS cuotas (
    id SERIAL PRIMARY KEY,
    estado INTEGER NOT NULL,
    fecha_vencimiento DATE NOT NULL,
    monto DECIMAL(38,2) NOT NULL,
    numero_cuota INTEGER NOT NULL,
    saldo DECIMAL(38,2) NOT NULL,
    id_venta BIGINT NOT NULL
);

-- Volcando datos para la tabla acceso.cuotas
INSERT INTO cuotas (id, estado, fecha_vencimiento, monto, numero_cuota, saldo, id_venta) VALUES
    (1, 2, '2025-12-18', 25.00, 1, 0.00, 3),
    (2, 2, '2025-12-25', 25.00, 2, 0.00, 3),
    (3, 1, '2025-12-18', 25.00, 1, 0.00, 4),
    (4, 0, '2025-12-25', 25.00, 2, 25.00, 4)
ON CONFLICT (id) DO NOTHING;

-- Volcando estructura para tabla acceso.detalle_venta
CREATE TABLE IF NOT EXISTS detalle_venta (
    id SERIAL PRIMARY KEY,
    cantidad INTEGER NOT NULL,
    precio_unitario DECIMAL(10,2) NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL,
    id_producto BIGINT NOT NULL,
    id_venta BIGINT NOT NULL
);

-- Volcando datos para la tabla acceso.detalle_venta
INSERT INTO detalle_venta (id, cantidad, precio_unitario, subtotal, id_producto, id_venta) VALUES
    (1, 2, 25.00, 50.00, 1, 1),
    (2, 2, 25.00, 50.00, 1, 2),
    (3, 3, 25.00, 75.00, 1, 3),
    (4, 3, 25.00, 75.00, 1, 4)
ON CONFLICT (id) DO NOTHING;

-- Volcando estructura para tabla acceso.pagos
CREATE TABLE IF NOT EXISTS pagos (
    id SERIAL PRIMARY KEY,
    comentario VARCHAR(250) NOT NULL,
    estado INTEGER NOT NULL,
    fecha_pago TIMESTAMP(6) NOT NULL,
    metodo_pago VARCHAR(50) NOT NULL,
    monto_pagado DECIMAL(38,2) NOT NULL,
    id_cuota BIGINT NOT NULL
);

-- Volcando datos para la tabla acceso.pagos
INSERT INTO pagos (id, comentario, estado, fecha_pago, metodo_pago, monto_pagado, id_cuota) VALUES
    (1, '', 1, '2025-12-11 19:09:57.198728', 'EFECTIVO', 25.00, 1),
    (2, '', 1, '2025-12-11 19:10:35.501759', 'EFECTIVO', 25.00, 3)
ON CONFLICT (id) DO NOTHING;

-- Volcando estructura para tabla acceso.ventas
CREATE TABLE IF NOT EXISTS ventas (
    id SERIAL PRIMARY KEY,
    correlativo INTEGER NOT NULL,
    deuda DECIMAL(10,2) NOT NULL,
    estado INTEGER NOT NULL,
    fecha TIMESTAMP(6) NOT NULL,
    total DECIMAL(10,2) NOT NULL,
    id_cliente BIGINT,
    id_forma_pago BIGINT,
    id_serie_comprobante BIGINT NOT NULL,
    id_usuario BIGINT
);

-- Volcando datos para la tabla acceso.ventas
INSERT INTO ventas (id, correlativo, deuda, estado, fecha, total, id_cliente, id_forma_pago, id_serie_comprobante, id_usuario) VALUES
    (1, 2, 0.00, 1, '2025-12-09 22:58:24.841974', 50.00, 3, 1, 1, 16),
    (2, 3, 0.00, 2, '2025-12-11 19:05:36.567565', 50.00, 3, 1, 1, 16),
    (3, 3, 25.00, 2, '2025-12-11 19:09:40.833011', 75.00, 3, 2, 2, 16),
    (4, 4, 25.00, 0, '2025-12-11 19:10:32.639129', 75.00, 3, 2, 2, 16)
ON CONFLICT (id) DO NOTHING;

-- Agregar las llaves foráneas
ALTER TABLE ajustes_inventario ADD CONSTRAINT fk_ajustes_producto 
    FOREIGN KEY (id_producto) REFERENCES productos(id);
ALTER TABLE ajustes_inventario ADD CONSTRAINT fk_ajustes_tipo_movimiento 
    FOREIGN KEY (id_tipo_movimiento) REFERENCES tipo_movimiento(id);

ALTER TABLE productos ADD CONSTRAINT fk_productos_categoria 
    FOREIGN KEY (id_categoria) REFERENCES categorias(id);

ALTER TABLE perfil_opcion ADD CONSTRAINT fk_perfil_opcion_perfil 
    FOREIGN KEY (id_perfil) REFERENCES perfiles(id);
ALTER TABLE perfil_opcion ADD CONSTRAINT fk_perfil_opcion_opcion 
    FOREIGN KEY (id_opcion) REFERENCES opciones(id);

ALTER TABLE usuarios ADD CONSTRAINT fk_usuarios_perfil 
    FOREIGN KEY (id_perfil) REFERENCES perfiles(id);

ALTER TABLE cuotas ADD CONSTRAINT fk_cuotas_venta 
    FOREIGN KEY (id_venta) REFERENCES ventas(id);

ALTER TABLE detalle_venta ADD CONSTRAINT fk_detalle_producto 
    FOREIGN KEY (id_producto) REFERENCES productos(id);
ALTER TABLE detalle_venta ADD CONSTRAINT fk_detalle_venta 
    FOREIGN KEY (id_venta) REFERENCES ventas(id);

ALTER TABLE pagos ADD CONSTRAINT fk_pagos_cuota 
    FOREIGN KEY (id_cuota) REFERENCES cuotas(id);

ALTER TABLE ventas ADD CONSTRAINT fk_ventas_cliente 
    FOREIGN KEY (id_cliente) REFERENCES clientes(id);
ALTER TABLE ventas ADD CONSTRAINT fk_ventas_forma_pago 
    FOREIGN KEY (id_forma_pago) REFERENCES forma_pago(id);
ALTER TABLE ventas ADD CONSTRAINT fk_ventas_serie_comprobante 
    FOREIGN KEY (id_serie_comprobante) REFERENCES series_comprobante(id);
ALTER TABLE ventas ADD CONSTRAINT fk_ventas_usuario 
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id);

-- Restablecer secuencias (para que los IDs sigan funcionando correctamente)
SELECT setval('ajustes_inventario_id_seq', (SELECT COALESCE(MAX(id), 1) FROM ajustes_inventario));
SELECT setval('categorias_id_seq', (SELECT COALESCE(MAX(id), 1) FROM categorias));
SELECT setval('clientes_id_seq', (SELECT COALESCE(MAX(id), 1) FROM clientes));
SELECT setval('comentarios_id_seq', (SELECT COALESCE(MAX(id), 1) FROM comentarios));
SELECT setval('forma_pago_id_seq', (SELECT COALESCE(MAX(id), 1) FROM forma_pago));
SELECT setval('opciones_id_seq', (SELECT COALESCE(MAX(id), 1) FROM opciones));
SELECT setval('perfiles_id_seq', (SELECT COALESCE(MAX(id), 1) FROM perfiles));
SELECT setval('productos_id_seq', (SELECT COALESCE(MAX(id), 1) FROM productos));
SELECT setval('proveedor_id_seq', (SELECT COALESCE(MAX(id), 1) FROM proveedor));
SELECT setval('red_social_id_seq', (SELECT COALESCE(MAX(id), 1) FROM red_social));
SELECT setval('series_comprobante_id_seq', (SELECT COALESCE(MAX(id), 1) FROM series_comprobante));
SELECT setval('tipo_movimiento_id_seq', (SELECT COALESCE(MAX(id), 1) FROM tipo_movimiento));
SELECT setval('usuarios_id_seq', (SELECT COALESCE(MAX(id), 1) FROM usuarios));
SELECT setval('cuotas_id_seq', (SELECT COALESCE(MAX(id), 1) FROM cuotas));
SELECT setval('detalle_venta_id_seq', (SELECT COALESCE(MAX(id), 1) FROM detalle_venta));
SELECT setval('pagos_id_seq', (SELECT COALESCE(MAX(id), 1) FROM pagos));
SELECT setval('ventas_id_seq', (SELECT COALESCE(MAX(id), 1) FROM ventas));