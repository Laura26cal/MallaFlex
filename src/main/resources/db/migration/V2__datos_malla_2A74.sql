INSERT OR IGNORE INTO carrera (codigo, nombre, plan, creditos_totales, num_semestres, creditos_libre_eleccion)
VALUES ('2A74', 'Ingeniería de Sistemas y Computación', '2A74', 165, 10, 33);

INSERT OR IGNORE INTO materia (codigo, nombre, creditos) VALUES
-- Nivelacion
  ('1000001',   'Matemáticas básicas', 4),
  ('1000002',   'Lecto-escritura', 4),
  ('1000044',   'Inglés I', 3),
  ('1000045',   'Inglés II', 3),
  ('1000046',   'Inglés III', 3),
  ('1000047',   'Inglés IV', 3),
-- Primer semestre
  ('1000004',   'Cálculo diferencial', 4),
  ('1000089-B', 'Cátedra nacional de inducción y preparación para la vida universitaria', 2),
  ('2015734',   'Programación de computadores', 3),
  ('2016703',   'Pensamiento sistémico', 3),
  ('2025975',   'Introducción a la ingeniería de sistemas y computación', 3),
-- Segundo semestre  
  ('1000019',   'Fundamentos de mecánica', 4),
  ('1000005',   'Cálculo integral', 4),
  ('1000003',   'Álgebra lineal', 4),
  ('2016375',   'Programación orientada a objetos', 3),
-- Tercer semestre  
  ('1000017',   'Fundamentos de electricidad y magnetismo', 4),
  ('1000006',   'Cálculo en varias variables', 4),
  ('2025963',   'Matemáticas discretas I', 4),
  ('2016353',   'Bases de datos', 3),
  ('2016698',   'Elementos de computadores', 3),
-- Cuarto semestre  
  ('1000013',   'Probabilidad y estadística fundamental', 3),
  ('2015703',   'Ingeniería económica', 3),
  ('2025964',   'Matemáticas discretas II', 4),
  ('2016699',   'Estructuras de datos', 3),
  ('2016697',   'Arquitectura de computadores', 3),
-- Quinto semestre  
  ('2025970',   'Modelos y simulación', 3),
  ('2015702',   'Gerencia y gestión de proyectos', 3),
  ('2025967',   'Redes de computadores', 3),
  ('2016701',   'Ingeniería de software I', 3),
  ('2015174',   'Introducción a la teória de la computación', 4),
-- Sexto semestre  
  ('2025971',   'Optimización', 3),
  ('2025982',   'Sistemas de información', 3),
  ('2015970',   'Métodos Númericos', 3),
  ('2016702',   'Ingeniería de software II', 3),
  ('2016696',   'Algoritmos', 3),
  ('2016707',   'Sistemas operativos', 3),
-- Septimo semestre  
  ('2025969',   'Modelos estocastícos y simulación en computación y comunicaciones', 3),
  ('2025983',   'Arquitectura de infraestructura y gobierno de TICs', 3),
  ('2025994',   'Teoría de la Información y Sistemas de Comunicaciones', 3),
  ('2016716',   'Arquitectura de software', 3),
  ('2025995',   'Introducción a los sistemas inteligentes', 3),
  ('2025966',   'Lenguajes de programación', 3),
-- Octavo semestre
  ('2024045',   'Taller de proyectos interdisciplinarios', 3),
  ('2025968',   'Computación paralela y distribuida', 3),
  ('2025972',   'Introducción a la criptografía y a la seguridad de la Información', 3),
  ('2025960',   'Computación visual', 3),
-- ELETIVA  
-- Noveno semestre
-- ELECTIVAS
-- Decimo semestre
-- ELECTIVAS
  ('TRABAJO-GRADO', 'Trabajo de grado', 6),
  ('2025974', 'Trabajo de grado - Trabajo investigativo', 6),
  ('2025973', 'Trabajo de grado - Práctica de extensión', 6),
  ('2016843', 'Trabajo de grado - Asignaturas de posgrado', 6);

INSERT OR IGNORE INTO carrera_materia (carrera_codigo, materia_codigo, tipologia, semestre_sugerido) VALUES
-- Nivelacion
  ('2A74', '1000001',   'P', 1),
  ('2A74', '1000002',   'P', 1),
  ('2A74', '1000044',   'P', 3),
  ('2A74', '1000045',   'P', 4),
  ('2A74', '1000046',   'P', 5),
  ('2A74', '1000047',   'P', 6),
-- Primer semestre
  ('2A74', '1000004',   'B', 1),
  ('2A74', '1000089-B', 'L', 1),
  ('2A74', '2015734',   'C', 1),
  ('2A74', '2016703',   'C', 1),
  ('2A74', '2025975',   'C', 1),
-- Segundo semestre   
  ('2A74', '1000019',   'B', 2),
  ('2A74', '1000005',   'B', 2),
  ('2A74', '1000003',   'B', 2),
  ('2A74', '2016375',   'C', 2),
-- Tercer semestre  
  ('2A74', '1000017',   'B', 3),
  ('2A74', '1000006',   'B', 3),
  ('2A74', '2025963',   'B', 3),
  ('2A74', '2016353',   'C', 3),
  ('2A74', '2016698',   'C', 3),
-- Cuarto semestre    
  ('2A74', '1000013',   'B', 4),
  ('2A74', '2015703',   'B', 4),
  ('2A74', '2025964',   'B', 4),
  ('2A74', '2016699',   'C', 4),
  ('2A74', '2016697',   'C', 4),
-- Quinto semestre  
  ('2A74', '2025970',   'C', 5),
  ('2A74', '2015702',   'B', 5),
  ('2A74', '2025967',   'C', 5),
  ('2A74', '2016701',   'C', 5),
  ('2A74', '2015174',   'B', 5),
-- Sexto semestre
  ('2A74', '2025971',   'C', 6),
  ('2A74', '2025982',   'C', 6),
  ('2A74', '2015970',   'B', 6),
  ('2A74', '2016702',   'C', 6),
  ('2A74', '2016696',   'B', 6),
  ('2A74', '2016707',   'C', 6),
-- Septimo semestre
  ('2A74', '2025969',   'C', 7),
  ('2A74', '2025983',   'C', 7),
  ('2A74', '2025994',   'C', 7),
  ('2A74', '2016716',   'C', 7),
  ('2A74', '2025995',   'C', 7),
  ('2A74', '2025966',   'C', 7),
-- Octavo semestre
  ('2A74', '2024045',   'C', 8),
  ('2A74', '2025968',   'C', 8),
  ('2A74', '2025972',   'C', 8),
  ('2A74', '2025960',   'C', 8);

-- Decimo semestre
INSERT OR IGNORE INTO carrera_materia (carrera_codigo, materia_codigo, tipologia, semestre_sugerido, requiere_malla_completa) VALUES
  ('2A74', 'TRABAJO-GRADO', 'C', 10, 1);

INSERT OR IGNORE INTO prerrequisito (materia_codigo, requisito_codigo) VALUES
-- Nivelacion Ingles
  ('1000045', '1000044'),
  ('1000046', '1000045'),
  ('1000047', '1000046'),
-- Primer semestre  
  ('1000004', '1000001'),
-- Segundo semestre  
  ('1000005', '1000004'),
  ('1000019', '1000004'),
  ('1000003', '1000004'),
  ('2016375', '2015734'),
-- Tercer semestre  
  ('1000017', '1000005'),
  ('1000006', '1000005'),
  ('2025963', '1000003'),
  ('2016353', '2016375'),
  ('2016698', '2025975'),
-- Cuarto semestre  
  ('1000013', '1000005'),
  ('2015703', '1000005'),
  ('2025964', '2025963'),
  ('2016699', '2016375'),
  ('2016697', '2016698'),
-- Quinto semestre  
  ('2025970', '1000013'),
  ('2025970', '2016375'),
  ('2025970', '1000006'),
  ('2025970', '2025964'),
  ('2015702', '2015703'),
  ('2025967', '1000017'),
  ('2025967', '2016699'),
  ('2025967', '2016697'),
  ('2016701', '2016699'),
  ('2016701', '2016703'),
  ('2016701', '2016353'),
  ('2015174', '2025963'),
-- Sexto semestre
  ('2025971', '2025970'),
  ('2025982', '2015702'),
  ('2025982', '2016353'),
  ('2025982', '2016703'),
  ('2015970', '1000006'),
  ('2016702', '2016701'),
  ('2016702', '2025967'),
  ('2016696', '1000013'),
  ('2016696', '2025964'),
  ('2016696', '2016699'),
  ('2016707', '2016697'),
-- Septimo semestre
  ('2025969', '2025971'),
  ('2025983', '2025982'),
  ('2025983', '2016702'),
  ('2025994', '2025967'),
  ('2025994', '1000013'),
  ('2016716', '2016702'),
  ('2025995', '2016696'),
  ('2025966', '2015174'),
  ('2025966', '2016699'),
-- Octavo semestre
  ('2024045', '2015702'),
  ('2025968', '2016696'),
  ('2025972', '2016696'),
  ('2025960', '2016696');

INSERT OR IGNORE INTO materia_opcion (materia_codigo, opcion_codigo) VALUES
  ('TRABAJO-GRADO', '2025974'),
  ('TRABAJO-GRADO', '2025973'),
  ('TRABAJO-GRADO', '2016843');