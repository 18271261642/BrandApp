package com.isport.brandapp.util

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Created by Admin
 *Date 2022/5/11
 */
open class JSONUtils {

    /**
     * fromJson2List
     */
    inline fun <reified T> fromJson2List(json: String) = fromJson<List<T>>(json)

    /**
     * fromJson
     */
    inline fun <reified T> fromJson(json: String): T? {
        return try {
            val type = object : TypeToken<T>() {}.type
            return Gson().fromJson(json, type)
        } catch (e: Exception) {
            println("try exception,${e.message}")
            null
        }
    }

}