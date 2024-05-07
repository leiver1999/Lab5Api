package cr.ac.una.lab4

import android.util.Log
import cr.ac.una.lab4.entities.Movimiento

class MovimientoController {

    var movimientos: ArrayList<Movimiento> = arrayListOf()


    fun insertMovimiento(movimiento: Movimiento) {
        movimientos.add(movimiento)
    }

    fun getMovimientos(): List<Movimiento> {
        return movimientos
    }

    fun validarDecimales(amount: String): Boolean {

        if (amount.endsWith(".0")) {
            return true
        }

        val amountOfDecimals: Int

        try {
            amountOfDecimals = amount.substring(amount.indexOf('.')).length - 1
        } catch (e: Exception) {
            return true
        }

        return amountOfDecimals <= 2
    }

    fun showMovimientos() {
        for (movimiento in movimientos) {
            Log.d(
                "Movimiento",
                "Monto: ${movimiento.monto} Tipo: ${movimiento.tipo} Fecha: ${movimiento.fecha}"
            )
        }
    }
}