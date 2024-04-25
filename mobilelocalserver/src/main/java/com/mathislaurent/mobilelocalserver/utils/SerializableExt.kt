package com.mathislaurent.mobilelocalserver.utils

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable

fun Serializable.toByteArray(): ByteArray {
    val baos = ByteArrayOutputStream()
    val oos = ObjectOutputStream(baos)
    oos.writeObject(this)
    oos.close()
    return baos.toByteArray()
}

fun ByteArray.toSerializable(): Serializable? {
    val bais = ByteArrayInputStream(this)
    val ois = ObjectInputStream(bais)
    return ois.readObject() as? Serializable
}