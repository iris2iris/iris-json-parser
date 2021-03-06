package iris.json.plain

import iris.json.JsonValue
import iris.json.Util
import iris.sequence.*

/**
 * @created 14.04.2020
 * @author [Ivan Ivanov](https://vk.com/irisism)
 */
class IrisJsonValue(private val data: IrisSequence, private val valueType: Util.ValueType) : IrisJsonItem(), JsonValue {
	override fun toString(): String {
		return data.toString()
	}

	override fun <A : Appendable> appendToJsonString(buffer: A): A {
		data.joinTo(buffer)
		return buffer
	}

	override fun get(ind: Int): IrisJsonItem {
		return IrisJsonNull.Null
	}

	override fun get(key: String): IrisJsonItem {
		return IrisJsonNull.Null
	}

	private fun init(): Any? {
		val s = data

		return when (valueType) {
			Util.ValueType.Constant -> when (s as CharSequence) {
				"null" -> null
				"true", "1" -> true
				"false", "0" -> false
				else -> s.toString()
			}
			Util.ValueType.Integer -> s.toLong()
			Util.ValueType.Float -> s.toDouble()
			//else -> throw IllegalArgumentException("No argument: $valueType")
		}
	}

	override fun asIntOrNull(): Int? {
		if (done)
			return super.asIntOrNull()
		val res = data.toIntOrNull()
		ready = res
		done = true
		return res
	}

	override fun asInt(): Int {
		if (done)
			return super.asInt()
		val res = data.toInt()
		ready = res
		done = true
		return res
	}

	override fun asLongOrNull(): Long? {
		if (done)
			return super.asLongOrNull()
		val res = data.toLongOrNull()
		ready = res
		done = true
		return res
	}

	override fun asLong(): Long {
		if (done)
			return super.asLong()
		val res = data.toLong()
		ready = res
		done = true
		return res
	}

	override fun asDoubleOrNull(): Double? {
		if (done)
			return super.asDoubleOrNull()
		val res = data.toDoubleOrNull()
		ready = res
		done = true
		return res
	}

	override fun asDouble(): Double {
		if (done)
			return super.asDouble()
		val res = data.toDouble()
		ready = res
		done = true
		return res
	}

	override fun asFloatOrNull(): Float? {
		if (done)
			return super.asFloatOrNull()
		val res = data.toFloatOrNull()
		ready = res
		done = true
		return res
	}

	override fun asFloat(): Float {
		if (done)
			return super.asFloat()
		val res = data.toFloat()
		ready = res
		done = true
		return res
	}

	private var ready: Any? = null
	private var done = false

	override fun obj(): Any? {
		if (done)
			return ready
		ready = init()
		done = true
		return ready
	}

	override fun isPrimitive() = true
}