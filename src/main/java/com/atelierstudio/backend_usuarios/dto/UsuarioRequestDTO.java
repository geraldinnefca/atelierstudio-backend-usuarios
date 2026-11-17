package com.atelierstudio.backend_usuarios.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "JSON de NuevoUsuario.jsx y EditarUsuario.jsx. La contraseña solo es obligatoria al crear.")
public class UsuarioRequestDTO {

    @Schema(description = "Nombre.", example = "Daniela")
    @NotBlank(message = "Este campo es obligatorio.")
    @Size(max = 100, message = "No puede superar los 100 caracteres.")
    private String nombre;

    @Schema(description = "Apellidos.", example = "Muñoz")
    @NotBlank(message = "Este campo es obligatorio.")
    @Size(max = 100, message = "No puede superar los 100 caracteres.")
    private String apellidos;

    @Schema(description = "Correo único.", example = "daniela.munoz@admin.cl")
    @NotBlank(message = "El correo es obligatorio.")
    @Email(message = "Ingresa un correo válido.")
    private String correo;

    @Schema(description = "Contraseña en texto. Obligatoria al crear (8 a 10 caracteres). En edición se omite y no se cambia.", example = "holiholi")
    @Size(max = 10, message = "La contraseña debe tener entre 8 y 10 caracteres.")
    private String contrasena;

    @Schema(description = "Rol del select de React.", example = "Administrador", allowableValues = {"Cliente", "Vendedor", "Administrador"})
    @NotBlank(message = "Selecciona un rol válido.")
    @Pattern(regexp = "Cliente|Vendedor|Administrador", message = "Selecciona un rol válido.")
    private String rol;
}
