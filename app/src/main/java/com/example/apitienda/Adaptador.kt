package com.example.apitienda

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.apitienda.modelo.Producto
import com.squareup.picasso.Picasso

class Adaptador : BaseAdapter{
    //Atributos
    lateinit var contexto: Context
    var layout:Int=0
    lateinit var listaProductos: List<Producto>

    constructor(contexto: Context, layout:Int, listaProductos:List<Producto>){
        this.contexto=contexto
        this.layout=layout
        this.listaProductos=listaProductos
    }

    override  fun getCount(): Int{
        return  listaProductos.size
    }

    override fun getItem(posicion: Int): Any {
        return listaProductos[posicion]
    }
    override fun getItemId(posicion: Int): Long {
        return posicion.toLong()
    }

    override fun getView(posicion: Int, vista: View?, parent: ViewGroup?): View {
        var v:View
        var inflater: LayoutInflater = LayoutInflater.from(contexto)
        v = inflater.inflate(R.layout.layoutproducto, null)
        val txtNombre: TextView = v.findViewById(R.id.txtNombreProducto)
        txtNombre.text = listaProductos[posicion].nombre
        val txtCodigo:TextView=v.findViewById(R.id.txtCodigoProducto)
        txtCodigo.text = listaProductos[posicion].codigo.toString()
        val txtPrecio:TextView=v.findViewById(R.id.txtPrecioProducto)
        txtPrecio.text = listaProductos[posicion].precio.toString()
        val imgFoto: ImageView =v.findViewById(R.id.imgFoto)

        Picasso.get()
            .load(listaProductos[posicion].urlImagen)
            .resize(50, 50)
            .placeholder(R.drawable.ic_launcher_background)
            .error(R.drawable.ic_launcher_background)
            .into(imgFoto)
        return v;
    }
}