package cr.ac.una.lab4

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import android.Manifest
import android.app.DatePickerDialog
import android.graphics.BitmapFactory
import android.icu.util.Calendar
import android.os.Environment
import android.util.Log
import android.widget.DatePicker
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import cr.ac.una.lab4.entities.Movimiento
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

import android.util.Base64
import java.io.ByteArrayOutputStream


class CameraFragment : Fragment(), DatePickerDialog.OnDateSetListener {

    var movimientoController: MovimientoController = MovimientoController()


    lateinit var captureButton: Button
    lateinit var salirButton: Button
    lateinit var imageView: ImageView
    lateinit var selectDate: TextView
    lateinit var addButton: Button

    //    lateinit var datePicker: DatePicker
    lateinit var spinnerPaymentMethod: Spinner
    lateinit var editTextAmount: TextView

    private var movimiento: Movimiento? = null

//    val datePicker = binding.datePicker
//    val spinnerPaymentMethod = binding.spinnerPaymentMethod
//    val editTextAmount = binding.editTextAmount

    private val calendar = Calendar.getInstance()

    //con string
    //private var imagePath = movimiento?.fotoPath

    //con bitmap
    private var image64 = movimiento?.imagen64

    private val requestCameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()// aqui le sale el usuario el permiso
    ) { isGranted ->
        if (isGranted) {
            dispatchTakePictureIntent()// si se le da permiso se llama a la funcion para tomar la foto
        } else {
            // Permiso denegado, manejar la situación aquí si es necesario
        }
    }

    private val takePictureLauncher =
        registerForActivityResult(// se registra el resultado de la actividad
            ActivityResultContracts.StartActivityForResult()// se le pasa el intent de la camara
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                val imageBitmap = result.data?.extras?.get("data") as? Bitmap

                //imagePath = saveImageToFile(imageBitmap)

//                movimiento?.fotoPath = imagePath

                imageView.setImageBitmap(imageBitmap)

                image64 = bitmapToBase64(imageBitmap!!)


            } else {
                // Manejar el caso en el que no se haya podido capturar la imagen
            }
        }

    private fun saveImageToFile(imageBitmap: Bitmap?): String {
        if (imageBitmap == null) return " "

        val filesDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "Movimiento_${timeStamp}.jpg"

        val imageFile = File(filesDir, fileName)

        if (imageFile.exists()) {
            imageFile.delete()
        }

        FileOutputStream(imageFile).use { outputStream ->
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
        }

        return imageFile.absolutePath
    }


    private val vista: MovimientoService by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { args ->
            movimiento = args.getSerializable("movimiento") as? Movimiento
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_camera, container, false)

        addButton = view.findViewById(R.id.button)
        salirButton = view.findViewById(R.id.button2)
        captureButton = view.findViewById(R.id.captureButton)
        imageView = view.findViewById(R.id.imageView2)
        selectDate = view.findViewById(R.id.textView3)
        spinnerPaymentMethod = view.findViewById(R.id.spinnerPaymentMethod)
        editTextAmount = view.findViewById(R.id.editTextAmount)



        salirButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.home_content, HomeFragment())
                .commit()
        }


        return view

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        vista = ViewModelProvider(requireActivity()).get(movimientoVista::class.java)

        if (movimiento != null) {

            Toast.makeText(context, "Editando", Toast.LENGTH_SHORT).show()

            editTextAmount.text = movimiento!!.monto.toString()

            spinnerPaymentMethod.setSelection(
                resources.getStringArray(R.array.payment_methods).indexOf(movimiento!!.tipo)
            )

            calendar.time = movimiento!!.fecha

            selectDate.text =
                "${calendar.get(Calendar.DAY_OF_MONTH)}/${calendar.get(Calendar.MONTH) + 1}/${
                    calendar.get(Calendar.YEAR)
                }"

            // con string
//            val imageFile = File(movimiento!!.fotoPath!!)
//
//            if (imageFile.exists()) {
//                val imageBitmap = BitmapFactory.decodeFile(imageFile.absolutePath)
//                imageView.setImageBitmap(imageBitmap)
//            } else {
//                Log.e("CameraFragment", "Image file not found at: path")
//            }

            //con bitmap
            GlobalScope.launch(Dispatchers.Main) {
                val imageBitmap = base64ToBitmap(movimiento!!.imagen64!!)
                imageView.setImageBitmap(imageBitmap)
            }






            addButton.text = "Editar"

            addButton.setOnClickListener {

                val movimientoEditado = createMovimiento()

                if (movimientoEditado != null) {
                    //sin api
//                    vista.updateMovimiento(movimiento!!, movimientoEditado)

                    GlobalScope.launch(Dispatchers.Main) {
                        vista.updateMovimiento(movimiento?._uuid!!, movimientoEditado)
                    }

                    parentFragmentManager.beginTransaction()
                        .replace(R.id.home_content, HomeFragment())
                        .commit()

                } else {
                    Toast.makeText(context, "ERROR", Toast.LENGTH_LONG).show()
                }
            }


        } else {

            addButton.setOnClickListener {

                val movimiento = createMovimiento()

                if (movimiento != null) {

//                    vista.addMovimiento(movimiento)

                    var items = ArrayList<Movimiento>()
                    items.add(movimiento)

                    GlobalScope.launch(Dispatchers.Main) {
                        vista.createMovimiento(items)
                    }

                    parentFragmentManager.beginTransaction()
                        .replace(R.id.home_content, HomeFragment())
                        .commit()

                } else {
                    Toast.makeText(context, "ERROR", Toast.LENGTH_LONG).show()
                }
            }

        }

        selectDate.setOnClickListener {
            DatePickerDialog(
                requireContext(),
                this,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

//        captureButton = view.findViewById(R.id.captureButton)
//        // se le asigna el id del imageView
//        imageView = view.findViewById(R.id.imageView2)

        captureButton.setOnClickListener {// se le asigna el listener al boton
            if (checkCameraPermission() && checkWritePermission() && checkReadPermission()) {// se verifica si se tiene permiso
                dispatchTakePictureIntent()// se llama a la funcion para tomar la foto
            } else {
                requestCameraPermission()// se solicita el permiso
                requestWritePermission()
                requestReadPermission()
            }
        }
    }

    private fun checkCameraPermission(): Boolean {// pregunta si la camara tiene permiso y devuelve si o no
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun checkWritePermission(): Boolean {// pregunta si la camara tiene permiso y devuelve si o no
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun checkReadPermission(): Boolean {// pregunta si la camara tiene permiso y devuelve si o no
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }


    private fun requestCameraPermission() {//
        requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)// se lanza la solicitud de permiso de la camara y se le pasa el permiso
    }

    private fun requestWritePermission() {//
        requestCameraPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)// se lanza la solicitud de permiso de la camara y se le pasa el permiso
    }

    private fun requestReadPermission() {//
        requestCameraPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)// se lanza la solicitud de permiso de la camara y se le pasa el permiso
    }

    private fun dispatchTakePictureIntent() {// se lanza la camara para tomar la foto
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->// se crea el intent para tomar la foto
            takePictureIntent.resolveActivity(requireContext().packageManager)?.also {
                takePictureLauncher.launch(takePictureIntent)
            }
        }
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        selectDate.text = "$dayOfMonth/${month + 1}/$year"
//        Toast.makeText(context, "${selectDate.text}", Toast.LENGTH_SHORT).show()
    }

    //funciones con bitmap
    fun bitmapToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    fun base64ToBitmap(base64String: String): Bitmap? {
        val decodedString: ByteArray = Base64.decode(base64String, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
    }




    fun createMovimiento(): Movimiento? {

        try {

            val selectedDate: Date = calendar.time
            val selectedPaymentMethod = spinnerPaymentMethod.selectedItem.toString()
            val amount = editTextAmount.text.toString()
            //con string
            //val fotoPath = imagePath
            //con bitmap
            val image64 = image64

            if (movimientoController.validarDecimales(amount)) {
                Log.i("FormularioFragment", "TODO GUD")

                Log.i("FormularioFragment", "Selected Date: $selectedDate")
                Log.i("FormularioFragment", "Payment Method: $selectedPaymentMethod")
                Log.i("FormularioFragment", "Amount: $amount")

                val move = Movimiento(
                    null,
                    amount.toDouble(),
                    selectedPaymentMethod,
                    selectedDate,
                    //string
                    //fotoPath
                    //bitmap
                    image64
                )

                return move

            } else {
                Log.i("FormularioFragment", "Amount: $amount")
                Log.i("FormularioFragment", "TODO MAL")
                Toast.makeText(context, "Maximo dos decimales", Toast.LENGTH_SHORT).show()
            }

        } catch (e: Exception) {
            Log.i("FormularioFragment", "Error: ${e.message}")
        }



        return null
    }

    companion object {
        fun newInstance(movimiento: Movimiento): CameraFragment {
            val fragment = CameraFragment()
            val args = Bundle().apply {
                putSerializable("movimiento", movimiento)
            }
            fragment.arguments = args
            return fragment
        }
    }

}