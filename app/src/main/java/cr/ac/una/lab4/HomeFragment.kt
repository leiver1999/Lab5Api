package cr.ac.una.lab4

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView
import androidx.fragment.app.activityViewModels
import cr.ac.una.lab4.entities.Movimiento
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 **/
class HomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null

//    private var _binding: FragmentHomeBinding? = null
//    private val binding get() = _binding!!

    private lateinit var movimientos: List<Movimiento>
    private val vista: MovimientoService by activityViewModels()
    private lateinit var adapter: MovimientoAdapter
    private lateinit var listView: ListView


//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        arguments?.let {
//            param1 = it.getString(ARG_PARAM1)
//
//        }
//    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment


        val view = inflater.inflate(R.layout.fragment_home, container, false)

        listView = view.findViewById(R.id.listaMovimientos)

        adapter = MovimientoAdapter(
            requireContext(),
            vista.movimientos.value ?: mutableListOf(), { movimiento ->
//                vista.deleteMovimiento(movimiento)
                GlobalScope.launch(Dispatchers.Main) {
                    movimiento._uuid?.let { vista.deleteMovimiento(it) }
                }
                parentFragmentManager.beginTransaction()
                    .replace(R.id.home_content, HomeFragment())
                    .commit()

            }, { movimiento ->
//                val action = HomeFragmentDirections.actionHomeFragmentToCameraFragment(movimiento)
//                findNavController().navigate(action)

                parentFragmentManager.beginTransaction()
                    .replace(R.id.home_content, CameraFragment.newInstance(movimiento))
                    .commit()
            }//???
        )

        listView.adapter = adapter

        val botonNuevo = view.findViewById<Button>(R.id.botonNuevo)

        botonNuevo.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.home_content, CameraFragment())
                .commit()
        }

        vista.movimientos.observe(viewLifecycleOwner) { elem ->
//            adapter.updateList(movimientos)
            adapter.updateList(elem as ArrayList<Movimiento>)
            movimientos = elem
        }

        GlobalScope.launch(Dispatchers.Main) {
            vista.listMovimiento()
        }

        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        vista = ViewModelProvider(requireActivity()).get(movimientoVista::class.java)
//
//        val listView = binding.listaMovimientos
//
//        movimientos = mutableListOf<Movimiento>()
//
//        adapter = MovimientoAdapter(
//            requireContext(),
//            vista.movimientos.value ?: mutableListOf(), { movimiento ->
//                vista.deleteMovimiento(movimiento)
//            }, { movimiento ->
//                val action = HomeFragmentDirections.actionHomeFragmentToCameraFragment(movimiento)
//                findNavController().navigate(action)
//            }
//        )
//
//        listView.adapter = adapter

        vista.movimientos.observe(viewLifecycleOwner) { elem ->
//            adapter.updateList(movimientos)
            adapter.updateList(elem as ArrayList<Movimiento>)
            movimientos = elem
        }

        //TODO subirlo a oncreate
        GlobalScope.launch(Dispatchers.Main) {
            vista.listMovimiento()
        }

//        binding.botonNuevo.setOnClickListener{
//            findNavController().navigate(R.id.action_HomeFragment_to_CameraFragment)
//        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
//        _binding = null
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.

         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
//        @JvmStatic
//        fun newInstance(param1: String) =
//            HomeFragment().apply {
//                arguments = Bundle().apply {
//                    putString(ARG_PARAM1, param1)
//                }
//            }
    }
}