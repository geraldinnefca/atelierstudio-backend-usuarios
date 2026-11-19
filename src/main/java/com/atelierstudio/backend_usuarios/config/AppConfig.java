package com.atelierstudio.backend_usuarios.config;

import com.atelierstudio.backend_usuarios.model.Usuario;
import com.atelierstudio.backend_usuarios.repository.UsuarioRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AppConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(UsuarioRepository repository) {
        return correo -> {
            Usuario usuario = repository.findByCorreo(correo.trim().toLowerCase())
                    .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con correo: " + correo));

            String rolFinal = "ROLE_" + usuario.getRol().toUpperCase().trim().replace("ROLE_", "");

            return new User(
                    usuario.getCorreo(),
                    usuario.getContrasena(),
                    AuthorityUtils.createAuthorityList(rolFinal)
            );
        };
    }
}