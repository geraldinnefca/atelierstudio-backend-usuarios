# Backend de usuarios

Estructura: controlador, servicio, repositorio, DTOs, BCrypt, Swagger y HATEOAS.




Base: `http://localhost:8081/api/usuarios`



Alta de ejemplo:

```json
{
  "nombre": "Camila",
  "apellidos": "Fernández",
  "correo": "camila.fernandez@gmail.com",
  "contrasena": "nanonano",
  "rol": "Cliente"
}
```


Swagger: `http://localhost:8081/doc/swagger-ui.html`

## Ejecutar

MySQL en `localhost:3306`
base `usuarios_db`
usuario `root`
contraseña vacía.


./mvnw spring-boot:run



