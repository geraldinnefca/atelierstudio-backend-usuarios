package com.atelierstudio.backend_usuarios.service;

import com.atelierstudio.backend_usuarios.dto.UsuarioRequestDTO;
import com.atelierstudio.backend_usuarios.dto.UsuarioResponseDTO;
import com.atelierstudio.backend_usuarios.exception.EmailDuplicadoException;
import com.atelierstudio.backend_usuarios.exception.UsuarioNoEncontradoException;
import com.atelierstudio.backend_usuarios.model.Usuario;
import com.atelierstudio.backend_usuarios.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    private UsuarioResponseDTO mapToResponseDTO(Usuario usuario) {
        return new UsuarioResponseDTO(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getApellidos(),
                usuario.getCorreo(),
                usuario.getRol()
        );
    }

    private void normalizar(UsuarioRequestDTO dto) {
        dto.setNombre(dto.getNombre().trim());
        dto.setApellidos(dto.getApellidos().trim());
        dto.setCorreo(dto.getCorreo().trim().toLowerCase());
    }

    private void validarContrasena(String contrasena) {
        if (contrasena == null || contrasena.length() < 8 || contrasena.length() > 10) {
            throw new IllegalArgumentException("La contraseña debe tener entre 8 y 10 caracteres.");
        }
    }

    public List<UsuarioResponseDTO> obtenerTodos() {
        log.info("Buscando la lista completa de usuarios del sistema.");
        return usuarioRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    public UsuarioResponseDTO obtenerPorId(Long id) {
        log.info("Buscando usuario con ID: {}", id);
        return usuarioRepository.findById(id)
                .map(this::mapToResponseDTO)
                .orElseThrow(() -> new UsuarioNoEncontradoException(id));
    }

    @Transactional
    public UsuarioResponseDTO guardar(UsuarioRequestDTO dto) {
        normalizar(dto);
        log.info("Iniciando proceso de guardado para el usuario con correo: {}", dto.getCorreo());
        validarContrasena(dto.getContrasena());

        if (usuarioRepository.existsByCorreo(dto.getCorreo())) {
            log.warn("Intento de registro fallido: El correo {} ya existe", dto.getCorreo());
            throw new EmailDuplicadoException(dto.getCorreo());
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(dto.getNombre());
        usuario.setApellidos(dto.getApellidos());
        usuario.setCorreo(dto.getCorreo());
        usuario.setRol(dto.getRol());
        usuario.setContrasena(passwordEncoder.encode(dto.getContrasena()));

        Usuario guardado = usuarioRepository.save(usuario);
        log.info("Usuario guardado exitosamente con ID: {}", guardado.getId());
        return mapToResponseDTO(guardado);
    }

    @Transactional
    public UsuarioResponseDTO actualizar(Long id, UsuarioRequestDTO dto) {
        normalizar(dto);
        log.info("Iniciando actualización del usuario ID: {}", id);
        return usuarioRepository.findById(id).map(usuarioExistente -> {
            if (!dto.getCorreo().equalsIgnoreCase(usuarioExistente.getCorreo())
                    && usuarioRepository.existsByCorreo(dto.getCorreo())) {
                throw new EmailDuplicadoException(dto.getCorreo());
            }

            usuarioExistente.setNombre(dto.getNombre());
            usuarioExistente.setApellidos(dto.getApellidos());
            usuarioExistente.setCorreo(dto.getCorreo());
            usuarioExistente.setRol(dto.getRol());

            if (dto.getContrasena() != null && !dto.getContrasena().isBlank()) {
                validarContrasena(dto.getContrasena());
                log.info("Encriptando nueva contraseña para el usuario ID: {}", id);
                usuarioExistente.setContrasena(passwordEncoder.encode(dto.getContrasena()));
            }

            return mapToResponseDTO(usuarioRepository.save(usuarioExistente));
        }).orElseThrow(() -> new UsuarioNoEncontradoException(id));
    }

    @Transactional
    public void eliminar(Long id) {
        log.info("Solicitud para eliminar usuario con ID: {}", id);
        if (!usuarioRepository.existsById(id)) {
            throw new UsuarioNoEncontradoException(id);
        }
        usuarioRepository.deleteById(id);
        log.info("Usuario con ID: {} eliminado correctamente de la base de datos", id);
    }

    public UsuarioResponseDTO obtenerPorCorreo(String correo) {
        String normalizado = correo.trim().toLowerCase();
        log.info("Buscando usuario por correo exacto: {}", normalizado);
        return usuarioRepository.findByCorreo(normalizado)
                .map(this::mapToResponseDTO)
                .orElseThrow(() -> new RuntimeException("No se encontró ningún usuario registrado con el correo: " + normalizado + "."));
    }

    public List<UsuarioResponseDTO> obtenerPorRol(String rol) {
        log.info("Filtrando usuarios con rol: {}", rol);
        List<UsuarioResponseDTO> resultados = usuarioRepository.findByRol(rol).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
        if (resultados.isEmpty()) {
            throw new RuntimeException("No se encontraron usuarios registrados con el rol: " + rol + ".");
        }
        return resultados;
    }

    public List<UsuarioResponseDTO> buscarPorNombre(String nombre) {
        log.info("Ejecutando buscador parcial de usuarios por nombre: '{}'", nombre);
        List<UsuarioResponseDTO> resultados = usuarioRepository.findByNombreContainingIgnoreCase(nombre).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
        if (resultados.isEmpty()) {
            throw new RuntimeException("No se encontraron usuarios que coincidan con el nombre: '" + nombre + "'.");
        }
        return resultados;
    }

    public List<UsuarioResponseDTO> buscarPorApellidos(String apellidos) {
        log.info("Ejecutando buscador parcial de usuarios por apellidos: '{}'", apellidos);
        List<UsuarioResponseDTO> resultados = usuarioRepository.findByApellidosContainingIgnoreCase(apellidos).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
        if (resultados.isEmpty()) {
            throw new RuntimeException("No se encontraron usuarios que coincidan con los apellidos: '" + apellidos + "'.");
        }
        return resultados;
    }

    public List<UsuarioResponseDTO> filtrarUsuarios(String rol, String nombre, String apellidos, String correo, String texto) {
        log.info("Filtrando usuarios. Rol: '{}', nombre: '{}', apellidos: '{}', correo: '{}', texto: '{}'",
                rol, nombre, apellidos, correo, texto);

        List<Usuario> usuarios = tieneTexto(texto)
                ? usuarioRepository.findByNombreContainingIgnoreCaseOrApellidosContainingIgnoreCaseOrCorreoContainingIgnoreCase(
                        texto.trim(), texto.trim(), texto.trim())
                : usuarioRepository.findAll();

        return usuarios.stream()
                .filter(usuario -> !tieneTexto(rol) || usuario.getRol().equalsIgnoreCase(rol.trim()))
                .filter(usuario -> !tieneTexto(nombre) || contiene(usuario.getNombre(), nombre))
                .filter(usuario -> !tieneTexto(apellidos) || contiene(usuario.getApellidos(), apellidos))
                .filter(usuario -> !tieneTexto(correo) || contiene(usuario.getCorreo(), correo))
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    private boolean tieneTexto(String valor) {
        return valor != null && !valor.trim().isEmpty();
    }

    private boolean contiene(String campo, String filtro) {
        return campo != null && campo.toLowerCase().contains(filtro.trim().toLowerCase());
    }
}
