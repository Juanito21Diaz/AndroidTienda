package com.example.apitienda.modelo

class Producto constructor(id: Int, codigo: Int, nombre: String, precio: Int, categoria: Int, urlImagen: String) {
    var nombre = nombre
    var id = id
    var codigo = codigo
    var precio = precio
    var categoria = categoria
    var urlImagen = urlImagen

    //sobrescribir metodo toString
    override fun toString(): String{
        return nombre
    }
}