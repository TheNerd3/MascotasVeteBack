/* =========================================================
   MASCOTAS CÓRDOBA - Script de creación de base de datos
   Motor: Microsoft SQL Server (RNF11)
   Fuente: Modelo lógico "MASCOTAS-CBA-LOGICO"
   ========================================================= */

-- Base de datos ya creada: correr este script conectado a MascotasCordoba

/* ---------------------------------------------------------
   CIUDADANOS
   --------------------------------------------------------- */
CREATE TABLE ciudadanos (
    id_ciudadano    INT IDENTITY(1,1) PRIMARY KEY,
    apellido        NVARCHAR(100) NOT NULL,
    nombre          NVARCHAR(100) NOT NULL,
    cuil            VARCHAR(11)   NOT NULL UNIQUE,
    clave           NVARCHAR(255) NOT NULL,
    correo          NVARCHAR(150) NULL,
    telefono        VARCHAR(20)   NULL,
    domicilio       NVARCHAR(200) NULL,
    habilitado      BIT           NOT NULL DEFAULT 1
);
GO

/* ---------------------------------------------------------
   VETERINARIAS
   --------------------------------------------------------- */
CREATE TABLE veterinarias (
    id_veterinaria          INT IDENTITY(1,1) PRIMARY KEY,
    razon_social             NVARCHAR(150) NOT NULL,
    correo                   NVARCHAR(150) NULL,
    telefono                 VARCHAR(20)   NULL,
    domicilio                NVARCHAR(200) NULL,
    habilitacion_municipal   VARCHAR(50)   NULL
);
GO

/* ---------------------------------------------------------
   REFUGIOS
   --------------------------------------------------------- */
CREATE TABLE refugios (
    id_refugio               INT IDENTITY(1,1) PRIMARY KEY,
    razon_social              NVARCHAR(150) NOT NULL,
    correo                    NVARCHAR(150) NULL,
    telefono                  VARCHAR(20)   NULL,
    domicilio                 NVARCHAR(200) NULL,
    habilitacion_municipal    VARCHAR(50)   NULL,
    id_responsable            INT NOT NULL,
    CONSTRAINT FK_refugios_responsable
        FOREIGN KEY (id_responsable) REFERENCES ciudadanos (id_ciudadano)
);
GO

/* ---------------------------------------------------------
   RASGOS_MASCOTAS
   --------------------------------------------------------- */
CREATE TABLE rasgos_mascotas (
    cod_rasgo   INT IDENTITY(1,1) PRIMARY KEY,
    nom_rasgo   NVARCHAR(100) NOT NULL
);
GO

/* ---------------------------------------------------------
   DOMINIO_RASGOS_MASCOTAS
   --------------------------------------------------------- */
CREATE TABLE dominio_rasgos_mascotas (
    cod_rasgo           INT NOT NULL,
    nro_valor_dominio   INT NOT NULL,
    nom_valor_dominio   NVARCHAR(100) NOT NULL,
    CONSTRAINT PK_dominio_rasgos_mascotas PRIMARY KEY (cod_rasgo, nro_valor_dominio),
    CONSTRAINT FK_dominio_rasgo
        FOREIGN KEY (cod_rasgo) REFERENCES rasgos_mascotas (cod_rasgo)
);
GO

/* ---------------------------------------------------------
   MASCOTAS
   --------------------------------------------------------- */
CREATE TABLE mascotas (
    nro_reg_municipal   INT IDENTITY(1,1) PRIMARY KEY,
    nombre               NVARCHAR(100) NOT NULL,
    sexo                 CHAR(1) NOT NULL,
    [año_nacimiento]     SMALLINT NULL,
    microchip            VARCHAR(50) NULL,
    vive                 BIT NOT NULL DEFAULT 1,
    id_responsable       INT NOT NULL,
    id_refugio           INT NULL,
    CONSTRAINT FK_mascotas_responsable
        FOREIGN KEY (id_responsable) REFERENCES ciudadanos (id_ciudadano),
    CONSTRAINT FK_mascotas_refugio
        FOREIGN KEY (id_refugio) REFERENCES refugios (id_refugio),
    CONSTRAINT CK_mascotas_sexo CHECK (sexo IN ('M','H'))
);
GO

/* ---------------------------------------------------------
   CARACTERISTICAS_MASCOTAS
   --------------------------------------------------------- */
CREATE TABLE caracteristicas_mascotas (
    nro_reg_municipal      INT NOT NULL,
    cod_rasgo               INT NOT NULL,
    nro_caracteristica      INT NOT NULL,
    nro_valor_dominio       INT NOT NULL,
    valor_caracteristica    NVARCHAR(200) NULL,
    CONSTRAINT PK_caracteristicas_mascotas
        PRIMARY KEY (nro_reg_municipal, cod_rasgo, nro_caracteristica),
    CONSTRAINT FK_caract_mascota
        FOREIGN KEY (nro_reg_municipal) REFERENCES mascotas (nro_reg_municipal),
    CONSTRAINT FK_caract_dominio
        FOREIGN KEY (cod_rasgo, nro_valor_dominio)
        REFERENCES dominio_rasgos_mascotas (cod_rasgo, nro_valor_dominio)
);
GO

/* ---------------------------------------------------------
   PROFESIONALES_VETERINARIAS
   (el profesional se modela como un ciudadano habilitado
    a operar en nombre de una veterinaria)
   --------------------------------------------------------- */
CREATE TABLE profesionales_veterinarias (
    id_veterinaria   INT NOT NULL,
    id_profesional   INT NOT NULL,
    baja             BIT NOT NULL DEFAULT 0,
    CONSTRAINT PK_profesionales_veterinarias PRIMARY KEY (id_veterinaria, id_profesional),
    CONSTRAINT FK_profvet_veterinaria
        FOREIGN KEY (id_veterinaria) REFERENCES veterinarias (id_veterinaria),
    CONSTRAINT FK_profvet_profesional
        FOREIGN KEY (id_profesional) REFERENCES ciudadanos (id_ciudadano)
);
GO

/* ---------------------------------------------------------
   PUBLICACIONES_ADOPCION
   --------------------------------------------------------- */
CREATE TABLE publicaciones_adopcion (
    nro_publicacion            INT IDENTITY(1,1) PRIMARY KEY,
    nro_reg_municipal           INT NOT NULL,
    id_refugio                  INT NOT NULL,
    fecha_publicacion           DATE NOT NULL,
    caracteristicas_mascota     NVARCHAR(MAX) NULL,
    condicion_adopcion          NVARCHAR(MAX) NULL,
    foto                        VARBINARY(MAX) NULL,
    estado_publicacion          VARCHAR(20) NOT NULL DEFAULT 'Activa',
    CONSTRAINT FK_public_mascota
        FOREIGN KEY (nro_reg_municipal) REFERENCES mascotas (nro_reg_municipal),
    CONSTRAINT FK_public_refugio
        FOREIGN KEY (id_refugio) REFERENCES refugios (id_refugio),
    CONSTRAINT CK_estado_publicacion
        CHECK (estado_publicacion IN ('Activa','Pausada','Finalizada'))
);
GO

/* ---------------------------------------------------------
   ATRIBUTOS_SISTEMA
   --------------------------------------------------------- */
CREATE TABLE atributos_sistema (
    cod_atributo      INT IDENTITY(1,1) PRIMARY KEY,
    desc_atributo      NVARCHAR(150) NOT NULL,
    tipo_dato          VARCHAR(30) NOT NULL,
    observ_atributo    NVARCHAR(300) NULL
);
GO

/* ---------------------------------------------------------
   CONFIGURACION_VETERINATARIAS
   --------------------------------------------------------- */
CREATE TABLE configuracion_veterinatarias (
    id_veterinaria   INT NOT NULL,
    cod_atributo     INT NOT NULL,
    valor            NVARCHAR(300) NULL,
    CONSTRAINT PK_configuracion_veterinatarias PRIMARY KEY (id_veterinaria, cod_atributo),
    CONSTRAINT FK_configvet_veterinaria
        FOREIGN KEY (id_veterinaria) REFERENCES veterinarias (id_veterinaria),
    CONSTRAINT FK_configvet_atributo
        FOREIGN KEY (cod_atributo) REFERENCES atributos_sistema (cod_atributo)
);
GO

/* ---------------------------------------------------------
   TIPOS_ATENCION_SANITARIA
   --------------------------------------------------------- */
CREATE TABLE tipos_atencion_sanitaria (
    cod_tipo_atencion   INT IDENTITY(1,1) PRIMARY KEY,
    desc_tipo_atencion   NVARCHAR(100) NOT NULL
);
GO

/* ---------------------------------------------------------
   INFORMACION_SANITARIA
   (destino local de lo consumido del servicio de la veterinaria)
   --------------------------------------------------------- */
CREATE TABLE informacion_sanitaria (
    nro_reg_municipal    INT NOT NULL,
    nro_registro          INT NOT NULL,
    fecha_atencion        DATE NOT NULL,
    cod_tipo_atencion     INT NOT NULL,
    detalle_atencion      NVARCHAR(MAX) NULL,
    fecha_vencimiento     DATE NULL,
    id_veterinaria        INT NOT NULL,
    id_profesional        INT NOT NULL,
    CONSTRAINT PK_informacion_sanitaria PRIMARY KEY (nro_reg_municipal, nro_registro),
    CONSTRAINT FK_infosan_mascota
        FOREIGN KEY (nro_reg_municipal) REFERENCES mascotas (nro_reg_municipal),
    CONSTRAINT FK_infosan_tipo
        FOREIGN KEY (cod_tipo_atencion) REFERENCES tipos_atencion_sanitaria (cod_tipo_atencion),
    CONSTRAINT FK_infosan_profvet
        FOREIGN KEY (id_veterinaria, id_profesional)
        REFERENCES profesionales_veterinarias (id_veterinaria, id_profesional)
);
GO


/* =========================================================
   MASCOTAS CÓRDOBA - Triggers de auditoría (RNF08)
   Registra INSERT / UPDATE / DELETE de cada tabla,
   guardando el estado anterior y nuevo como JSON.
   Requiere haber corrido antes: mascotas-cordoba-schema.sql
   ========================================================= */

/* ---------------------------------------------------------
   TABLA DE AUDITORÍA (genérica, sirve para todas las tablas)
   --------------------------------------------------------- */
CREATE TABLE auditoria (
    id_auditoria        BIGINT IDENTITY(1,1) PRIMARY KEY,
    nombre_tabla         VARCHAR(100)  NOT NULL,
    operacion             VARCHAR(10)   NOT NULL, -- INSERT / UPDATE / DELETE
    fecha_operacion       DATETIME2     NOT NULL DEFAULT SYSDATETIME(),
    usuario_bd            NVARCHAR(128) NOT NULL DEFAULT SUSER_SNAME(),
    datos_anteriores      NVARCHAR(MAX) NULL, -- JSON, NULL en INSERT
    datos_nuevos          NVARCHAR(MAX) NULL  -- JSON, NULL en DELETE
);
GO

CREATE TRIGGER TR_ciudadanos_Audit ON ciudadanos
AFTER INSERT, UPDATE, DELETE AS
BEGIN
    SET NOCOUNT ON;
    IF EXISTS(SELECT 1 FROM inserted) AND EXISTS(SELECT 1 FROM deleted)
        INSERT INTO auditoria (nombre_tabla, operacion, datos_anteriores, datos_nuevos)
        SELECT 'ciudadanos', 'UPDATE', (SELECT * FROM deleted FOR JSON AUTO), (SELECT * FROM inserted FOR JSON AUTO);
    ELSE IF EXISTS(SELECT 1 FROM inserted)
        INSERT INTO auditoria (nombre_tabla, operacion, datos_nuevos)
        SELECT 'ciudadanos', 'INSERT', (SELECT * FROM inserted FOR JSON AUTO);
    ELSE IF EXISTS(SELECT 1 FROM deleted)
        INSERT INTO auditoria (nombre_tabla, operacion, datos_anteriores)
        SELECT 'ciudadanos', 'DELETE', (SELECT * FROM deleted FOR JSON AUTO);
END
GO

CREATE TRIGGER TR_veterinarias_Audit ON veterinarias
AFTER INSERT, UPDATE, DELETE AS
BEGIN
    SET NOCOUNT ON;
    IF EXISTS(SELECT 1 FROM inserted) AND EXISTS(SELECT 1 FROM deleted)
        INSERT INTO auditoria (nombre_tabla, operacion, datos_anteriores, datos_nuevos)
        SELECT 'veterinarias', 'UPDATE', (SELECT * FROM deleted FOR JSON AUTO), (SELECT * FROM inserted FOR JSON AUTO);
    ELSE IF EXISTS(SELECT 1 FROM inserted)
        INSERT INTO auditoria (nombre_tabla, operacion, datos_nuevos)
        SELECT 'veterinarias', 'INSERT', (SELECT * FROM inserted FOR JSON AUTO);
    ELSE IF EXISTS(SELECT 1 FROM deleted)
        INSERT INTO auditoria (nombre_tabla, operacion, datos_anteriores)
        SELECT 'veterinarias', 'DELETE', (SELECT * FROM deleted FOR JSON AUTO);
END
GO

CREATE TRIGGER TR_refugios_Audit ON refugios
AFTER INSERT, UPDATE, DELETE AS
BEGIN
    SET NOCOUNT ON;
    IF EXISTS(SELECT 1 FROM inserted) AND EXISTS(SELECT 1 FROM deleted)
        INSERT INTO auditoria (nombre_tabla, operacion, datos_anteriores, datos_nuevos)
        SELECT 'refugios', 'UPDATE', (SELECT * FROM deleted FOR JSON AUTO), (SELECT * FROM inserted FOR JSON AUTO);
    ELSE IF EXISTS(SELECT 1 FROM inserted)
        INSERT INTO auditoria (nombre_tabla, operacion, datos_nuevos)
        SELECT 'refugios', 'INSERT', (SELECT * FROM inserted FOR JSON AUTO);
    ELSE IF EXISTS(SELECT 1 FROM deleted)
        INSERT INTO auditoria (nombre_tabla, operacion, datos_anteriores)
        SELECT 'refugios', 'DELETE', (SELECT * FROM deleted FOR JSON AUTO);
END
GO

CREATE TRIGGER TR_rasgos_mascotas_Audit ON rasgos_mascotas
AFTER INSERT, UPDATE, DELETE AS
BEGIN
    SET NOCOUNT ON;
    IF EXISTS(SELECT 1 FROM inserted) AND EXISTS(SELECT 1 FROM deleted)
        INSERT INTO auditoria (nombre_tabla, operacion, datos_anteriores, datos_nuevos)
        SELECT 'rasgos_mascotas', 'UPDATE', (SELECT * FROM deleted FOR JSON AUTO), (SELECT * FROM inserted FOR JSON AUTO);
    ELSE IF EXISTS(SELECT 1 FROM inserted)
        INSERT INTO auditoria (nombre_tabla, operacion, datos_nuevos)
        SELECT 'rasgos_mascotas', 'INSERT', (SELECT * FROM inserted FOR JSON AUTO);
    ELSE IF EXISTS(SELECT 1 FROM deleted)
        INSERT INTO auditoria (nombre_tabla, operacion, datos_anteriores)
        SELECT 'rasgos_mascotas', 'DELETE', (SELECT * FROM deleted FOR JSON AUTO);
END
GO

CREATE TRIGGER TR_dominio_rasgos_mascotas_Audit ON dominio_rasgos_mascotas
AFTER INSERT, UPDATE, DELETE AS
BEGIN
    SET NOCOUNT ON;
    IF EXISTS(SELECT 1 FROM inserted) AND EXISTS(SELECT 1 FROM deleted)
        INSERT INTO auditoria (nombre_tabla, operacion, datos_anteriores, datos_nuevos)
        SELECT 'dominio_rasgos_mascotas', 'UPDATE', (SELECT * FROM deleted FOR JSON AUTO), (SELECT * FROM inserted FOR JSON AUTO);
    ELSE IF EXISTS(SELECT 1 FROM inserted)
        INSERT INTO auditoria (nombre_tabla, operacion, datos_nuevos)
        SELECT 'dominio_rasgos_mascotas', 'INSERT', (SELECT * FROM inserted FOR JSON AUTO);
    ELSE IF EXISTS(SELECT 1 FROM deleted)
        INSERT INTO auditoria (nombre_tabla, operacion, datos_anteriores)
        SELECT 'dominio_rasgos_mascotas', 'DELETE', (SELECT * FROM deleted FOR JSON AUTO);
END
GO

CREATE TRIGGER TR_mascotas_Audit ON mascotas
AFTER INSERT, UPDATE, DELETE AS
BEGIN
    SET NOCOUNT ON;
    IF EXISTS(SELECT 1 FROM inserted) AND EXISTS(SELECT 1 FROM deleted)
        INSERT INTO auditoria (nombre_tabla, operacion, datos_anteriores, datos_nuevos)
        SELECT 'mascotas', 'UPDATE', (SELECT * FROM deleted FOR JSON AUTO), (SELECT * FROM inserted FOR JSON AUTO);
    ELSE IF EXISTS(SELECT 1 FROM inserted)
        INSERT INTO auditoria (nombre_tabla, operacion, datos_nuevos)
        SELECT 'mascotas', 'INSERT', (SELECT * FROM inserted FOR JSON AUTO);
    ELSE IF EXISTS(SELECT 1 FROM deleted)
        INSERT INTO auditoria (nombre_tabla, operacion, datos_anteriores)
        SELECT 'mascotas', 'DELETE', (SELECT * FROM deleted FOR JSON AUTO);
END
GO

CREATE TRIGGER TR_caracteristicas_mascotas_Audit ON caracteristicas_mascotas
AFTER INSERT, UPDATE, DELETE AS
BEGIN
    SET NOCOUNT ON;
    IF EXISTS(SELECT 1 FROM inserted) AND EXISTS(SELECT 1 FROM deleted)
        INSERT INTO auditoria (nombre_tabla, operacion, datos_anteriores, datos_nuevos)
        SELECT 'caracteristicas_mascotas', 'UPDATE', (SELECT * FROM deleted FOR JSON AUTO), (SELECT * FROM inserted FOR JSON AUTO);
    ELSE IF EXISTS(SELECT 1 FROM inserted)
        INSERT INTO auditoria (nombre_tabla, operacion, datos_nuevos)
        SELECT 'caracteristicas_mascotas', 'INSERT', (SELECT * FROM inserted FOR JSON AUTO);
    ELSE IF EXISTS(SELECT 1 FROM deleted)
        INSERT INTO auditoria (nombre_tabla, operacion, datos_anteriores)
        SELECT 'caracteristicas_mascotas', 'DELETE', (SELECT * FROM deleted FOR JSON AUTO);
END
GO

CREATE TRIGGER TR_profesionales_veterinarias_Audit ON profesionales_veterinarias
AFTER INSERT, UPDATE, DELETE AS
BEGIN
    SET NOCOUNT ON;
    IF EXISTS(SELECT 1 FROM inserted) AND EXISTS(SELECT 1 FROM deleted)
        INSERT INTO auditoria (nombre_tabla, operacion, datos_anteriores, datos_nuevos)
        SELECT 'profesionales_veterinarias', 'UPDATE', (SELECT * FROM deleted FOR JSON AUTO), (SELECT * FROM inserted FOR JSON AUTO);
    ELSE IF EXISTS(SELECT 1 FROM inserted)
        INSERT INTO auditoria (nombre_tabla, operacion, datos_nuevos)
        SELECT 'profesionales_veterinarias', 'INSERT', (SELECT * FROM inserted FOR JSON AUTO);
    ELSE IF EXISTS(SELECT 1 FROM deleted)
        INSERT INTO auditoria (nombre_tabla, operacion, datos_anteriores)
        SELECT 'profesionales_veterinarias', 'DELETE', (SELECT * FROM deleted FOR JSON AUTO);
END
GO

CREATE TRIGGER TR_publicaciones_adopcion_Audit ON publicaciones_adopcion
AFTER INSERT, UPDATE, DELETE AS
BEGIN
    SET NOCOUNT ON;
    IF EXISTS(SELECT 1 FROM inserted) AND EXISTS(SELECT 1 FROM deleted)
        INSERT INTO auditoria (nombre_tabla, operacion, datos_anteriores, datos_nuevos)
        SELECT 'publicaciones_adopcion', 'UPDATE', (SELECT * FROM deleted FOR JSON AUTO), (SELECT * FROM inserted FOR JSON AUTO);
    ELSE IF EXISTS(SELECT 1 FROM inserted)
        INSERT INTO auditoria (nombre_tabla, operacion, datos_nuevos)
        SELECT 'publicaciones_adopcion', 'INSERT', (SELECT * FROM inserted FOR JSON AUTO);
    ELSE IF EXISTS(SELECT 1 FROM deleted)
        INSERT INTO auditoria (nombre_tabla, operacion, datos_anteriores)
        SELECT 'publicaciones_adopcion', 'DELETE', (SELECT * FROM deleted FOR JSON AUTO);
END
GO

CREATE TRIGGER TR_atributos_sistema_Audit ON atributos_sistema
AFTER INSERT, UPDATE, DELETE AS
BEGIN
    SET NOCOUNT ON;
    IF EXISTS(SELECT 1 FROM inserted) AND EXISTS(SELECT 1 FROM deleted)
        INSERT INTO auditoria (nombre_tabla, operacion, datos_anteriores, datos_nuevos)
        SELECT 'atributos_sistema', 'UPDATE', (SELECT * FROM deleted FOR JSON AUTO), (SELECT * FROM inserted FOR JSON AUTO);
    ELSE IF EXISTS(SELECT 1 FROM inserted)
        INSERT INTO auditoria (nombre_tabla, operacion, datos_nuevos)
        SELECT 'atributos_sistema', 'INSERT', (SELECT * FROM inserted FOR JSON AUTO);
    ELSE IF EXISTS(SELECT 1 FROM deleted)
        INSERT INTO auditoria (nombre_tabla, operacion, datos_anteriores)
        SELECT 'atributos_sistema', 'DELETE', (SELECT * FROM deleted FOR JSON AUTO);
END
GO

CREATE TRIGGER TR_configuracion_veterinatarias_Audit ON configuracion_veterinatarias
AFTER INSERT, UPDATE, DELETE AS
BEGIN
    SET NOCOUNT ON;
    IF EXISTS(SELECT 1 FROM inserted) AND EXISTS(SELECT 1 FROM deleted)
        INSERT INTO auditoria (nombre_tabla, operacion, datos_anteriores, datos_nuevos)
        SELECT 'configuracion_veterinatarias', 'UPDATE', (SELECT * FROM deleted FOR JSON AUTO), (SELECT * FROM inserted FOR JSON AUTO);
    ELSE IF EXISTS(SELECT 1 FROM inserted)
        INSERT INTO auditoria (nombre_tabla, operacion, datos_nuevos)
        SELECT 'configuracion_veterinatarias', 'INSERT', (SELECT * FROM inserted FOR JSON AUTO);
    ELSE IF EXISTS(SELECT 1 FROM deleted)
        INSERT INTO auditoria (nombre_tabla, operacion, datos_anteriores)
        SELECT 'configuracion_veterinatarias', 'DELETE', (SELECT * FROM deleted FOR JSON AUTO);
END
GO

CREATE TRIGGER TR_tipos_atencion_sanitaria_Audit ON tipos_atencion_sanitaria
AFTER INSERT, UPDATE, DELETE AS
BEGIN
    SET NOCOUNT ON;
    IF EXISTS(SELECT 1 FROM inserted) AND EXISTS(SELECT 1 FROM deleted)
        INSERT INTO auditoria (nombre_tabla, operacion, datos_anteriores, datos_nuevos)
        SELECT 'tipos_atencion_sanitaria', 'UPDATE', (SELECT * FROM deleted FOR JSON AUTO), (SELECT * FROM inserted FOR JSON AUTO);
    ELSE IF EXISTS(SELECT 1 FROM inserted)
        INSERT INTO auditoria (nombre_tabla, operacion, datos_nuevos)
        SELECT 'tipos_atencion_sanitaria', 'INSERT', (SELECT * FROM inserted FOR JSON AUTO);
    ELSE IF EXISTS(SELECT 1 FROM deleted)
        INSERT INTO auditoria (nombre_tabla, operacion, datos_anteriores)
        SELECT 'tipos_atencion_sanitaria', 'DELETE', (SELECT * FROM deleted FOR JSON AUTO);
END
GO

CREATE TRIGGER TR_informacion_sanitaria_Audit ON informacion_sanitaria
AFTER INSERT, UPDATE, DELETE AS
BEGIN
    SET NOCOUNT ON;
    IF EXISTS(SELECT 1 FROM inserted) AND EXISTS(SELECT 1 FROM deleted)
        INSERT INTO auditoria (nombre_tabla, operacion, datos_anteriores, datos_nuevos)
        SELECT 'informacion_sanitaria', 'UPDATE', (SELECT * FROM deleted FOR JSON AUTO), (SELECT * FROM inserted FOR JSON AUTO);
    ELSE IF EXISTS(SELECT 1 FROM inserted)
        INSERT INTO auditoria (nombre_tabla, operacion, datos_nuevos)
        SELECT 'informacion_sanitaria', 'INSERT', (SELECT * FROM inserted FOR JSON AUTO);
    ELSE IF EXISTS(SELECT 1 FROM deleted)
        INSERT INTO auditoria (nombre_tabla, operacion, datos_anteriores)
        SELECT 'informacion_sanitaria', 'DELETE', (SELECT * FROM deleted FOR JSON AUTO);
END
GO

/* =========================================================
   EXTRAS: constraints, índices, datos semilla, vista y SP
   ========================================================= */

ALTER TABLE mascotas
    ADD CONSTRAINT UQ_mascotas_microchip UNIQUE (microchip);
GO

CREATE UNIQUE INDEX UQ_publicacion_activa_por_mascota
    ON publicaciones_adopcion (nro_reg_municipal)
    WHERE estado_publicacion = 'Activa';
GO

CREATE INDEX IX_mascotas_nombre ON mascotas (nombre);
GO

CREATE INDEX IX_informacion_sanitaria_mascota ON informacion_sanitaria (nro_reg_municipal);
GO

CREATE INDEX IX_publicaciones_refugio ON publicaciones_adopcion (id_refugio);
GO

/* ---------------------------------------------------------
   DATOS SEMILLA (catálogos)
   --------------------------------------------------------- */

INSERT INTO tipos_atencion_sanitaria (desc_tipo_atencion) VALUES
    ('Vacunación'),
    ('Desparasitación'),
    ('Esterilización'),
    ('Castración');
GO

DECLARE @cod_especie INT, @cod_raza INT, @cod_pelaje INT;

INSERT INTO rasgos_mascotas (nom_rasgo) VALUES ('Especie');
SET @cod_especie = SCOPE_IDENTITY();

INSERT INTO rasgos_mascotas (nom_rasgo) VALUES ('Raza');
SET @cod_raza = SCOPE_IDENTITY();

INSERT INTO rasgos_mascotas (nom_rasgo) VALUES ('Pelaje');
SET @cod_pelaje = SCOPE_IDENTITY();

INSERT INTO dominio_rasgos_mascotas (cod_rasgo, nro_valor_dominio, nom_valor_dominio) VALUES
    (@cod_especie, 1, 'Gato'),
    (@cod_especie, 2, 'Perro'),
    (@cod_especie, 3, 'Conejo'),
    (@cod_raza, 1, 'Border Collie'),
    (@cod_raza, 2, 'Labrador'),
    (@cod_raza, 3, 'Siamés'),
    (@cod_raza, 4, 'Mestizo'),
    (@cod_pelaje, 1, 'Corto'),
    (@cod_pelaje, 2, 'Largo');
GO

INSERT INTO atributos_sistema (desc_atributo, tipo_dato, observ_atributo) VALUES
    ('URL del servicio', 'String', 'Endpoint expuesto por la veterinaria'),
    ('Tecnología servicio', 'String', 'REST o SOAP'),
    ('Parámetros servicio', 'String', 'Parámetros adicionales de configuración'),
    ('Api Key', 'String', 'Credencial de autenticación de la veterinaria');
GO

/* ---------------------------------------------------------
   VISTA: carnet sanitario vigente (RF10 / RF11)
   --------------------------------------------------------- */
CREATE VIEW vw_carnet_sanitario AS
SELECT
    m.nro_reg_municipal,
    m.nombre                AS nombre_mascota,
    c.nombre                 AS nombre_responsable,
    c.apellido                AS apellido_responsable,
    c.cuil,
    infosan.nro_registro,
    infosan.fecha_atencion,
    tas.desc_tipo_atencion,
    infosan.detalle_atencion,
    infosan.fecha_vencimiento,
    v.razon_social            AS veterinaria
FROM mascotas m
INNER JOIN ciudadanos c
    ON c.id_ciudadano = m.id_responsable
LEFT JOIN informacion_sanitaria infosan
    ON infosan.nro_reg_municipal = m.nro_reg_municipal
LEFT JOIN tipos_atencion_sanitaria tas
    ON tas.cod_tipo_atencion = infosan.cod_tipo_atencion
LEFT JOIN veterinarias v
    ON v.id_veterinaria = infosan.id_veterinaria;
GO

/* ---------------------------------------------------------
   STORED PROCEDURE: búsqueda de duplicados (RF06)
   --------------------------------------------------------- */
CREATE PROCEDURE sp_BuscarMascotaExistente
    @nombre           NVARCHAR(100),
    @anio_nacimiento  SMALLINT = NULL,
    @id_responsable   INT,
    @microchip        VARCHAR(50) = NULL
AS
BEGIN
    SET NOCOUNT ON;

    IF @microchip IS NOT NULL
    BEGIN
        SELECT * FROM mascotas WHERE microchip = @microchip;
        RETURN;
    END

    SELECT * FROM mascotas
    WHERE nombre = @nombre
      AND id_responsable = @id_responsable
      AND ([año_nacimiento] = @anio_nacimiento OR @anio_nacimiento IS NULL);
END
GO

/* =========================================================
   NUMERACIÓN ESCALABLE POR ENTIDAD
   ========================================================= */

ALTER TABLE mascotas
    ADD ultimo_nro_atencion INT NOT NULL DEFAULT 0;
GO

CREATE PROCEDURE sp_InsertarInformacionSanitaria
    @nro_reg_municipal   INT,
    @fecha_atencion       DATE,
    @cod_tipo_atencion    INT,
    @detalle_atencion     NVARCHAR(MAX),
    @fecha_vencimiento    DATE = NULL,
    @id_veterinaria       INT,
    @id_profesional       INT
AS
BEGIN
    SET NOCOUNT ON;

    DECLARE @siguiente TABLE (nro INT);
    DECLARE @nro_registro INT;

    UPDATE mascotas
        SET ultimo_nro_atencion = ultimo_nro_atencion + 1
        OUTPUT inserted.ultimo_nro_atencion INTO @siguiente
        WHERE nro_reg_municipal = @nro_reg_municipal;

    SELECT @nro_registro = nro FROM @siguiente;

    INSERT INTO informacion_sanitaria
        (nro_reg_municipal, nro_registro, fecha_atencion, cod_tipo_atencion,
         detalle_atencion, fecha_vencimiento, id_veterinaria, id_profesional)
    VALUES
        (@nro_reg_municipal, @nro_registro, @fecha_atencion, @cod_tipo_atencion,
         @detalle_atencion, @fecha_vencimiento, @id_veterinaria, @id_profesional);

    SELECT @nro_registro AS nro_registro_generado;
END
GO

CREATE TABLE contador_caracteristicas (
    nro_reg_municipal   INT NOT NULL,
    cod_rasgo            INT NOT NULL,
    ultimo_nro           INT NOT NULL DEFAULT 0,
    CONSTRAINT PK_contador_caracteristicas PRIMARY KEY (nro_reg_municipal, cod_rasgo),
    CONSTRAINT FK_contcarac_mascota
        FOREIGN KEY (nro_reg_municipal) REFERENCES mascotas (nro_reg_municipal),
    CONSTRAINT FK_contcarac_rasgo
        FOREIGN KEY (cod_rasgo) REFERENCES rasgos_mascotas (cod_rasgo)
);
GO

CREATE PROCEDURE sp_InsertarCaracteristicaMascota
    @nro_reg_municipal      INT,
    @cod_rasgo               INT,
    @nro_valor_dominio       INT,
    @valor_caracteristica    NVARCHAR(200) = NULL
AS
BEGIN
    SET NOCOUNT ON;

    DECLARE @siguiente TABLE (nro INT);
    DECLARE @nro_caracteristica INT;

    MERGE contador_caracteristicas AS target
    USING (SELECT @nro_reg_municipal AS nrm, @cod_rasgo AS cr) AS src
        ON target.nro_reg_municipal = src.nrm AND target.cod_rasgo = src.cr
    WHEN MATCHED THEN
        UPDATE SET ultimo_nro = target.ultimo_nro + 1
    WHEN NOT MATCHED THEN
        INSERT (nro_reg_municipal, cod_rasgo, ultimo_nro)
        VALUES (src.nrm, src.cr, 1)
    OUTPUT inserted.ultimo_nro INTO @siguiente;

    SELECT @nro_caracteristica = nro FROM @siguiente;

    INSERT INTO caracteristicas_mascotas
        (nro_reg_municipal, cod_rasgo, nro_caracteristica, nro_valor_dominio, valor_caracteristica)
    VALUES
        (@nro_reg_municipal, @cod_rasgo, @nro_caracteristica, @nro_valor_dominio, @valor_caracteristica);

    SELECT @nro_caracteristica AS nro_caracteristica_generado;
END
GO
