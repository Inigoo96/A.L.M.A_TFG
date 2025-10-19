-- ============================================
-- QUERIES DE VERIFICACIÓN - ALMA Backend
-- ============================================
-- Usa estas queries para verificar que los tests funcionaron correctamente

-- ============================================
-- 1. RESUMEN GENERAL DEL SISTEMA
-- ============================================

SELECT
    'Organizaciones' as tipo, COUNT(*) as cantidad FROM organizacion
UNION ALL
SELECT
    'Usuarios Totales', COUNT(*) FROM usuario
UNION ALL
SELECT
    'Admins Organizacion', COUNT(*) FROM usuario WHERE tipo_usuario = 'ADMIN_ORGANIZACION'
UNION ALL
SELECT
    'Profesionales', COUNT(*) FROM profesional
UNION ALL
SELECT
    'Pacientes', COUNT(*) FROM paciente
UNION ALL
SELECT
    'Asignaciones Activas', COUNT(*) FROM asignacion_profesional_paciente WHERE activo = true;

-- ============================================
-- 2. DETALLE DE ORGANIZACIONES Y SUS USUARIOS
-- ============================================

SELECT
    o.id_organizacion,
    o.nombre_organizacion,
    o.cif,
    COUNT(u.id_usuario) as total_usuarios,
    COUNT(CASE WHEN u.tipo_usuario = 'ADMIN_ORGANIZACION' THEN 1 END) as admins,
    COUNT(CASE WHEN u.tipo_usuario = 'PROFESIONAL' THEN 1 END) as profesionales,
    COUNT(CASE WHEN u.tipo_usuario = 'PACIENTE' THEN 1 END) as pacientes
FROM organizacion o
LEFT JOIN usuario u ON o.id_organizacion = u.organizacion_id
GROUP BY o.id_organizacion, o.nombre_organizacion, o.cif
ORDER BY o.id_organizacion;

-- ============================================
-- 3. TODOS LOS USUARIOS CON SU INFORMACIÓN COMPLETA
-- ============================================

SELECT
    u.id_usuario,
    u.email,
    u.nombre,
    u.apellidos,
    u.tipo_usuario,
    o.nombre_organizacion,
    u.activo,
    u.fecha_registro
FROM usuario u
JOIN organizacion o ON u.organizacion_id = o.id_organizacion
ORDER BY o.id_organizacion,
    CASE u.tipo_usuario
        WHEN 'SUPER_ADMIN' THEN 1
        WHEN 'ADMIN_ORGANIZACION' THEN 2
        WHEN 'PROFESIONAL' THEN 3
        WHEN 'PACIENTE' THEN 4
    END,
    u.nombre;

-- ============================================
-- 4. PROFESIONALES CON SU INFORMACIÓN DETALLADA
-- ============================================

SELECT
    p.id_profesional,
    u.email,
    u.nombre || ' ' || u.apellidos AS nombre_completo,
    p.numero_colegiado,
    p.especialidad,
    o.nombre_organizacion,
    COUNT(a.id_asignacion) as pacientes_asignados
FROM profesional p
JOIN usuario u ON p.id_usuario = u.id_usuario
JOIN organizacion o ON u.organizacion_id = o.id_organizacion
LEFT JOIN asignacion_profesional_paciente a ON p.id_profesional = a.id_profesional AND a.activo = true
GROUP BY p.id_profesional, u.email, u.nombre, u.apellidos, p.numero_colegiado, p.especialidad, o.nombre_organizacion
ORDER BY o.nombre_organizacion, u.nombre;

-- ============================================
-- 5. PACIENTES CON SU INFORMACIÓN DETALLADA
-- ============================================

SELECT
    pac.id_paciente,
    u.email,
    u.nombre || ' ' || u.apellidos AS nombre_completo,
    pac.fecha_nacimiento,
    pac.genero,
    o.nombre_organizacion,
    COUNT(a.id_asignacion) as profesionales_asignados
FROM paciente pac
JOIN usuario u ON pac.id_usuario = u.id_usuario
JOIN organizacion o ON u.organizacion_id = o.id_organizacion
LEFT JOIN asignacion_profesional_paciente a ON pac.id_paciente = a.id_paciente AND a.activo = true
GROUP BY pac.id_paciente, u.email, u.nombre, u.apellidos, pac.fecha_nacimiento, pac.genero, o.nombre_organizacion
ORDER BY o.nombre_organizacion, u.nombre;

-- ============================================
-- 6. TODAS LAS ASIGNACIONES PROFESIONAL-PACIENTE
-- ============================================

SELECT
    a.id_asignacion,
    o.nombre_organizacion,
    prof_u.nombre || ' ' || prof_u.apellidos AS profesional,
    prof.especialidad,
    pac_u.nombre || ' ' || pac_u.apellidos AS paciente,
    a.es_principal,
    a.activo,
    a.fecha_asignacion
FROM asignacion_profesional_paciente a
JOIN profesional prof ON a.id_profesional = prof.id_profesional
JOIN paciente pac ON a.id_paciente = pac.id_paciente
JOIN usuario prof_u ON prof.id_usuario = prof_u.id_usuario
JOIN usuario pac_u ON pac.id_usuario = pac_u.id_usuario
JOIN organizacion o ON prof_u.organizacion_id = o.id_organizacion
ORDER BY o.nombre_organizacion, prof_u.nombre, a.fecha_asignacion DESC;

-- ============================================
-- 7. VERIFICAR INTEGRIDAD: Usuarios sin perfil específico
-- ============================================
-- Nota: Los ADMIN_ORGANIZACION no tienen tabla específica, pero sí AdminOrganizacion si existe

SELECT
    u.id_usuario,
    u.email,
    u.nombre,
    u.apellidos,
    u.tipo_usuario,
    CASE
        WHEN u.tipo_usuario = 'PROFESIONAL' AND p.id_profesional IS NULL THEN 'FALTA PERFIL PROFESIONAL'
        WHEN u.tipo_usuario = 'PACIENTE' AND pac.id_paciente IS NULL THEN 'FALTA PERFIL PACIENTE'
        ELSE 'OK'
    END as estado_perfil
FROM usuario u
LEFT JOIN profesional p ON u.id_usuario = p.id_usuario
LEFT JOIN paciente pac ON u.id_usuario = pac.id_usuario
WHERE
    (u.tipo_usuario = 'PROFESIONAL' AND p.id_profesional IS NULL)
    OR (u.tipo_usuario = 'PACIENTE' AND pac.id_paciente IS NULL);

-- Si esta query no devuelve resultados, significa que todos los usuarios tienen su perfil correcto

-- ============================================
-- 8. VERIFICAR AISLAMIENTO: Asignaciones cruzadas entre organizaciones
-- ============================================
-- Esta query debe devolver 0 resultados si el aislamiento funciona correctamente

SELECT
    a.id_asignacion,
    prof_u.email AS profesional_email,
    o_prof.nombre_organizacion AS org_profesional,
    pac_u.email AS paciente_email,
    o_pac.nombre_organizacion AS org_paciente
FROM asignacion_profesional_paciente a
JOIN profesional prof ON a.id_profesional = prof.id_profesional
JOIN paciente pac ON a.id_paciente = pac.id_paciente
JOIN usuario prof_u ON prof.id_usuario = prof_u.id_usuario
JOIN usuario pac_u ON pac.id_usuario = pac_u.id_usuario
JOIN organizacion o_prof ON prof_u.organizacion_id = o_prof.id_organizacion
JOIN organizacion o_pac ON pac_u.organizacion_id = o_pac.id_organizacion
WHERE o_prof.id_organizacion != o_pac.id_organizacion;

-- Si esta query devuelve resultados, HAY UN PROBLEMA DE SEGURIDAD

-- ============================================
-- 9. ESTADÍSTICAS POR ORGANIZACIÓN
-- ============================================

WITH stats AS (
    SELECT
        o.id_organizacion,
        o.nombre_organizacion,
        COUNT(DISTINCT CASE WHEN u.tipo_usuario = 'PROFESIONAL' THEN p.id_profesional END) as total_profesionales,
        COUNT(DISTINCT CASE WHEN u.tipo_usuario = 'PACIENTE' THEN pac.id_paciente END) as total_pacientes,
        COUNT(DISTINCT a.id_asignacion) as total_asignaciones
    FROM organizacion o
    LEFT JOIN usuario u ON o.id_organizacion = u.organizacion_id
    LEFT JOIN profesional p ON u.id_usuario = p.id_usuario
    LEFT JOIN paciente pac ON u.id_usuario = pac.id_usuario
    LEFT JOIN asignacion_profesional_paciente a ON
        (p.id_profesional = a.id_profesional OR pac.id_paciente = a.id_paciente) AND a.activo = true
    GROUP BY o.id_organizacion, o.nombre_organizacion
)
SELECT
    nombre_organizacion,
    total_profesionales,
    total_pacientes,
    total_asignaciones,
    CASE
        WHEN total_profesionales > 0 THEN ROUND(total_pacientes::numeric / total_profesionales::numeric, 2)
        ELSE 0
    END as ratio_pacientes_por_profesional
FROM stats
ORDER BY id_organizacion;

-- ============================================
-- 10. VERIFICAR CONSTRAINT: Email único
-- ============================================

SELECT
    email,
    COUNT(*) as veces_usado
FROM usuario
GROUP BY email
HAVING COUNT(*) > 1;

-- Si esta query devuelve resultados, hay emails duplicados (NO DEBERÍA PASAR)

-- ============================================
-- 11. VERIFICAR CONSTRAINT: CIF único
-- ============================================

SELECT
    cif,
    COUNT(*) as veces_usado
FROM organizacion
GROUP BY cif
HAVING COUNT(*) > 1;

-- Si esta query devuelve resultados, hay CIFs duplicados (NO DEBERÍA PASAR)

-- ============================================
-- 12. ÚLTIMAS ACTIVIDADES (Registros y Asignaciones)
-- ============================================

SELECT
    'Usuario Registrado' as tipo_actividad,
    u.nombre || ' ' || u.apellidos as quien,
    u.email,
    o.nombre_organizacion as organizacion,
    u.fecha_registro as fecha
FROM usuario u
JOIN organizacion o ON u.organizacion_id = o.id_organizacion
UNION ALL
SELECT
    'Asignación Creada' as tipo_actividad,
    prof_u.nombre || ' asignado a ' || pac_u.nombre as quien,
    '' as email,
    o.nombre_organizacion,
    a.fecha_asignacion as fecha
FROM asignacion_profesional_paciente a
JOIN profesional prof ON a.id_profesional = prof.id_profesional
JOIN paciente pac ON a.id_paciente = pac.id_paciente
JOIN usuario prof_u ON prof.id_usuario = prof_u.id_usuario
JOIN usuario pac_u ON pac.id_usuario = pac_u.id_usuario
JOIN organizacion o ON prof_u.organizacion_id = o.id_organizacion
ORDER BY fecha DESC
LIMIT 20;

-- ============================================
-- 13. BUSCAR PACIENTES POR ORGANIZACIÓN
-- ============================================
-- Ejemplo: buscar todos los pacientes de "Hospital La Paz"

SELECT
    pac.id_paciente,
    u.nombre || ' ' || u.apellidos AS nombre_completo,
    u.email,
    pac.fecha_nacimiento,
    pac.genero
FROM paciente pac
JOIN usuario u ON pac.id_usuario = u.id_usuario
JOIN organizacion o ON u.organizacion_id = o.id_organizacion
WHERE o.nombre_organizacion = 'Hospital La Paz';

-- ============================================
-- 14. BUSCAR PROFESIONALES POR ESPECIALIDAD
-- ============================================

SELECT
    p.id_profesional,
    u.nombre || ' ' || u.apellidos AS nombre_completo,
    p.especialidad,
    p.numero_colegiado,
    o.nombre_organizacion
FROM profesional p
JOIN usuario u ON p.id_usuario = u.id_usuario
JOIN organizacion o ON u.organizacion_id = o.id_organizacion
WHERE p.especialidad ILIKE '%psicología%'
ORDER BY o.nombre_organizacion, u.nombre;