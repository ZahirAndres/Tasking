package mx.edu.utng.tasking

data class UsuarioModelClass(
    val userId: Int? = null,  // ID autogenerado
    val userName: String,     // Nombre de usuario
    val image : String?,
    val userEmail: String,    // Correo electrónico
    val password: String      // Contraseña del usuario
)
