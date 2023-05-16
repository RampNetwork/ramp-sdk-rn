package network.ramp.reactnativesdk

import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.bridge.ReadableType
import com.facebook.react.bridge.WritableMap
import network.ramp.reactnativesdk.ArrayUtil.toArray
import network.ramp.reactnativesdk.ArrayUtil.toJSONArray
import network.ramp.reactnativesdk.ArrayUtil.toWritableArray
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject


object MapUtil {
    @Throws(JSONException::class)
    fun toJSONObject(readableMap: ReadableMap): JSONObject {
        val jsonObject = JSONObject()
        val iterator = readableMap.keySetIterator()
        while (iterator.hasNextKey()) {
            val key = iterator.nextKey()
            readableMap.getType(key)?.let {
                when (it) {
                    ReadableType.Null -> jsonObject.put(key, null)
                    ReadableType.Boolean -> jsonObject.put(key, readableMap.getBoolean(key))
                    ReadableType.Number -> jsonObject.put(key, readableMap.getDouble(key))
                    ReadableType.String -> jsonObject.put(key, readableMap.getString(key))
                    ReadableType.Map -> readableMap.getMap(key)?.let { jsonObject.put(key, toJSONObject(it)) }
                    ReadableType.Array -> readableMap.getArray(key)?.let { jsonObject.put(key, toJSONArray(it)) }
                    else -> {}
                }
            }
        }
        return jsonObject
    }

    @Throws(JSONException::class)
    fun toMap(jsonObject: JSONObject): Map<String, Any> {
        val map: MutableMap<String, Any> = HashMap()
        val iterator = jsonObject.keys()
        while (iterator.hasNext()) {
            val key = iterator.next()
            var value = jsonObject[key]
            if (value is JSONObject) {
                value = toMap(value)
            }
            if (value is JSONArray) {
                value = toArray(value)
            }
            map[key] = value
        }
        return map
    }

    fun toMap(readableMap: ReadableMap): Map<String, Any?> {
        val map: MutableMap<String, Any?> = HashMap()
        val iterator = readableMap.keySetIterator()
        while (iterator.hasNextKey()) {
            val key = iterator.nextKey()
            readableMap.getType(key)?.let {
                when (it) {
                    ReadableType.Null -> map[key] = null
                    ReadableType.Boolean -> map[key] = readableMap.getBoolean(key)
                    ReadableType.Number -> map[key] = readableMap.getDouble(key)
                    ReadableType.String -> map[key] = readableMap.getString(key)
                    ReadableType.Map -> readableMap.getMap(key)?.let { map[key] = toMap(it) }
                    ReadableType.Array -> readableMap.getArray(key)?.let { map[key] = toArray(it) }
                }
            }
        }
        return map
    }

    fun toWritableMap(map: MutableMap<String?, Any?>?): WritableMap {
        val writableMap = Arguments.createMap()
        val iterator: MutableIterator<*>? = map?.entries?.iterator()
        while (iterator?.hasNext() == true) {
            val (key, value) = iterator.next() as Map.Entry<*, *>
            if (value == null) {
                writableMap.putNull(key as String)
            } else if (value is Boolean) {
                writableMap.putBoolean(key as String, (value as Boolean?)!!)
            } else if (value is Double) {
                writableMap.putDouble(key as String, (value as Double?)!!)
            } else if (value is Int) {
                writableMap.putInt(key as String, (value as Int?)!!)
            } else if (value is String) {
                writableMap.putString(key as String, value as String?)
            } else if (value is Map<*, *>) {
                writableMap.putMap(
                    key as String,
                    toWritableMap(value as MutableMap<String?, Any?>)
                )
            } else if (value.javaClass != null && value.javaClass.isArray) {
                writableMap.putArray(
                    key as String,
                    toWritableArray(
                        (value as Array<Any?>?)!!
                    )
                )
            }
            iterator.remove()
        }
        return writableMap
    }
}
