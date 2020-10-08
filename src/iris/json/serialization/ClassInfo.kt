package iris.json.serialization

import iris.json.JsonEntry
import iris.json.JsonItem
import java.util.*
import kotlin.collections.HashMap
import kotlin.reflect.KFunction
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KParameter
import kotlin.reflect.KProperty

/**
 * @created 08.10.2020
 * @author [Ivan Ivanov](https://vk.com/irisism)
 */

class ClassInfo(private val constructorFunction: KFunction<*>, private val fields: Map<String, PropertyInfo>, private val hasPolymorphisms: Boolean) : NodeInfo {

	fun <T: Any>getObject(entries: List<JsonEntry>): T {
		val info = this
		val fields = info.fields
		val hasPolymorphisms = info.hasPolymorphisms
		val delayedInit: MutableMap<String, Delayed>?
		val result: MutableMap<String, Any?>?
		if (hasPolymorphisms) {
			delayedInit = mutableMapOf()
			result = mutableMapOf()
		} else {
			delayedInit = null
			result = null
		}

		val otherFields = mutableListOf<Pair<KProperty<*>, Any?>>()
		val constructorMap = HashMap<KParameter, Any?>(info.constructorFunction.parameters.size)
		for ((key, jsonItem) in entries) {
			val field = key.toString()
			val param = fields[field]?: continue
			val polymorphInfo = param.polymorphInfo
			if (polymorphInfo != null) {
				val sourceValue = result!![polymorphInfo.sourceField]
				if (sourceValue != null) { // already know what type is it
					val inherit = polymorphInfo.inheritClasses[sourceValue]!!
					val newValue: Any? = jsonItem.asObject(inherit)
					result[field] = newValue
					param.constructorParameter?.let { constructorMap[it] = newValue }
							?: run { otherFields += param.property to newValue }

				} else { // need delay initialization until we know source info
					val item = delayedInit!![polymorphInfo.sourceField]
					if (item == null)
						delayedInit[polymorphInfo.sourceField] = Delayed(Delayed.Data(field, param, jsonItem))
					else
						item.add(Delayed.Data(field, param, jsonItem))
				}
			} else {
				val value = getValue(jsonItem, param)
				if (delayedInit != null) {
					val delayed = delayedInit[field]
					if (delayed != null) { // yes! at last we have delayed information!
						val item = delayed.firstItem
						val property = item.propertyInfo
						val inherit = property.polymorphInfo!!.inheritClasses[value]!!
						val newValue: Any = item.json.asObject(inherit)
						item.propertyInfo.constructorParameter?.let { constructorMap[it] = newValue }
								?: run { otherFields += property.property to newValue }

						if (delayed.items != null) {
							for (item in delayed.items!!) {
								val property = item.propertyInfo
								val inherit = property.polymorphInfo!!.inheritClasses[value]!!
								val newValue: Any = item.json.asObject(inherit)
								item.propertyInfo.constructorParameter?.let { constructorMap[it] = newValue }
										?: run { otherFields += property.property to newValue }
							}
						}
					}
					result!![field] = value
				}

				param.constructorParameter?.let { constructorMap[it] = value }
						?: run{ otherFields += param.property to value }
			}
		}
		val item = info.constructorFunction.callBy(constructorMap) as T
		for (field in otherFields) {
			(field.first as KMutableProperty<*>).setter.call(item, field.second)
		}
		return item
	}

	private class Delayed(val firstItem: Data) {
		class Data(val field: String, val propertyInfo: PropertyInfo, val json: JsonItem)

		var items: MutableList<Data>? = null
		fun add(item: Data) {
			if (items == null)
				items = LinkedList()
			items!! += item
		}
	}

	private fun getValue(value: JsonItem, property: PropertyInfo): Any? {
		property.type?.let {
			return it.getValue(value)
		}

		property.customClass?.let {
			return value.asObject(it)
		}
		throw IllegalStateException("How we got here?")
	}
}