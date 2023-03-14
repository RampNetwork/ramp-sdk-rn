package com.rampnetwork.reactnativesdk

import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.bridge.ReadableType
import com.facebook.react.bridge.WritableArray
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject


object ArrayUtil {
    @Throws(JSONException::class)
    fun toJSONArray(readableArray: ReadableArray): JSONArray {
        val jsonArray = JSONArray()
        for (i in 0 until readableArray.size()) {
            readableArray.getType(i)?.let {
                when (it) {
                    ReadableType.Null -> jsonArray.put(i, null)
                    ReadableType.Boolean -> jsonArray.put(i, readableArray.getBoolean(i))
                    ReadableType.Number -> jsonArray.put(i, readableArray.getDouble(i))
                    ReadableType.String -> jsonArray.put(i, readableArray.getString(i))
                    ReadableType.Map -> jsonArray.put(
                        i,
                        readableArray.getMap(i)?.let { it1 -> MapUtil.toJSONObject(it1) }
                    )
                    ReadableType.Array -> jsonArray.put(
                        i,
                        readableArray.getArray(i)?.let { it1 -> toJSONArray(it1) }
                    )
                }
            }
        }
        return jsonArray
    }

    @Throws(JSONException::class)
    fun toArray(jsonArray: JSONArray): Array<Any?> {
        val array = arrayOfNulls<Any>(jsonArray.length())
        for (i in 0 until jsonArray.length()) {
            var value = jsonArray[i]
            if (value is JSONObject) {
                value = MapUtil.toMap(value)
            }
            if (value is JSONArray) {
                value = toArray(value)
            }
            array[i] = value
        }
        return array
    }

    fun toArray(readableArray: ReadableArray): Array<Any?> {
        val array = arrayOfNulls<Any>(readableArray.size())
        for (i in 0 until readableArray.size()) {
            readableArray.getType(i)?.let {
                when (it) {
                    ReadableType.Null -> array[i] = null
                    ReadableType.Boolean -> array[i] = readableArray.getBoolean(i)
                    ReadableType.Number -> array[i] = readableArray.getDouble(i)
                    ReadableType.String -> array[i] = readableArray.getString(i)
                    ReadableType.Map -> array[i] = readableArray.getMap(i)
                        ?.let { it1 -> MapUtil.toMap(it1) }
                    ReadableType.Array -> array[i] = readableArray.getArray(i)
                        ?.let { it1 -> toArray(it1) }
                }
            }
        }
        return array
    }

    fun toWritableArray(array: Array<Any?>): WritableArray {
        val writableArray = Arguments.createArray()
        for (i in array.indices) {
            val value = array[i]
            if (value == null) {
                writableArray.pushNull()
            }
            if (value is Boolean) {
                writableArray.pushBoolean((value as Boolean?)!!)
            }
            if (value is Double) {
                writableArray.pushDouble((value as Double?)!!)
            }
            if (value is Int) {
                writableArray.pushInt((value as Int?)!!)
            }
            if (value is String) {
                writableArray.pushString(value as String?)
            }
            if (value is Map<*, *>) {
                writableArray.pushMap(MapUtil.toWritableMap(value as MutableMap<String?, Any?>))
            }
            if (value!!.javaClass.isArray) {
                writableArray.pushArray(toWritableArray(value as Array<Any?>))
            }
        }
        return writableArray
    }
}
