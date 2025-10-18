package com.alma.alma_backend.service;

import com.alma.alma_backend.entity.TipoUsuario;
import com.alma.alma_backend.entity.Usuario;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz para la lógica de negocio relacionada con los Usuarios.
 * Define el contrato de las operaciones que se pueden realizar.
 */
public interface UsuarioService {

    Usuario save(Usuario usuario);

    Optional<Usuario> findById(Integer id);

    Optional<Usuario> findByEmail(String email);

    List<Usuario> findAll();

    List<Usuario> findByTipoUsuario(TipoUsuario tipoUsuario);

    void deleteById(Integer id);

    Usuario updateUser(Integer id, Usuario usuarioDetails);
}
