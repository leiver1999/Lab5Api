package cr.ac.una.lab4.dao

import cr.ac.una.lab4.entities.Movimiento
import cr.ac.una.lab4.entities.Movimientos
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface MovimientoDAO {

    //endpoints
    @GET("movimientos")
    suspend fun getItems(): Movimientos

    @GET("movimientos/{uuid}")
    suspend fun getItem(@Path("uuid") uuid: String): Movimiento

    @POST("movimientos")
    suspend fun createItem(@Body movimientos: List<Movimiento>): Movimientos

    @PUT("movimientos/{uuid}")
    suspend fun updateItem(
        @Path("uuid") uuid: String,
        @Body item: Movimiento
    ): Movimiento //TODO ojo

    @DELETE("movimientos/{uuid}")
    suspend fun deleteItem(@Path("uuid") uuid: String)

}