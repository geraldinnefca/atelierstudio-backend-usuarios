package com.atelierstudio.backend_usuarios.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

@Data
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "Usuario tal como lo leen UsuariosAdmin, DetalleUsuario y EditarUsuario. No incluye la contraseña.")
public class UsuarioResponseDTO extends RepresentationModel<UsuarioResponseDTO> {

    @Schema(example = "1")
    private Long id;

    @Schema(example = "Daniela")
    private String nombre;

    @Schema(example = "Muñoz")
    private String apellidos;

    @Schema(example = "daniela.munoz@admin.cl")
    private String correo;

    @Schema(example = "Administrador")
    private String rol;

    public UsuarioResponseDTO(Long id, String nombre, String apellidos, String correo, String rol) {
        super();
        this.id = id;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.correo = correo;
        this.rol = rol;
    }

    public Long getId() {
        return this.id;
    }
}
