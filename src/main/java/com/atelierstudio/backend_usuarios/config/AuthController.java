package com.atelierstudio.backend_usuarios.config;

import com.atelierstudio.backend_usuarios.model.Usuario;
import com.atelierstudio.backend_usuarios.repository.UsuarioRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest dto) {
        String correo = dto.getCorreo() == null ? "" : dto.getCorreo().trim().toLowerCase();
        String contrasena = dto.getContrasena() == null ? "" : dto.getContrasena();
        Usuario usuario = usuarioRepository.findByCorreo(correo).orElse(null);

        if (usuario != null && passwordEncoder.matches(contrasena, usuario.getContrasena())) {
            String tokenGenerado = jwtUtil.generarToken(usuario.getCorreo(), usuario.getRol());

            Map<String, Object> usuarioPublico = new LinkedHashMap<>();
            usuarioPublico.put("id", usuario.getId());
            usuarioPublico.put("nombre", usuario.getNombre());
            usuarioPublico.put("apellidos", usuario.getApellidos());
            usuarioPublico.put("correo", usuario.getCorreo());
            usuarioPublico.put("rol", usuario.getRol());

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("token", tokenGenerado);
            response.put("usuario", usuarioPublico);
            return ResponseEntity.ok(response);
        }

        return ResponseEntity.status(401).body(Map.of("MENSAJE", "Correo o contraseña incorrectos"));
    }

    @Data
    public static class LoginRequest {
        private String correo;
        private String contrasena;
    }
}
