package com.example.apitienda

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.ContextParams
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.*
import androidx.core.graphics.drawable.toBitmap
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.apitienda.modelo.Categoria
import com.example.apitienda.modelo.Producto
import com.squareup.picasso.Picasso
import org.json.JSONArray
import org.json.JSONException
import java.io.ByteArrayOutputStream

class MainActivity : AppCompatActivity() {
    lateinit var txtCodigo:EditText
    lateinit var txtNombre:EditText
    lateinit var txtPrecio: EditText
    lateinit var cbCategoria: Spinner
    lateinit var btnAgregar:Button
    lateinit var btnConsultar:Button
    lateinit var btnEliminar: Button
    lateinit var btnActualizar: Button
    lateinit var btnListar:Button
    lateinit var btnCargarFoto:Button
    lateinit var ivFoto:ImageView
    private lateinit var uriFoto: Uri
    private lateinit var bitmap: Bitmap
    private lateinit var listaCategorias:MutableList<Categoria> /* MutableList es una interfaz en Kotlin que define una colección de elementos que se pueden modificar. La parte <Categoria> indicamos que estás creando una lista de objetos de tipo Categoria.*/
    private lateinit var listaProductos:MutableList<Producto>
    private var  fotoBase64: String=""
    private var bandera: Boolean = false
    private val PICK_IMAGE = 1
    private val STORAGE_PERMISSION_CODE = 101
    private var idCategoria:Int=0
    private var idProducto:Int=0
    private var Codigo:Int=0
    private var urlBase:String = "http://Juanito21.pythonanywhere.com/" /*Est va aca?*/
    //private var urlBase:String = "http://mariadelmar.pythonanywhere.com/" /*Est va aca?*/


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        txtCodigo = findViewById(R.id.txtCodigo)
        txtNombre = findViewById(R.id.txtNombre)
        txtPrecio = findViewById(R.id.txtPrecio)
        btnAgregar = findViewById(R.id.btnAgregar)
        btnConsultar = findViewById(R.id.btnConsultar)
        btnEliminar = findViewById(R.id.btnEliminar)
        btnListar = findViewById(R.id.btnListar)
        btnActualizar = findViewById(R.id.btnActualizar)
        ivFoto = findViewById(R.id.ivFoto)
        btnCargarFoto = findViewById(R.id.btnCargarFoto)
        cbCategoria = findViewById(R.id.CbCategoria)
        listaCategorias = mutableListOf<Categoria>()
        listaProductos = mutableListOf<Producto>()


        btnAgregar.setOnClickListener{ agregar() }
        btnConsultar.setOnClickListener{ consultar() }
        btnEliminar.setOnClickListener{ validarBorrar() }
        btnListar.setOnClickListener{ listar() }
        btnActualizar.setOnClickListener{ actualizar() }
        btnCargarFoto.setOnClickListener { abrirGaleria() }


        /*Se llama a la siguiente funcion para consumir la api que nos
         retorna las categorias y las guarda en la listaCategorias*/
        obtenerCategorias()

        cbCategoria.onItemSelectedListener = object:
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, posicion: Int, p3: Long){
                idCategoria = listaCategorias[posicion].id
                Toast.makeText(this@MainActivity, "seleccionado", Toast.LENGTH_LONG).show()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }


        /*Creando un adaptador donde se van a capturar los datos de la listaCategorias - dichi
        adaptador se asocia al control visual de tipo spinner que es como un combobox*/
    val adaptador = ArrayAdapter<Categoria>(this, android.R.layout.simple_spinner_dropdown_item,listaCategorias)
    adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
    cbCategoria.adapter=adaptador

}


private fun obtenerCategorias(){
val url = urlBase + "categoria"
val queue = Volley.newRequestQueue(this)
val jsonCategorias = JsonArrayRequest(Request.Method.GET,url,null,
    Response.Listener<JSONArray>() { response ->
        try{
            val jsonArray = response
            for(i in 0 until jsonArray.length() ){
                val jsonObject = jsonArray.getJSONObject(i)
                val id = jsonObject.getString("id")
                val nombre = jsonObject.getString("catNombre")
                var categoria = Categoria(id.toInt(), nombre)
                listaCategorias.add(categoria)
            }
            //crear el adaptador y asociarle la lista de las categorias
            val adaptador = ArrayAdapter<Categoria>(this,
                android.R.layout.simple_spinner_item, listaCategorias)
            adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            cbCategoria.adapter = adaptador
        }catch (e: JSONException){
            e.printStackTrace()
        }
    },Response.ErrorListener{ error ->
        Toast.makeText(this, error.message, Toast.LENGTH_LONG).show()
        Log.e("Error: ", error.toString())

    })
queue.add(jsonCategorias)
}

private fun agregar(){
val url = urlBase + "producto"
"""val url = """""
val queue = Volley.newRequestQueue(this)
val progresBar = ProgressDialog.show(this, "Enviando Datos...", "Espere por favor")
val resultadoPost = object : StringRequest(
    Request.Method.POST,url,
    Response.Listener<String>{ response ->
        progresBar.dismiss()
        Toast.makeText(this, "producto agregado correctamente", Toast.LENGTH_LONG).show()
        limpiar()
    }, Response.ErrorListener{ error ->
        progresBar.dismiss()
        Toast.makeText(this, "Error${error.message}", Toast.LENGTH_LONG).show()
    }){
    override fun getParams(): MutableMap<String, String>{
        val parametros = HashMap<String, String>()
        val foto = bitmapToString(bitmap)
        parametros.put("proCodigo", txtCodigo.text.toString())
        parametros.put("proNombre", txtNombre.text.toString())
        parametros.put("proPrecio", txtPrecio.text.toString())
        parametros.put("proCategoria", idCategoria.toString())
        parametros.put("proFoto", foto)
        return parametros
    }
}
queue.add(resultadoPost)
}

private fun consultar(){
    val id = txtCodigo.text.toString()
    val url = urlBase + "producto/$id"
    val queue = Volley.newRequestQueue(this)
    val jsonObjectRequest = JsonObjectRequest(
        com.android.volley.Request.Method.GET,url,null,
        Response.Listener { response ->
            txtCodigo.setText(response.getString("proCodigo"))
            txtNombre.setText(response.getString("proNombre"))
            txtPrecio.setText(response.getString("proPrecio"))
            idProducto = response.getString("id").toInt()
            idCategoria = response.getInt("proCategoria")
            //cbCategoria.setSelection(idCategoria)
            Codigo=response.getInt("proCodigo")

            var pos=0
            for (cat in listaCategorias){
                if (idCategoria==cat.id){
                    cbCategoria.setSelection(pos)
                    break
                }
                pos++
            }
            val imagenUrl = response.getString("proFoto")
            val imageViewProducto = findViewById<ImageView>(R.id.ivFoto)
            Picasso.get().load(imagenUrl).into(imageViewProducto)

        }, Response.ErrorListener { error ->
            Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show()
            Log.e("Error: ",error.toString())
        }
    )
    queue.add(jsonObjectRequest)
}
private fun actualizar(){
    val url = urlBase + "producto/$Codigo"
    val queue = Volley.newRequestQueue(this)
    val resultadoPost = object : StringRequest(
        Method.PUT,url,
        Response.Listener<String>{ response ->
            Toast.makeText(this, "producto actualizado correctamente", Toast.LENGTH_LONG).show()
            limpiar()

    }, Response.ErrorListener{ error ->
        Toast.makeText(this,
            "Error${error.message}", Toast.LENGTH_LONG).show()
    })
  {
    override fun getParams(): MutableMap<String, String>{
            val parametros = HashMap<String, String>()
            parametros.put("proCodigo", txtCodigo.text.toString())
            parametros.put("proNombre", txtNombre.text.toString())
            parametros.put("proPrecio", txtPrecio.text.toString())
            parametros.put("proCategoria", idCategoria.toString())
            return parametros
    }
  }
    queue.add(resultadoPost)
}

private fun validarBorrar(){
   val alertDialog: AlertDialog? = this?.let {
     val builder = AlertDialog.Builder(it)
     builder.apply{
        setPositiveButton("OK",
            DialogInterface.OnClickListener { dialog, id ->
                borrar()//llamado funcion borrar
                dialog.dismiss()
            })
        setNegativeButton("Cancelar",
            DialogInterface.OnClickListener{ dialog, id ->
                dialog.dismiss()

            })
    }
    builder?.setMessage("Esta seguro de eliminar el Producto?")
    builder?.setMessage("Eliminar Producto")
    //Create the AlertDialog
    builder.create()
}
alertDialog?.show()

}

private fun borrar(){
    val url = urlBase + "producto/$Codigo"
    """val url = """""
    val queue = Volley.newRequestQueue(this)
    val resultadoPost = object : StringRequest(
         Method.DELETE,url,
         Response.Listener{response ->
              Toast.makeText(this, "Producto Elimminado",
                  Toast.LENGTH_LONG).show()
              limpiar()
            },
        Response.ErrorListener{ error ->
            Toast.makeText(this, "Error al eliminar el porducto$error",
                Toast.LENGTH_LONG).show()
        }
    ){
    }
    queue.add(resultadoPost)
}

private fun limpiar() {
    txtCodigo.text.clear()
    txtNombre.text.clear()
    txtPrecio.text.clear()
    ivFoto.setImageDrawable(null)

}

private fun listar(){
    val intent = Intent(this, MainActivityListarProductos::class.java)
    startActivity(intent)
    }

private fun abrirGaleria(){
    val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
    startActivityForResult(intent, PICK_IMAGE)
}

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null){
            val imageUri: Uri? = data.data
            ivFoto.setImageURI(imageUri)
            ivFoto.invalidate()
            val drawable = ivFoto.drawable
            bitmap = drawable.toBitmap()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION_CODE){
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                abrirGaleria()
            }else{
                Toast.makeText(this,"No tiene permisos", Toast.LENGTH_LONG).show()
            }
        }
    }
    fun bitmapToString(bitmap: Bitmap): String{
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG,100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return  Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    fun stringToBitmap(encodedString: String): Bitmap{
        val decodedBytes = Base64.decode(encodedString, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedBytes,0, decodedBytes.size)
    }

}