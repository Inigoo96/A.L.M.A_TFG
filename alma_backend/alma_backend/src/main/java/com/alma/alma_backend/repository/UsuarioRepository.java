package com.alma.alma_backend.repository;

import com.alma.alma_backend.entity.TipoUsuario;
import com.alma.alma_backend.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Usuario.
 * Proporciona métodos de acceso a datos para usuarios.
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    /**
     * Busca un usuario por su email.
     * @param email El email del usuario
     * @return Optional con el usuario si existe
     */
    Optional<Usuario> findByEmail(String email);

    /**
     * Busca un usuario por su DNI.
     * @param dni El DNI del usuario
     * @return Optional con el usuario si existe
     */
    Optional<Usuario> findByDni(String dni);

    /**
     * Verifica si existe un usuario con el email dado.
     * @param email El email a verificar
     * @return true si existe, false si no
     */
    boolean existsByEmail(String email);

    /**
     * Busca usuarios por tipo.
     * @param tipoUsuario El tipo de usuario
     * @return Lista de usuarios del tipo especificado
     */
    List<Usuario> findByTipoUsuario(TipoUsuario tipoUsuario);

    /**
     * Busca todos los usuarios que pertenecen a una organización específica.
     * Spring Data JPA generará automáticamente la consulta SQL optimizada.
     * @param organizacionId El ID de la organización
     * @return Lista de usuarios de esa organización
     */
    List<Usuario> findByOrganizacion_Id(Integer organizacionId);

    /**
     * Alias del método anterior para compatibilidad.
     * @param organizacionId El ID de la organización
     * @return Lista de usuarios de esa organización
     */
    default List<Usuario> findByOrganizacionIdOrganizacion(Integer organizacionId) {
        return findByOrganizacion_Id(organizacionId);
    }
}
