package com.example.apitienda

import android.os.Bundle
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.example.apitienda.modelo.Producto
import org.json.JSONException

class MainActivityListarProductos : AppCompatActivity() {
    private lateinit var listaProductos:MutableList<Producto>
    private lateinit var listViewProductos: ListView
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_listar_productos)
        listaProductos = mutableListOf()
        listViewProductos = findViewById(R.id.listaProductos)
        obtenerProductos()
    }

    private fun obtenerProductos(){
       //
        val url = "http://Juanito21.pythonanywhere.com/producto"
        val queue = Volley.newRequestQueue(this)
        val jsonCategorias = JsonArrayRequest(
            Request.Method.GET,url,null,
            { response ->
                try{
                    val jsonArray = response
                    for(i in 0 until jsonArray.length() ){
                        val jsonObject = response.getJSONObject(i)
                        val id = jsonObject.getInt("id")
                        val codigo = jsonObject.getInt("proCodigo")
                        val nombre = jsonObject.getString("proNombre")
                        val precio = jsonObject.getInt("proPrecio")
                        val categoria = jsonObject.getInt("proCategoria")
                        val urlImagen = jsonObject.getString("proFoto")
                        val producto = Producto(id, codigo, nombre, precio, categoria, urlImagen)
                        listaProductos.add(producto)

                    }
                    //crear el adaptador y asociarle la lista de las categorias
                    val adaptador = Adaptador(this,
                        R.layout.layoutproducto, listaProductos)
                    listViewProductos.adapter = adaptador
                }catch (e: JSONException){
                    e.printStackTrace()
                }
            },{ error ->
                Toast.makeText(this, error.message, Toast.LENGTH_LONG).show()
            })
        queue.add(jsonCategorias)
    }
}