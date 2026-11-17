package com.atelierstudio.backend_usuarios.config;

import com.atelierstudio.backend_usuarios.model.Usuario;
import com.atelierstudio.backend_usuarios.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    private Usuario crearUsuario(String nombre, String apellidos, String correo, String contrasena, String rol) {
        Usuario usuario = new Usuario();
        usuario.setNombre(nombre);
        usuario.setApellidos(apellidos);
        usuario.setCorreo(correo);
        usuario.setContrasena(passwordEncoder.encode(contrasena));
        usuario.setRol(rol);
        return usuario;
    }

    @Override
    public void run(String... args) {
        if (usuarioRepository.count() > 0) {
            log.info("Base de datos de usuarios ya contiene datos. Omitiendo inicialización.");
            return;
        }

        log.info("Cargando los usuarios iniciales de UsuariosAdmin.jsx.");
        usuarioRepository.saveAll(List.of(
                crearUsuario("Daniela", "Muñoz", "daniela.munoz@admin.cl", "holiholi", "Administrador"),
                crearUsuario("Camila", "Fernández", "camila.fernandez@gmail.com", "nanonano", "Cliente"),
                crearUsuario("Juan", "Alarcón", "juan.alarcon@gmail.com", "juan01", "Vendedor"),
                crearUsuario("Ana", "Correa", "ana.correa@gmail.com", "ana02", "Vendedor"),
                crearUsuario("Matias", "Gutierrez", "matias.gutierrez@gmail.com", "matias03", "Cliente"),
                crearUsuario("Diego", "Roa", "diego.roa@admin.cl", "diego04", "Administrador")
        ));
        log.info("Se cargaron 6 usuarios.");
    }
}
