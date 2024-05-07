package cr.ac.una.lab4

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cr.ac.una.lab4.dao.MovimientoDAO
import cr.ac.una.lab4.entities.Movimiento
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MovimientoService : ViewModel() {

    private val _movimientos = MutableLiveData<List<Movimiento>>()
    var movimientos: LiveData<List<Movimiento>> = _movimientos
    lateinit var apiService: MovimientoDAO



    //con api

    fun initService() {
        val client = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor("F7OZowducUlZ6RenRZuoPddkmN0lozg8S3oT-20L3vwlKbRrWA"))
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://crudapi.co.uk/api/v1/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(MovimientoDAO::class.java)
    }

    suspend fun createMovimiento(movimiento: List<Movimiento>) {
        initService()
        apiService.createItem(movimiento)
    }

    suspend fun listMovimiento() {
        initService()
        val lista = apiService.getItems()
        _movimientos.postValue(lista.items)
    }

    suspend fun deleteMovimiento(uuid: String) {
        initService()
        apiService.deleteItem(uuid)
    }

    suspend fun updateMovimiento(uuid: String, movimiento: Movimiento) {
        initService()
        apiService.updateItem(uuid, movimiento)
    }

    //sin api

//    init {
//        _movimientos.value = mutableListOf()
//    }
//
//    fun getMovimientos() {
//        _movimientos.postValue(movimientos as List<Movimiento>)
//    }
//
//    fun addMovimiento(movimiento: Movimiento) {
//        val lista = _movimientos.value?.toMutableList() ?: mutableListOf()
//        lista.add(movimiento)
//        _movimientos.postValue(lista)
//    }
//
//    fun deleteMovimiento(movimiento: Movimiento) {
//        val lista = _movimientos.value?.toMutableList() ?: mutableListOf()
//        lista.remove(movimiento)
//        _movimientos.postValue(lista)
//    }
//
//    fun updateMovimiento(oldMovimiento: Movimiento, newMovimiento: Movimiento) {
//        val currentList = _movimientos.value?.toMutableList() ?: mutableListOf()
//        val index = currentList.indexOfFirst { it == oldMovimiento }
//        if (index != -1) {
//            currentList[index] = newMovimiento
//            _movimientos.value = currentList
//        }
//    }
}
