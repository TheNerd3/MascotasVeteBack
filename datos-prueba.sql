/*
  Datos de prueba realistas para Mascotas Córdoba, contra la base
  MASCOTAS real (schema de mascotas-cordoba-schema-completo.sql ya
  corrido).

  Idempotente: usa IF NOT EXISTS por clave natural (cuil, microchip,
  nombre+responsable, etc.), nunca asume IDs fijos porque las PK son
  IDENTITY. Se puede correr varias veces sin duplicar nada.

  Todo dentro de una unica transaccion con BEGIN TRY/CATCH: si algo
  falla, ROLLBACK completo.

  Reglas respetadas:
  - informacion_sanitaria y caracteristicas_mascotas se cargan
    EXCLUSIVAMENTE con EXEC a sp_InsertarInformacionSanitaria y
    sp_InsertarCaracteristicaMascota. Nunca INSERT directo.
  - auditoria y contador_caracteristicas no se tocan directamente
    (los llenan los triggers y los SP solos).
  - Los catalogos (rasgos_mascotas, dominio_rasgos_mascotas,
    tipos_atencion_sanitaria, atributos_sistema) se REUSAN tal cual
    quedaron sembrados por mascotas-cordoba-schema-completo.sql:
      Especie: 1=Gato, 2=Perro, 3=Conejo
      Raza: 1=Border Collie, 2=Labrador, 3=Siames, 4=Mestizo
      Pelaje: 1=Corto, 2=Largo
      Tipos de atencion: 1=Vacunacion, 2=Desparasitacion,
                         3=Esterilizacion, 4=Castracion
      Atributos de sistema: 1=URL del servicio, 2=Tecnologia servicio,
                            3=Parametros servicio, 4=Api Key
  - Las claves de ciudadanos van con el hash BCrypt real de
    "Prueba123!" generado con el mismo BCryptPasswordEncoder que usa
    el backend (ver datos-prueba-referencia.md).
*/

SET NOCOUNT ON;
SET QUOTED_IDENTIFIER ON;
GO

BEGIN TRY
    BEGIN TRANSACTION;

    -- Códigos de catálogo reales (sembrados por el script de schema)
    DECLARE @cod_rasgo_especie INT = (SELECT cod_rasgo FROM rasgos_mascotas WHERE nom_rasgo = N'Especie');
    DECLARE @cod_rasgo_raza INT = (SELECT cod_rasgo FROM rasgos_mascotas WHERE nom_rasgo = N'Raza');
    DECLARE @cod_rasgo_pelaje INT = (SELECT cod_rasgo FROM rasgos_mascotas WHERE nom_rasgo = N'Pelaje');

    DECLARE @dom_especie_gato INT = (SELECT nro_valor_dominio FROM dominio_rasgos_mascotas WHERE cod_rasgo = @cod_rasgo_especie AND nom_valor_dominio = N'Gato');
    DECLARE @dom_especie_perro INT = (SELECT nro_valor_dominio FROM dominio_rasgos_mascotas WHERE cod_rasgo = @cod_rasgo_especie AND nom_valor_dominio = N'Perro');

    DECLARE @dom_raza_bordercollie INT = (SELECT nro_valor_dominio FROM dominio_rasgos_mascotas WHERE cod_rasgo = @cod_rasgo_raza AND nom_valor_dominio = N'Border Collie');
    DECLARE @dom_raza_labrador INT = (SELECT nro_valor_dominio FROM dominio_rasgos_mascotas WHERE cod_rasgo = @cod_rasgo_raza AND nom_valor_dominio = N'Labrador');
    DECLARE @dom_raza_siames INT = (SELECT nro_valor_dominio FROM dominio_rasgos_mascotas WHERE cod_rasgo = @cod_rasgo_raza AND nom_valor_dominio = N'Siamés');
    DECLARE @dom_raza_mestizo INT = (SELECT nro_valor_dominio FROM dominio_rasgos_mascotas WHERE cod_rasgo = @cod_rasgo_raza AND nom_valor_dominio = N'Mestizo');

    DECLARE @dom_pelaje_corto INT = (SELECT nro_valor_dominio FROM dominio_rasgos_mascotas WHERE cod_rasgo = @cod_rasgo_pelaje AND nom_valor_dominio = N'Corto');
    DECLARE @dom_pelaje_largo INT = (SELECT nro_valor_dominio FROM dominio_rasgos_mascotas WHERE cod_rasgo = @cod_rasgo_pelaje AND nom_valor_dominio = N'Largo');

    DECLARE @tipo_vacunacion INT = (SELECT cod_tipo_atencion FROM tipos_atencion_sanitaria WHERE desc_tipo_atencion = N'Vacunación');
    DECLARE @tipo_desparasitacion INT = (SELECT cod_tipo_atencion FROM tipos_atencion_sanitaria WHERE desc_tipo_atencion = N'Desparasitación');
    DECLARE @tipo_esterilizacion INT = (SELECT cod_tipo_atencion FROM tipos_atencion_sanitaria WHERE desc_tipo_atencion = N'Esterilización');
    DECLARE @tipo_castracion INT = (SELECT cod_tipo_atencion FROM tipos_atencion_sanitaria WHERE desc_tipo_atencion = N'Castración');

    DECLARE @attr_url INT = (SELECT cod_atributo FROM atributos_sistema WHERE desc_atributo = N'URL del servicio');
    DECLARE @attr_tecnologia INT = (SELECT cod_atributo FROM atributos_sistema WHERE desc_atributo = N'Tecnología servicio');
    DECLARE @attr_parametros INT = (SELECT cod_atributo FROM atributos_sistema WHERE desc_atributo = N'Parámetros servicio');
    DECLARE @attr_api_key INT = (SELECT cod_atributo FROM atributos_sistema WHERE desc_atributo = N'Api Key');

    IF @cod_rasgo_especie IS NULL OR @tipo_vacunacion IS NULL OR @attr_api_key IS NULL
    BEGIN
        RAISERROR('Los catalogos esperados (rasgos_mascotas/tipos_atencion_sanitaria/atributos_sistema) no estan sembrados. Correr primero mascotas-cordoba-schema-completo.sql.', 16, 1);
    END

    -- =========================================================
    -- 1) CIUDADANOS (8)
    --    Clave BCrypt real de "Prueba123!" (ver referencia .md)
    -- =========================================================

    DECLARE @ciudadanos TABLE (
        cuil VARCHAR(11),
        apellido NVARCHAR(100),
        nombre NVARCHAR(100),
        clave_hash NVARCHAR(255),
        correo NVARCHAR(150),
        telefono VARCHAR(20),
        domicilio NVARCHAR(200),
        habilitado BIT);

    INSERT INTO @ciudadanos (cuil, apellido, nombre, clave_hash, correo, telefono, domicilio, habilitado) VALUES
    ('20123456786', N'Gimenez',   N'Ana Maria',  '$2a$10$zJapBKgwdkfW9Xdz4RHN8.o4GEg6zyEeF2cLEEBtDEJnT9QKqUTnS', 'ana.gimenez@gmail.com',      '351-4556677', N'Av. Hipolito Yrigoyen 450, Nueva Cordoba', 1),
    ('27234567891', N'Fernandez', N'Lucas',      '$2a$10$/ZZsU/5tO8IMlerQ64ThVOEPCkxbeB8Jb3eh7AamUEk4qfKFuFWCa', 'lucas.fernandez@hotmail.com','351-5667788', N'Bv. Illia 230, General Paz',               1),
    ('20345678906', N'Diaz',      N'Marcos',     '$2a$10$pBwRMB/Y7lQq6YP6zhe3QeRe.vbeL45AgMlyTdIJKPmZKk/ku1.1m', 'marcos.diaz@gmail.com',      '351-6778899', N'Calle Obispo Trejo 870, Centro',           1),
    ('27456789019', N'Lopez',     N'Carla',      '$2a$10$xFWPkui5/Q.XnS56hNL8PujKa5Oq4n.rVRLVTTsE7.o5yOgxmkHdS', 'carla.lopez@hotmail.com',    '351-7889900', N'Av. Rafael Nunez 4200, Cerro de las Rosas',1),
    ('20567890121', N'Molina',    N'Juan Pablo', '$2a$10$6Uqh7sE5mzNYH9GCIKqqC.UwXGCxIypMEsWUGco8W.Eyovf75IYiC', 'juanpablo.molina@gmail.com', '351-8990011', N'Calle Duarte Quiros 1560, Alta Cordoba',   1),
    ('23678901237', N'Suarez',    N'Victoria',   '$2a$10$qjvM9kUXTRLDGu6tehI9HuQFiAseZ.LVJqH2DjKFrnEWTnPRm4lWq', 'victoria.suarez@gmail.com',  '351-9001122', N'Av. Colon 3300, Cofico',                   0),
    ('20789012343', N'Romero',    N'Esteban',    '$2a$10$BB6Lw2InmcCmyohy80Lq7eqwDRzpKAfAFuaG0bKsM.rHpKbsLuQq2', 'esteban.romero@hotmail.com', '351-0112233', N'Bv. Guzman 650, Nueva Cordoba',            1),
    ('27890123452', N'Acosta',    N'Florencia',  '$2a$10$NRsReylqidi0cT05R3edM.SQ.T2VeEEhtfE/lGGu4Tg9ZOgbjQmru', 'florencia.acosta@gmail.com', '351-1223344', N'Calle San Jeronimo 980, Centro',           1);

    INSERT INTO ciudadanos (cuil, apellido, nombre, clave, correo, telefono, domicilio, habilitado)
    SELECT c.cuil, c.apellido, c.nombre, c.clave_hash, c.correo, c.telefono, c.domicilio, c.habilitado
    FROM @ciudadanos c
    WHERE NOT EXISTS (SELECT 1 FROM ciudadanos x WHERE x.cuil = c.cuil);

    DECLARE @id_ana INT = (SELECT id_ciudadano FROM ciudadanos WHERE cuil = '20123456786');      -- dueña con varias mascotas
    DECLARE @id_lucas INT = (SELECT id_ciudadano FROM ciudadanos WHERE cuil = '27234567891');     -- dueño con varias mascotas
    DECLARE @id_marcos INT = (SELECT id_ciudadano FROM ciudadanos WHERE cuil = '20345678906');    -- responsable de refugio
    DECLARE @id_carla INT = (SELECT id_ciudadano FROM ciudadanos WHERE cuil = '27456789019');     -- dueña
    DECLARE @id_juanpablo INT = (SELECT id_ciudadano FROM ciudadanos WHERE cuil = '20567890121'); -- profesional veterinario activo
    DECLARE @id_victoria INT = (SELECT id_ciudadano FROM ciudadanos WHERE cuil = '23678901237');  -- deshabilitada (login rechazado)
    DECLARE @id_esteban INT = (SELECT id_ciudadano FROM ciudadanos WHERE cuil = '20789012343');   -- profesional veterinario dado de baja
    DECLARE @id_florencia INT = (SELECT id_ciudadano FROM ciudadanos WHERE cuil = '27890123452'); -- dueña

    -- =========================================================
    -- 2) VETERINARIAS (4: 3 habilitadas, 1 no habilitada)
    -- =========================================================

    IF NOT EXISTS (SELECT 1 FROM veterinarias WHERE razon_social = N'Veterinaria Nueva Cordoba')
        INSERT INTO veterinarias (razon_social, correo, telefono, domicilio, habilitacion_municipal)
        VALUES (N'Veterinaria Nueva Cordoba', 'contacto@vetnuevacordoba.com.ar', '351-4001100', N'Av. Hipolito Yrigoyen 320, Nueva Cordoba', 'HM-1001');
    IF NOT EXISTS (SELECT 1 FROM veterinarias WHERE razon_social = N'Clinica Veterinaria Alta Cordoba')
        INSERT INTO veterinarias (razon_social, correo, telefono, domicilio, habilitacion_municipal)
        VALUES (N'Clinica Veterinaria Alta Cordoba', 'contacto@vetaltacordoba.com.ar', '351-4002200', N'Calle Duarte Quiros 1800, Alta Cordoba', 'HM-1002');
    IF NOT EXISTS (SELECT 1 FROM veterinarias WHERE razon_social = N'Veterinaria Cerro de las Rosas')
        INSERT INTO veterinarias (razon_social, correo, telefono, domicilio, habilitacion_municipal)
        VALUES (N'Veterinaria Cerro de las Rosas', 'contacto@vetcerro.com.ar', '351-4003300', N'Av. Rafael Nunez 4500, Cerro de las Rosas', 'HM-1003');
    IF NOT EXISTS (SELECT 1 FROM veterinarias WHERE razon_social = N'Veterinaria General Paz (no habilitada)')
        INSERT INTO veterinarias (razon_social, correo, telefono, domicilio, habilitacion_municipal)
        VALUES (N'Veterinaria General Paz (no habilitada)', 'contacto@vetgeneralpaz.com.ar', '351-4004400', N'Bv. Illia 500, General Paz', NULL);

    DECLARE @vet_nueva_cordoba INT = (SELECT id_veterinaria FROM veterinarias WHERE razon_social = N'Veterinaria Nueva Cordoba');
    DECLARE @vet_alta_cordoba INT = (SELECT id_veterinaria FROM veterinarias WHERE razon_social = N'Clinica Veterinaria Alta Cordoba');
    DECLARE @vet_cerro INT = (SELECT id_veterinaria FROM veterinarias WHERE razon_social = N'Veterinaria Cerro de las Rosas');
    DECLARE @vet_general_paz INT = (SELECT id_veterinaria FROM veterinarias WHERE razon_social = N'Veterinaria General Paz (no habilitada)');

    DECLARE @config TABLE (id_veterinaria INT, cod_atributo INT, valor NVARCHAR(300));
    INSERT INTO @config (id_veterinaria, cod_atributo, valor) VALUES
        (@vet_nueva_cordoba, @attr_url, 'https://api.vetnuevacordoba.com.ar/v1'),
        (@vet_nueva_cordoba, @attr_tecnologia, 'REST'),
        (@vet_nueva_cordoba, @attr_parametros, 'timeoutMs=3000'),
        (@vet_nueva_cordoba, @attr_api_key, 'vnc-9f1a2b3c4d5e6f70'),
        (@vet_alta_cordoba, @attr_url, 'https://api.vetaltacordoba.com.ar/v1'),
        (@vet_alta_cordoba, @attr_tecnologia, 'REST'),
        (@vet_alta_cordoba, @attr_parametros, 'timeoutMs=3000'),
        (@vet_alta_cordoba, @attr_api_key, 'vac-1a2b3c4d5e6f7081'),
        (@vet_cerro, @attr_url, 'https://ws.vetcerro.com.ar/soap'),
        (@vet_cerro, @attr_tecnologia, 'SOAP'),
        (@vet_cerro, @attr_parametros, 'timeoutMs=5000'),
        (@vet_cerro, @attr_api_key, 'vcr-2b3c4d5e6f708192'),
        (@vet_general_paz, @attr_url, 'https://api.vetgeneralpaz.com.ar/v1'),
        (@vet_general_paz, @attr_tecnologia, 'REST'),
        (@vet_general_paz, @attr_parametros, 'timeoutMs=3000'),
        (@vet_general_paz, @attr_api_key, 'vgp-3c4d5e6f70819203');

    INSERT INTO configuracion_veterinatarias (id_veterinaria, cod_atributo, valor)
    SELECT c.id_veterinaria, c.cod_atributo, c.valor
    FROM @config c
    WHERE NOT EXISTS (
        SELECT 1 FROM configuracion_veterinatarias x
        WHERE x.id_veterinaria = c.id_veterinaria AND x.cod_atributo = c.cod_atributo);

    -- =========================================================
    -- 3) PROFESIONALES EN VETERINARIAS (4: 1 dado de baja)
    -- =========================================================

    IF NOT EXISTS (SELECT 1 FROM profesionales_veterinarias WHERE id_veterinaria = @vet_nueva_cordoba AND id_profesional = @id_juanpablo)
        INSERT INTO profesionales_veterinarias (id_veterinaria, id_profesional, baja) VALUES (@vet_nueva_cordoba, @id_juanpablo, 0);
    IF NOT EXISTS (SELECT 1 FROM profesionales_veterinarias WHERE id_veterinaria = @vet_alta_cordoba AND id_profesional = @id_juanpablo)
        INSERT INTO profesionales_veterinarias (id_veterinaria, id_profesional, baja) VALUES (@vet_alta_cordoba, @id_juanpablo, 0);
    IF NOT EXISTS (SELECT 1 FROM profesionales_veterinarias WHERE id_veterinaria = @vet_cerro AND id_profesional = @id_juanpablo)
        INSERT INTO profesionales_veterinarias (id_veterinaria, id_profesional, baja) VALUES (@vet_cerro, @id_juanpablo, 0);
    IF NOT EXISTS (SELECT 1 FROM profesionales_veterinarias WHERE id_veterinaria = @vet_nueva_cordoba AND id_profesional = @id_esteban)
        INSERT INTO profesionales_veterinarias (id_veterinaria, id_profesional, baja) VALUES (@vet_nueva_cordoba, @id_esteban, 1);

    -- id_profesional no vinculado a ninguna veterinaria todavia: @id_victoria se usa solo como ciudadano deshabilitado

    -- =========================================================
    -- 4) REFUGIOS (3: 2 habilitados, 1 no habilitado)
    -- =========================================================

    IF NOT EXISTS (SELECT 1 FROM refugios WHERE razon_social = N'Refugio Huellitas Cordoba')
        INSERT INTO refugios (razon_social, correo, telefono, domicilio, habilitacion_municipal, id_responsable)
        VALUES (N'Refugio Huellitas Cordoba', 'contacto@huellitascba.org', '351-4100100', N'Camino a 60 Cuadras 1200, Cordoba', 'HM-2001', @id_marcos);
    IF NOT EXISTS (SELECT 1 FROM refugios WHERE razon_social = N'Refugio Patitas sin Hogar')
        INSERT INTO refugios (razon_social, correo, telefono, domicilio, habilitacion_municipal, id_responsable)
        VALUES (N'Refugio Patitas sin Hogar', 'contacto@patitassinhogar.org', '351-4200200', N'Ruta 20 Km 12, Cordoba', 'HM-2002', @id_marcos);
    IF NOT EXISTS (SELECT 1 FROM refugios WHERE razon_social = N'Refugio Esperanza Animal (no habilitado)')
        INSERT INTO refugios (razon_social, correo, telefono, domicilio, habilitacion_municipal, id_responsable)
        VALUES (N'Refugio Esperanza Animal (no habilitado)', 'contacto@esperanzaanimal.org', '351-4300300', N'Av. Circunvalacion 3400, Cordoba', NULL, @id_marcos);

    DECLARE @refugio_huellitas INT = (SELECT id_refugio FROM refugios WHERE razon_social = N'Refugio Huellitas Cordoba');
    DECLARE @refugio_patitas INT = (SELECT id_refugio FROM refugios WHERE razon_social = N'Refugio Patitas sin Hogar');
    DECLARE @refugio_esperanza INT = (SELECT id_refugio FROM refugios WHERE razon_social = N'Refugio Esperanza Animal (no habilitado)');

    -- =========================================================
    -- 5) MASCOTAS (12)
    -- =========================================================

    IF NOT EXISTS (SELECT 1 FROM mascotas WHERE microchip = '900000000000001')
        INSERT INTO mascotas (nombre, sexo, [año_nacimiento], microchip, vive, id_responsable, id_refugio)
        VALUES (N'Rocky', 'M', 2021, '900000000000001', 1, @id_ana, NULL);
    IF NOT EXISTS (SELECT 1 FROM mascotas WHERE nombre = N'Luna' AND [año_nacimiento] = 2020 AND id_responsable = @id_ana)
        INSERT INTO mascotas (nombre, sexo, [año_nacimiento], microchip, vive, id_responsable, id_refugio)
        VALUES (N'Luna', 'H', 2020, NULL, 1, @id_ana, NULL);
    IF NOT EXISTS (SELECT 1 FROM mascotas WHERE nombre = N'Toby' AND [año_nacimiento] = 2019 AND id_responsable = @id_ana)
        INSERT INTO mascotas (nombre, sexo, [año_nacimiento], microchip, vive, id_responsable, id_refugio)
        VALUES (N'Toby', 'M', 2019, NULL, 0, @id_ana, NULL);
    IF NOT EXISTS (SELECT 1 FROM mascotas WHERE microchip = '900000000000002')
        INSERT INTO mascotas (nombre, sexo, [año_nacimiento], microchip, vive, id_responsable, id_refugio)
        VALUES (N'Michi', 'H', 2022, '900000000000002', 1, @id_lucas, NULL);
    IF NOT EXISTS (SELECT 1 FROM mascotas WHERE nombre = N'Simon' AND [año_nacimiento] = 2021 AND id_responsable = @id_lucas)
        INSERT INTO mascotas (nombre, sexo, [año_nacimiento], microchip, vive, id_responsable, id_refugio)
        VALUES (N'Simon', 'M', 2021, NULL, 1, @id_lucas, NULL);
    IF NOT EXISTS (SELECT 1 FROM mascotas WHERE microchip = '900000000000003')
        INSERT INTO mascotas (nombre, sexo, [año_nacimiento], microchip, vive, id_responsable, id_refugio)
        VALUES (N'Nala', 'H', 2023, '900000000000003', 1, @id_carla, NULL);
    IF NOT EXISTS (SELECT 1 FROM mascotas WHERE nombre = N'Thor' AND [año_nacimiento] = 2022 AND id_responsable = @id_florencia)
        INSERT INTO mascotas (nombre, sexo, [año_nacimiento], microchip, vive, id_responsable, id_refugio)
        VALUES (N'Thor', 'M', 2022, NULL, 1, @id_florencia, NULL);
    IF NOT EXISTS (SELECT 1 FROM mascotas WHERE microchip = '900000000000004')
        INSERT INTO mascotas (nombre, sexo, [año_nacimiento], microchip, vive, id_responsable, id_refugio)
        VALUES (N'Mia', 'H', 2020, '900000000000004', 1, @id_florencia, NULL);
    IF NOT EXISTS (SELECT 1 FROM mascotas WHERE nombre = N'Bruno' AND [año_nacimiento] = 2021 AND id_responsable = @id_marcos)
        INSERT INTO mascotas (nombre, sexo, [año_nacimiento], microchip, vive, id_responsable, id_refugio)
        VALUES (N'Bruno', 'M', 2021, NULL, 1, @id_marcos, @refugio_huellitas);
    IF NOT EXISTS (SELECT 1 FROM mascotas WHERE nombre = N'Pelusa' AND [año_nacimiento] = 2022 AND id_responsable = @id_marcos)
        INSERT INTO mascotas (nombre, sexo, [año_nacimiento], microchip, vive, id_responsable, id_refugio)
        VALUES (N'Pelusa', 'H', 2022, NULL, 1, @id_marcos, @refugio_huellitas);
    IF NOT EXISTS (SELECT 1 FROM mascotas WHERE nombre = N'Max' AND [año_nacimiento] = 2020 AND id_responsable = @id_marcos)
        INSERT INTO mascotas (nombre, sexo, [año_nacimiento], microchip, vive, id_responsable, id_refugio)
        VALUES (N'Max', 'M', 2020, NULL, 1, @id_marcos, @refugio_patitas);
    IF NOT EXISTS (SELECT 1 FROM mascotas WHERE nombre = N'Coty' AND [año_nacimiento] = 2023 AND id_responsable = @id_ana)
        INSERT INTO mascotas (nombre, sexo, [año_nacimiento], microchip, vive, id_responsable, id_refugio)
        VALUES (N'Coty', 'H', 2023, NULL, 1, @id_ana, NULL);

    DECLARE @nrm_rocky INT = (SELECT nro_reg_municipal FROM mascotas WHERE microchip = '900000000000001');
    DECLARE @nrm_luna INT = (SELECT nro_reg_municipal FROM mascotas WHERE nombre = N'Luna' AND [año_nacimiento] = 2020 AND id_responsable = @id_ana);
    DECLARE @nrm_toby INT = (SELECT nro_reg_municipal FROM mascotas WHERE nombre = N'Toby' AND [año_nacimiento] = 2019 AND id_responsable = @id_ana);
    DECLARE @nrm_michi INT = (SELECT nro_reg_municipal FROM mascotas WHERE microchip = '900000000000002');
    DECLARE @nrm_simon INT = (SELECT nro_reg_municipal FROM mascotas WHERE nombre = N'Simon' AND [año_nacimiento] = 2021 AND id_responsable = @id_lucas);
    DECLARE @nrm_nala INT = (SELECT nro_reg_municipal FROM mascotas WHERE microchip = '900000000000003');
    DECLARE @nrm_thor INT = (SELECT nro_reg_municipal FROM mascotas WHERE nombre = N'Thor' AND [año_nacimiento] = 2022 AND id_responsable = @id_florencia);
    DECLARE @nrm_mia INT = (SELECT nro_reg_municipal FROM mascotas WHERE microchip = '900000000000004');
    DECLARE @nrm_bruno INT = (SELECT nro_reg_municipal FROM mascotas WHERE nombre = N'Bruno' AND [año_nacimiento] = 2021 AND id_responsable = @id_marcos);
    DECLARE @nrm_pelusa INT = (SELECT nro_reg_municipal FROM mascotas WHERE nombre = N'Pelusa' AND [año_nacimiento] = 2022 AND id_responsable = @id_marcos);
    DECLARE @nrm_max INT = (SELECT nro_reg_municipal FROM mascotas WHERE nombre = N'Max' AND [año_nacimiento] = 2020 AND id_responsable = @id_marcos);
    DECLARE @nrm_coty INT = (SELECT nro_reg_municipal FROM mascotas WHERE nombre = N'Coty' AND [año_nacimiento] = 2023 AND id_responsable = @id_ana);

    -- =========================================================
    -- 6) CARACTERISTICAS (Especie/Raza/Pelaje de cada mascota, via SP)
    --    Idempotencia: si la mascota ya tiene alguna caracteristica
    --    con el rasgo Especie, se asume que ya se cargo antes.
    -- =========================================================

    DECLARE @caracteristicas TABLE (nrm INT, dom_especie INT, dom_raza INT, dom_pelaje INT);
    INSERT INTO @caracteristicas (nrm, dom_especie, dom_raza, dom_pelaje) VALUES
        (@nrm_rocky, @dom_especie_perro, @dom_raza_labrador, @dom_pelaje_corto),
        (@nrm_luna, @dom_especie_perro, @dom_raza_mestizo, @dom_pelaje_corto),
        (@nrm_toby, @dom_especie_perro, @dom_raza_bordercollie, @dom_pelaje_largo),
        (@nrm_michi, @dom_especie_gato, @dom_raza_siames, @dom_pelaje_corto),
        (@nrm_simon, @dom_especie_perro, @dom_raza_mestizo, @dom_pelaje_corto),
        (@nrm_nala, @dom_especie_gato, @dom_raza_mestizo, @dom_pelaje_largo),
        (@nrm_thor, @dom_especie_perro, @dom_raza_labrador, @dom_pelaje_corto),
        (@nrm_mia, @dom_especie_gato, @dom_raza_mestizo, @dom_pelaje_corto),
        (@nrm_bruno, @dom_especie_perro, @dom_raza_mestizo, @dom_pelaje_corto),
        (@nrm_pelusa, @dom_especie_perro, @dom_raza_mestizo, @dom_pelaje_largo),
        (@nrm_max, @dom_especie_perro, @dom_raza_bordercollie, @dom_pelaje_corto),
        (@nrm_coty, @dom_especie_gato, @dom_raza_mestizo, @dom_pelaje_corto);

    DECLARE @nrm_actual INT, @dom_especie_actual INT, @dom_raza_actual INT, @dom_pelaje_actual INT;
    DECLARE cur_caracteristicas CURSOR LOCAL FAST_FORWARD FOR
        SELECT nrm, dom_especie, dom_raza, dom_pelaje FROM @caracteristicas;
    OPEN cur_caracteristicas;
    FETCH NEXT FROM cur_caracteristicas INTO @nrm_actual, @dom_especie_actual, @dom_raza_actual, @dom_pelaje_actual;
    WHILE @@FETCH_STATUS = 0
    BEGIN
        IF NOT EXISTS (
            SELECT 1 FROM caracteristicas_mascotas
            WHERE nro_reg_municipal = @nrm_actual AND cod_rasgo = @cod_rasgo_especie)
        BEGIN
            EXEC sp_InsertarCaracteristicaMascota
                @nro_reg_municipal = @nrm_actual, @cod_rasgo = @cod_rasgo_especie,
                @nro_valor_dominio = @dom_especie_actual, @valor_caracteristica = NULL;
            EXEC sp_InsertarCaracteristicaMascota
                @nro_reg_municipal = @nrm_actual, @cod_rasgo = @cod_rasgo_raza,
                @nro_valor_dominio = @dom_raza_actual, @valor_caracteristica = NULL;
            EXEC sp_InsertarCaracteristicaMascota
                @nro_reg_municipal = @nrm_actual, @cod_rasgo = @cod_rasgo_pelaje,
                @nro_valor_dominio = @dom_pelaje_actual, @valor_caracteristica = NULL;
        END
        FETCH NEXT FROM cur_caracteristicas INTO @nrm_actual, @dom_especie_actual, @dom_raza_actual, @dom_pelaje_actual;
    END
    CLOSE cur_caracteristicas;
    DEALLOCATE cur_caracteristicas;

    -- =========================================================
    -- 7) INFORMACION SANITARIA (18 atenciones, via SP)
    --    Idempotencia: si ya existe una fila con esa mascota+fecha+tipo,
    --    se asume que ya se cargo antes.
    -- =========================================================

    DECLARE @atenciones TABLE (
        nrm INT, fecha_atencion DATE, cod_tipo INT, detalle NVARCHAR(MAX),
        fecha_vencimiento DATE, id_vet INT, id_prof INT);
    INSERT INTO @atenciones (nrm, fecha_atencion, cod_tipo, detalle, fecha_vencimiento, id_vet, id_prof) VALUES
        (@nrm_rocky, '2025-03-10', @tipo_vacunacion, N'Antirrabica', '2026-03-10', @vet_nueva_cordoba, @id_juanpablo),
        (@nrm_rocky, '2025-09-15', @tipo_desparasitacion, N'Desparasitacion interna', '2026-03-15', @vet_nueva_cordoba, @id_juanpablo),
        (@nrm_luna, '2024-11-01', @tipo_vacunacion, N'Antirrabica', '2025-11-01', @vet_alta_cordoba, @id_juanpablo),
        (@nrm_luna, '2025-06-20', @tipo_esterilizacion, N'Esterilizacion', NULL, @vet_alta_cordoba, @id_juanpablo),
        (@nrm_toby, '2024-05-12', @tipo_vacunacion, N'Antirrabica', '2025-05-12', @vet_nueva_cordoba, @id_juanpablo),
        (@nrm_michi, '2025-08-05', @tipo_vacunacion, N'Triple felina', '2026-08-05', @vet_nueva_cordoba, @id_juanpablo),
        (@nrm_michi, '2025-08-05', @tipo_desparasitacion, N'Desparasitacion externa', '2026-02-05', @vet_nueva_cordoba, @id_juanpablo),
        (@nrm_simon, '2025-01-20', @tipo_vacunacion, N'Antirrabica', '2026-01-20', @vet_alta_cordoba, @id_juanpablo),
        (@nrm_nala, '2025-07-01', @tipo_vacunacion, N'Triple felina', '2026-07-01', @vet_cerro, @id_juanpablo),
        (@nrm_thor, '2024-12-15', @tipo_vacunacion, N'Antirrabica', '2025-12-15', @vet_nueva_cordoba, @id_juanpablo),
        (@nrm_thor, '2025-09-01', @tipo_desparasitacion, N'Desparasitacion interna', '2026-03-01', @vet_nueva_cordoba, @id_juanpablo),
        (@nrm_mia, '2025-04-18', @tipo_vacunacion, N'Triple felina', '2026-04-18', @vet_alta_cordoba, @id_juanpablo),
        (@nrm_bruno, '2025-02-10', @tipo_vacunacion, N'Antirrabica', '2026-02-10', @vet_nueva_cordoba, @id_juanpablo),
        (@nrm_bruno, '2025-02-10', @tipo_castracion, N'Castracion', NULL, @vet_nueva_cordoba, @id_juanpablo),
        (@nrm_pelusa, '2025-05-22', @tipo_vacunacion, N'Antirrabica', '2026-05-22', @vet_alta_cordoba, @id_juanpablo),
        (@nrm_max, '2024-08-30', @tipo_vacunacion, N'Antirrabica', '2025-08-30', @vet_cerro, @id_juanpablo),
        (@nrm_max, '2025-09-10', @tipo_desparasitacion, N'Desparasitacion interna', '2026-03-10', @vet_cerro, @id_juanpablo),
        (@nrm_coty, '2025-06-15', @tipo_vacunacion, N'Triple felina', '2026-06-15', @vet_nueva_cordoba, @id_juanpablo);

    DECLARE @at_nrm INT, @at_fecha DATE, @at_tipo INT, @at_detalle NVARCHAR(MAX), @at_vencimiento DATE, @at_vet INT, @at_prof INT;
    DECLARE cur_atenciones CURSOR LOCAL FAST_FORWARD FOR
        SELECT nrm, fecha_atencion, cod_tipo, detalle, fecha_vencimiento, id_vet, id_prof FROM @atenciones;
    OPEN cur_atenciones;
    FETCH NEXT FROM cur_atenciones INTO @at_nrm, @at_fecha, @at_tipo, @at_detalle, @at_vencimiento, @at_vet, @at_prof;
    WHILE @@FETCH_STATUS = 0
    BEGIN
        IF NOT EXISTS (
            SELECT 1 FROM informacion_sanitaria
            WHERE nro_reg_municipal = @at_nrm AND fecha_atencion = @at_fecha AND cod_tipo_atencion = @at_tipo)
        BEGIN
            EXEC sp_InsertarInformacionSanitaria
                @nro_reg_municipal = @at_nrm, @fecha_atencion = @at_fecha, @cod_tipo_atencion = @at_tipo,
                @detalle_atencion = @at_detalle, @fecha_vencimiento = @at_vencimiento,
                @id_veterinaria = @at_vet, @id_profesional = @at_prof;
        END
        FETCH NEXT FROM cur_atenciones INTO @at_nrm, @at_fecha, @at_tipo, @at_detalle, @at_vencimiento, @at_vet, @at_prof;
    END
    CLOSE cur_atenciones;
    DEALLOCATE cur_atenciones;

    -- =========================================================
    -- 8) PUBLICACIONES DE ADOPCION (4: 2 Activa, 1 Pausada, 1 Finalizada)
    -- =========================================================

    IF NOT EXISTS (SELECT 1 FROM publicaciones_adopcion WHERE nro_reg_municipal = @nrm_bruno)
        INSERT INTO publicaciones_adopcion (nro_reg_municipal, id_refugio, fecha_publicacion, caracteristicas_mascota, condicion_adopcion, estado_publicacion)
        VALUES (@nrm_bruno, @refugio_huellitas, '2025-09-01', N'Perro mestizo adulto, muy sociable con otros perros', N'Se entrega con seguimiento post adopcion', N'Activa');

    IF NOT EXISTS (SELECT 1 FROM publicaciones_adopcion WHERE nro_reg_municipal = @nrm_pelusa)
        INSERT INTO publicaciones_adopcion (nro_reg_municipal, id_refugio, fecha_publicacion, caracteristicas_mascota, condicion_adopcion, estado_publicacion)
        VALUES (@nrm_pelusa, @refugio_huellitas, '2025-09-10', N'Perra joven, buena con niños', N'Requiere patio', N'Activa');

    IF NOT EXISTS (SELECT 1 FROM publicaciones_adopcion WHERE nro_reg_municipal = @nrm_max)
        INSERT INTO publicaciones_adopcion (nro_reg_municipal, id_refugio, fecha_publicacion, caracteristicas_mascota, condicion_adopcion, estado_publicacion)
        VALUES (@nrm_max, @refugio_patitas, '2025-07-01', N'Perro adulto tranquilo', N'Se entrega castrado', N'Pausada');

    IF NOT EXISTS (SELECT 1 FROM publicaciones_adopcion WHERE nro_reg_municipal = @nrm_toby)
        INSERT INTO publicaciones_adopcion (nro_reg_municipal, id_refugio, fecha_publicacion, caracteristicas_mascota, condicion_adopcion, estado_publicacion)
        VALUES (@nrm_toby, @refugio_huellitas, '2024-01-15', N'Perro mayor, bajo nivel de actividad', N'Adoptado y luego fallecido', N'Finalizada');

    DECLARE @pub_bruno INT = (SELECT nro_publicacion FROM publicaciones_adopcion WHERE nro_reg_municipal = @nrm_bruno);
    DECLARE @pub_pelusa INT = (SELECT nro_publicacion FROM publicaciones_adopcion WHERE nro_reg_municipal = @nrm_pelusa);
    DECLARE @pub_max INT = (SELECT nro_publicacion FROM publicaciones_adopcion WHERE nro_reg_municipal = @nrm_max);
    DECLARE @pub_toby INT = (SELECT nro_publicacion FROM publicaciones_adopcion WHERE nro_reg_municipal = @nrm_toby);

    COMMIT TRANSACTION;

    PRINT 'Datos de prueba cargados correctamente.';
END TRY
BEGIN CATCH
    IF @@TRANCOUNT > 0
        ROLLBACK TRANSACTION;

    DECLARE @mensaje_error NVARCHAR(4000) = ERROR_MESSAGE();
    DECLARE @numero_error INT = ERROR_NUMBER();
    RAISERROR('Fallo la carga de datos de prueba (error %d): %s', 16, 1, @numero_error, @mensaje_error);
END CATCH;
GO

-- =========================================================
-- 9) CONSULTAS DE CONTROL
-- =========================================================

SELECT 'ciudadanos' AS tabla, COUNT(*) AS cantidad FROM ciudadanos
UNION ALL SELECT 'veterinarias', COUNT(*) FROM veterinarias
UNION ALL SELECT 'configuracion_veterinatarias', COUNT(*) FROM configuracion_veterinatarias
UNION ALL SELECT 'profesionales_veterinarias', COUNT(*) FROM profesionales_veterinarias
UNION ALL SELECT 'refugios', COUNT(*) FROM refugios
UNION ALL SELECT 'mascotas', COUNT(*) FROM mascotas
UNION ALL SELECT 'caracteristicas_mascotas', COUNT(*) FROM caracteristicas_mascotas
UNION ALL SELECT 'informacion_sanitaria', COUNT(*) FROM informacion_sanitaria
UNION ALL SELECT 'publicaciones_adopcion', COUNT(*) FROM publicaciones_adopcion;

SELECT * FROM vw_carnet_sanitario
WHERE nro_reg_municipal IN (
    SELECT nro_reg_municipal FROM mascotas WHERE microchip = '900000000000001'
    UNION
    SELECT nro_reg_municipal FROM mascotas WHERE nombre = N'Bruno'
);

SELECT id_ciudadano, cuil, apellido, nombre, habilitado FROM ciudadanos
ORDER BY id_ciudadano;

SELECT id_veterinaria, razon_social, habilitacion_municipal FROM veterinarias ORDER BY id_veterinaria;

SELECT id_refugio, razon_social, habilitacion_municipal, id_responsable FROM refugios ORDER BY id_refugio;

SELECT nro_reg_municipal, nombre, microchip, id_responsable, id_refugio, vive FROM mascotas ORDER BY nro_reg_municipal;

SELECT nro_publicacion, nro_reg_municipal, id_refugio, estado_publicacion FROM publicaciones_adopcion ORDER BY nro_publicacion;
