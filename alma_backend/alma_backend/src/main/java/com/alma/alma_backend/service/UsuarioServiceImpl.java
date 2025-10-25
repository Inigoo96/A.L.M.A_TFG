package com.alma.alma_backend.service;

import com.alma.alma_backend.dto.UsuarioUpdateRequestDTO;
import com.alma.alma_backend.mapper.UsuarioMapper;
import com.alma.alma_backend.entity.TipoUsuario;
import com.alma.alma_backend.entity.Usuario;
import com.alma.alma_backend.exceptions.ResourceNotFoundException;
import com.alma.alma_backend.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Implementación de la lógica de negocio para los Usuarios.
 */
@Service
public class UsuarioServiceImpl extends BaseService<Usuario, Integer> implements UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository) {
        super(usuarioRepository);
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public Usuario save(Usuario usuario) {
        return super.save(usuario);
    }

    @Override
    public Optional<Usuario> findById(Integer id) {
        return super.findById(id);
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
        return super.findAll();
    }

    @Override
    public List<Usuario> findByOrganizacionId(Integer organizacionId) {
        // <-- BUG CORREGIDO: Se llama al método correcto del repositorio.
        return usuarioRepository.findByOrganizacion_Id(organizacionId);
    }

    @Override
    public List<Usuario> findByTipoUsuario(TipoUsuario tipoUsuario) {
        return usuarioRepository.findByTipoUsuario(tipoUsuario);
    }

    @Override
    public void deleteById(Integer id) {
        super.deleteById(id);
    }

    @Override
    public Usuario updateUser(Integer id, UsuarioUpdateRequestDTO usuarioDetails) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));

        UsuarioMapper.updateEntity(usuario, usuarioDetails);

        return usuarioRepository.save(usuario);
    }
}
