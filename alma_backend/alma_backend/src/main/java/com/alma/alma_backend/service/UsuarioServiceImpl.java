package com.alma.alma_backend.service;

import com.alma.alma_backend.entity.TipoUsuario;
import com.alma.alma_backend.entity.Usuario;
import com.alma.alma_backend.exceptions.ResourceNotFoundException;
import com.alma.alma_backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Implementación de la lógica de negocio para los Usuarios.
 */
@Service
public class UsuarioServiceImpl implements UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public Usuario save(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    @Override
    public Optional<Usuario> findById(Integer id) {
        return usuarioRepository.findById(id);
    }

    @Override
    public Optional<Usuario> findByEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    @Override
    public boolean existsByEmail(String email) {
        return usuarioRepository.existsByEmail(email);
    }

    @Override
    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }

    @Override
    public List<Usuario> findByOrganizacionId(Integer organizacionId) {
        return usuarioRepository.findByOrganizacionIdOrganizacion(organizacionId);
    }

    @Override
    public List<Usuario> findByTipoUsuario(TipoUsuario tipoUsuario) {
        return usuarioRepository.findByTipoUsuario(tipoUsuario);
    }

    @Override
    public void deleteById(Integer id) {
        usuarioRepository.deleteById(id);
    }

    @Override
    public Usuario updateUser(Integer id, Usuario usuarioDetails) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));

        // Actualizar solo los campos que se proporcionan y no son nulos
        if (usuarioDetails.getNombre() != null && !usuarioDetails.getNombre().isEmpty()) {
            usuario.setNombre(usuarioDetails.getNombre());
        }
        if (usuarioDetails.getApellidos() != null) {
            usuario.setApellidos(usuarioDetails.getApellidos());
        }
        if (usuarioDetails.getEmail() != null && !usuarioDetails.getEmail().isEmpty()) {
            usuario.setEmail(usuarioDetails.getEmail());
        }
        if (usuarioDetails.getActivo() != null) {
            usuario.setActivo(usuarioDetails.getActivo());
        }

        return usuarioRepository.save(usuario);
    }
}
