package iris.json

import iris.json.serialization.DeserializerFactory
import kotlin.reflect.typeOf

/**
 * @created 26.09.2020
 * @author [Ivan Ivanov](https://vk.com/irisism)
 */
interface JsonItem {
	operator fun get(ind: Int): JsonItem
	operator fun get(key: String): JsonItem

	operator fun set(ind: Int, value: JsonItem): JsonItem
	operator fun set(key: String, value: JsonItem): JsonItem
	operator fun set(ind: Int, value: Any?): JsonItem
	operator fun set(key: String, value: Any?): JsonItem

	fun obj(): Any?
	fun <A: Appendable>appendToJsonString(buffer: A): A
	fun toJsonString(): String

	fun iterable() : Iterable<JsonItem>

	fun asIntOrNull(): Int?

	fun asInt(): Int

	fun asLongOrNull() : Long?

	fun asLong() : Long

	fun asDoubleOrNull() : Double?

	fun asDouble() : Double

	fun asFloatOrNull() : Float?

	fun asFloat() : Float

	fun asBooleanOrNull() : Boolean?

	fun asBoolean() : Boolean

	fun asList(): List<Any?>

	fun <T>asTypedList(): List<T>

	fun asMap(): Map<String, Any?>

	fun asStringOrNull(): String?

	fun asString(): String

	fun find(tree: Array<String>): JsonItem

	fun find(tree: List<String>): JsonItem

	fun isNull(): Boolean

	fun isNotNull() = !isNull()

	fun isPrimitive(): Boolean

	fun isArray(): Boolean

	fun isObject(): Boolean

	fun find(tree: String): JsonItem
}

inline fun<T : JsonItem> JsonItem.cast(): T = this as T

inline fun <reified T>JsonItem.asObject(): T {
	val deserializer = DeserializerFactory.getDeserializer(typeOf<T>())
	return deserializer.deserialize(this)
}