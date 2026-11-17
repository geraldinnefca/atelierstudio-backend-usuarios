package com.atelierstudio.backend_usuarios.repository;

import com.atelierstudio.backend_usuarios.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByCorreo(String correo);

    List<Usuario> findByRol(String rol);

    boolean existsByCorreo(String correo);

    List<Usuario> findByNombreContainingIgnoreCase(String nombre);

    List<Usuario> findByApellidosContainingIgnoreCase(String apellidos);

    List<Usuario> findByNombreContainingIgnoreCaseOrApellidosContainingIgnoreCaseOrCorreoContainingIgnoreCase(
            String nombre, String apellidos, String correo);
}
