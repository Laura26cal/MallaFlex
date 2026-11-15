INSERT OR IGNORE INTO usuario (id, nombre, correo, password, rol, carrera_codigo)
VALUES 
(1, 'Usuario de prueba', 'prueba@unal.edu.co', 'pbkdf2$120000$gACzuhfK8aKEe0PswtFuYA==$/uXg9gpUEcAWKg+S68PL6baG+UlfPykeB4X6FYcAyI4=', 'ESTUDIANTE', '2A74'),
(2, 'Administrador MallaFlex', 'admin@mallaflex.com','pbkdf2$120000$aKMWjDmJWQDxRINAfZz1sA==$7PRCCyO4Yp8yqbjvn7lp1QQfWZBTbnouuCp4hTn4RbE=','ADMIN', NULL),
(3, 'Estudiante de Prueba', 'estudiante@mallaflex.com', 'pbkdf2$120000$r7LANX2CDiKorqyYui0Q6g==$JER3SsESRSw3VXzuD8Twj5F7JfcMK1tZR9mqR0zaUic=', 'ESTUDIANTE', '2A74');