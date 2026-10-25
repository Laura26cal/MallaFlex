-- -----------------------------------------------------
-- carrera
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS carrera (
  codigo            TEXT PRIMARY KEY,
  nombre            TEXT NOT NULL UNIQUE,
  plan              TEXT NOT NULL,
  creditos_totales  INTEGER NOT NULL CHECK (creditos_totales > 0),
  num_semestres     INTEGER NOT NULL CHECK (num_semestres > 0),
  creditos_libre_eleccion  INTEGER NOT NULL DEFAULT 0 CHECK (creditos_libre_eleccion >= 0)
);

-- -----------------------------------------------------
-- usuario
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS usuario (
  id              INTEGER PRIMARY KEY AUTOINCREMENT,
  nombre          TEXT NOT NULL,
  correo          TEXT NOT NULL UNIQUE,
  password        TEXT NOT NULL,
  rol             TEXT NOT NULL CHECK (rol IN ('ESTUDIANTE', 'ADMIN')),
  carrera_codigo  TEXT,
  FOREIGN KEY (carrera_codigo) REFERENCES carrera (codigo)
);

CREATE INDEX IF NOT EXISTS idx_usuario_carrera
  ON usuario (carrera_codigo);

-- -----------------------------------------------------
-- materia
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS materia (
  codigo    TEXT PRIMARY KEY,
  nombre    TEXT NOT NULL,
  creditos  INTEGER NOT NULL CHECK (creditos > 0)
);

-- -----------------------------------------------------
-- carrera_materia (qué materias tiene cada carrera)
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS carrera_materia (
  carrera_codigo     TEXT NOT NULL,
  materia_codigo     TEXT NOT NULL,
  tipologia          TEXT,
  semestre_sugerido  INTEGER CHECK (semestre_sugerido > 0),
  requiere_malla_completa  INTEGER NOT NULL DEFAULT 0 CHECK (requiere_malla_completa IN (0, 1)),
  PRIMARY KEY (carrera_codigo, materia_codigo),
  FOREIGN KEY (carrera_codigo) REFERENCES carrera (codigo) ON DELETE CASCADE,
  FOREIGN KEY (materia_codigo) REFERENCES materia (codigo) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_carrera_materia_materia
  ON carrera_materia (materia_codigo);

-- -----------------------------------------------------
-- prerrequisito
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS prerrequisito (
  materia_codigo    TEXT NOT NULL,
  requisito_codigo  TEXT NOT NULL,
  PRIMARY KEY (materia_codigo, requisito_codigo),
  CHECK (materia_codigo <> requisito_codigo),
  FOREIGN KEY (materia_codigo)   REFERENCES materia (codigo) ON DELETE CASCADE,
  FOREIGN KEY (requisito_codigo) REFERENCES materia (codigo) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_prerrequisito_requisito
  ON prerrequisito (requisito_codigo);

-- -----------------------------------------------------
-- correquisito
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS correquisito (
  materia_codigo       TEXT NOT NULL,
  correquisito_codigo  TEXT NOT NULL,
  PRIMARY KEY (materia_codigo, correquisito_codigo),
  CHECK (materia_codigo <> correquisito_codigo),
  FOREIGN KEY (materia_codigo)      REFERENCES materia (codigo) ON DELETE CASCADE,
  FOREIGN KEY (correquisito_codigo) REFERENCES materia (codigo) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_correquisito_correquisito
  ON correquisito (correquisito_codigo);

-- -----------------------------------------------------
-- materia_opcion (para optativas por ahora solo trabajo de grado)
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS materia_opcion (
  materia_codigo  TEXT NOT NULL,
  opcion_codigo   TEXT NOT NULL,
  PRIMARY KEY (materia_codigo, opcion_codigo),
  CHECK (materia_codigo <> opcion_codigo),
  FOREIGN KEY (materia_codigo) REFERENCES materia (codigo) ON DELETE CASCADE,
  FOREIGN KEY (opcion_codigo)  REFERENCES materia (codigo) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_materia_opcion_opcion
  ON materia_opcion (opcion_codigo);
  
-- -----------------------------------------------------
-- historia_academica
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS historia_academica (
  id                INTEGER PRIMARY KEY AUTOINCREMENT,
  usuario_id        INTEGER NOT NULL,
  materia_codigo    TEXT NOT NULL,
  numero_matricula  INTEGER NOT NULL CHECK (numero_matricula > 0),
  nota              REAL CHECK (nota BETWEEN 0.0 AND 5.0),
  estado            TEXT NOT NULL CHECK (estado IN ('APROBADA', 'EN_CURSO', 'PERDIDA')),
  UNIQUE (usuario_id, materia_codigo, numero_matricula),
  FOREIGN KEY (usuario_id)     REFERENCES usuario (id) ON DELETE CASCADE,
  FOREIGN KEY (materia_codigo) REFERENCES materia (codigo)
);

CREATE INDEX IF NOT EXISTS idx_historia_materia
  ON historia_academica (materia_codigo);

-- -----------------------------------------------------
-- nivelacion_requerida (para ver si le toca o no nivelar)
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS nivelacion_requerida (
  usuario_id      INTEGER NOT NULL,
  materia_codigo  TEXT NOT NULL,
  PRIMARY KEY (usuario_id, materia_codigo),
  FOREIGN KEY (usuario_id)     REFERENCES usuario (id) ON DELETE CASCADE,
  FOREIGN KEY (materia_codigo) REFERENCES materia (codigo) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_nivelacion_materia
  ON nivelacion_requerida (materia_codigo);
  
-- -----------------------------------------------------
-- parametro (reglas que se pueden cambiar sin tocar el código)
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS parametro (
  clave  TEXT PRIMARY KEY,
  valor  INTEGER NOT NULL
);

-- -----------------------------------------------------
-- libre_eleccion (el usuario la registra)
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS libre_eleccion (
  id                INTEGER PRIMARY KEY AUTOINCREMENT,
  usuario_id        INTEGER NOT NULL,
  nombre            TEXT NOT NULL,
  creditos          INTEGER NOT NULL CHECK (creditos > 0),
  numero_matricula  INTEGER NOT NULL CHECK (numero_matricula > 0),
  nota              REAL CHECK (nota BETWEEN 0.0 AND 5.0),
  estado            TEXT NOT NULL CHECK (estado IN ('APROBADA', 'EN_CURSO', 'PERDIDA')),
  FOREIGN KEY (usuario_id) REFERENCES usuario (id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_libre_eleccion_usuario
  ON libre_eleccion (usuario_id);

INSERT OR IGNORE INTO parametro (clave, valor) VALUES
  ('creditos_minimos', 10),
  ('creditos_sobrecarga', 21);