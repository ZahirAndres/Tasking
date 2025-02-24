package mx.edu.utng.tasking

import java.io.Serializable

data class TareaModelClass(
    val id: Int? = null,
    val userId: Int,
    var titulo: String,
    var descripcion: String,
    var estado: String,
    val fechaCreacion: String? = null,
    var fechaFinalizacion : String?
)  : Serializable
