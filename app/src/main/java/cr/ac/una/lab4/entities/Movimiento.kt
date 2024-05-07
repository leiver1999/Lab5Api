package cr.ac.una.lab4.entities

import java.io.Serializable
import java.util.Date

data class Movimiento(
    var _uuid: String?,
    var monto: Double,
    var tipo: String,
    var fecha: Date,
    //var fotoPath: String? = null,
    var imagen64: String? = null
) : Serializable
