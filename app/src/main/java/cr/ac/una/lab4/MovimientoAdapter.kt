package cr.ac.una.lab4

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageButton
import android.widget.TextView
import cr.ac.una.lab4.entities.Movimiento


class MovimientoAdapter(
    private val context: Context,
    private var movimientos: List<Movimiento>,
    private var onDeleteClickListener: ((Movimiento) -> Unit),
    private val onEditClickListener: (Movimiento) -> Unit
) : BaseAdapter() {

    fun updateList(newMovimientos: List<Movimiento>) {
        movimientos = newMovimientos
        notifyDataSetChanged()
    }

    override fun getCount(): Int = movimientos.size

    override fun getItem(position: Int): Movimiento = movimientos[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        var view = LayoutInflater.from(context)
            .inflate(R.layout.list_item, parent, false)

        val monto = view.findViewById<TextView>(R.id.monto)
        val tipo = view.findViewById<TextView>(R.id.tipo)
        val fecha = view.findViewById<TextView>(R.id.fecha)

        var movimiento = getItem(position)
        monto.text = movimiento?.monto.toString()
        tipo.text = movimiento?.tipo.toString()
        fecha.text = movimiento?.fecha.toString()

        val deleteButton = view.findViewById<ImageButton>(R.id.borrar)

        deleteButton.setOnClickListener {
            onDeleteClickListener.invoke(movimiento)
        }

        val editButton = view.findViewById<ImageButton>(R.id.editar)

        editButton.setOnClickListener {
            onEditClickListener.invoke(movimiento)
        }

        return view
    }
}
