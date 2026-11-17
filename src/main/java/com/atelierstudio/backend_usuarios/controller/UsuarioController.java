package com.atelierstudio.backend_usuarios.controller;

import com.atelierstudio.backend_usuarios.dto.UsuarioRequestDTO;
import com.atelierstudio.backend_usuarios.dto.UsuarioResponseDTO;
import com.atelierstudio.backend_usuarios.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
@Tag(name = "Usuarios", description = "Usuarios del administrador React: listado, alta, edición y detalle")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    private void agregarEnlacesHipermedia(UsuarioResponseDTO dto) {
        if (dto != null) {
            dto.add(linkTo(methodOn(UsuarioController.class).obtenerPorId(dto.getId())).withSelfRel());
            dto.add(linkTo(methodOn(UsuarioController.class).actualizar(dto.getId(), null)).withRel("actualizar"));
            dto.add(linkTo(methodOn(UsuarioController.class).eliminar(dto.getId())).withRel("eliminar"));
            dto.add(linkTo(methodOn(UsuarioController.class).obtenerTodos()).withRel("lista-completa"));
        }
    }

    @GetMapping
    @Operation(summary = "Obtener todos los usuarios", description = "Lista que consume UsuariosAdmin.jsx.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de usuarios recuperada exitosamente.",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = UsuarioResponseDTO.class))))
    })
    public ResponseEntity<List<UsuarioResponseDTO>> obtenerTodos() {
        List<UsuarioResponseDTO> usuarios = usuarioService.obtenerTodos();
        usuarios.forEach(this::agregarEnlacesHipermedia);
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener usuario por ID", description = "DetalleUsuario.jsx y EditarUsuario.jsx.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UsuarioResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado en la base de datos.")
    })
    public ResponseEntity<UsuarioResponseDTO> obtenerPorId(
            @Parameter(description = "ID del usuario.", example = "1")
            @PathVariable Long id) {
        UsuarioResponseDTO dto = usuarioService.obtenerPorId(id);
        agregarEnlacesHipermedia(dto);
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    @Operation(summary = "Crea un nuevo usuario", description = "NuevoUsuario.jsx. Exige contraseña de 8 a 10 caracteres.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuario creado de forma exitosa.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UsuarioResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Error de validación en los datos de entrada."),
            @ApiResponse(responseCode = "409", description = "Ya existe un usuario con ese correo.")
    })
    public ResponseEntity<UsuarioResponseDTO> crear(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos del formulario de nuevo usuario.", required = true)
            @Valid @RequestBody UsuarioRequestDTO dto) {
        UsuarioResponseDTO creado = usuarioService.guardar(dto);
        agregarEnlacesHipermedia(creado);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un usuario existente", description = "EditarUsuario.jsx. Si contrasena viene vacía, se conserva la actual.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario actualizado exitosamente.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UsuarioResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos."),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado.")
    })
    public ResponseEntity<UsuarioResponseDTO> actualizar(
            @Parameter(description = "ID del usuario a modificar.", required = true, example = "1")
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Nuevos datos para el usuario.", required = true)
            @Valid @RequestBody UsuarioRequestDTO dto) {
        UsuarioResponseDTO actualizado = usuarioService.actualizar(id, dto);
        agregarEnlacesHipermedia(actualizado);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un usuario", description = "Acción eliminar de UsuariosAdmin.jsx.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Usuario eliminado de forma exitosa."),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado.")
    })
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "ID del usuario que se desea eliminar.", required = true, example = "2")
            @PathVariable Long id) {
        usuarioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/correo/{correo}")
    @Operation(summary = "Buscar usuario por correo", description = "Correo exacto, el mismo campo del formulario.")
    public ResponseEntity<UsuarioResponseDTO> obtenerPorCorreo(
            @Parameter(description = "Correo del usuario.", required = true, example = "camila.fernandez@gmail.com")
            @PathVariable String correo) {
        UsuarioResponseDTO dto = usuarioService.obtenerPorCorreo(correo);
        agregarEnlacesHipermedia(dto);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/rol/{rol}")
    @Operation(summary = "Listar usuarios por rol", description = "Cliente, Vendedor o Administrador.")
    public ResponseEntity<List<UsuarioResponseDTO>> obtenerPorRol(
            @Parameter(description = "Rol del select.", required = true, example = "Vendedor")
            @PathVariable String rol) {
        List<UsuarioResponseDTO> usuarios = usuarioService.obtenerPorRol(rol);
        usuarios.forEach(this::agregarEnlacesHipermedia);
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/nombre/{nombre}")
    @Operation(summary = "Buscar usuarios por nombre")
    public ResponseEntity<List<UsuarioResponseDTO>> buscarPorNombre(
            @Parameter(description = "Texto o parte del nombre.", required = true, example = "Daniela")
            @PathVariable String nombre) {
        List<UsuarioResponseDTO> usuarios = usuarioService.buscarPorNombre(nombre);
        usuarios.forEach(this::agregarEnlacesHipermedia);
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/apellidos/{apellidos}")
    @Operation(summary = "Buscar usuarios por apellidos")
    public ResponseEntity<List<UsuarioResponseDTO>> buscarPorApellidos(
            @Parameter(description = "Texto o parte de los apellidos.", required = true, example = "Muñoz")
            @PathVariable String apellidos) {
        List<UsuarioResponseDTO> usuarios = usuarioService.buscarPorApellidos(apellidos);
        usuarios.forEach(this::agregarEnlacesHipermedia);
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/buscar")
    @Operation(summary = "Buscador del listado", description = "texto busca en nombre, apellidos y correo, como el input de UsuariosAdmin.jsx.")
    public ResponseEntity<List<UsuarioResponseDTO>> buscarUsuarios(
            @RequestParam(required = false) String rol,
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String apellidos,
            @RequestParam(required = false) String correo,
            @RequestParam(required = false) String texto) {
        List<UsuarioResponseDTO> usuarios = usuarioService.filtrarUsuarios(rol, nombre, apellidos, correo, texto);
        usuarios.forEach(this::agregarEnlacesHipermedia);
        return ResponseEntity.ok(usuarios);
    }
}
