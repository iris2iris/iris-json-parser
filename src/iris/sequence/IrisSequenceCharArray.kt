package iris.sequence

import java.util.*

/**
 * @created 01.08.2020
 * @author [Ivan Ivanov](https://vk.com/irisism)
 */
class IrisSequenceCharArray(val source: CharArray, val start: Int = 0, val end: Int = source.size) : IrisSequence, CharArraySource {

	private val len = end - start
	override val length: Int
		get() = len

	override fun get(index: Int): Char {
		return source[start + index]
	}

	override fun subSequence(startIndex: Int, endIndex: Int): CharSequence {
		return IrisSequenceCharArray(source,this.start + startIndex,this.start + endIndex)
	}

	override fun toString(): String {
		return String(source, start, length)
	}

	override fun <A : Appendable> joinTo(buffer: A): A {
		when (buffer) {
			is StringBuilder -> buffer.append(source, start, length)
			is StringBuffer -> buffer.append(source, start, length)
			else -> buffer.append(this)
		}
		return buffer
	}

	private var hash = 0
	private var hashed = false
	override fun hashCode(): Int {
		if (hashed)
			return hash
		var res = 0
		for (i in start until end)
			res = (res * 33) + source[i].toInt()
		hash = res
		hashed = true
		return res
	}

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (other == null) return false

		if (this.javaClass == other.javaClass) {
			other as IrisSequenceCharArray
			return Arrays.equals(source, start, end, other.source, other.start, other.end)
		}
		if (other !is CharSequence)
			return false
		val len = len
		if (len != other.length) return false
		val source = source
		var pos = start
		for (i in 0 until len)
			if (source[pos++] != other[i])
				return false
		return true
	}

	override fun toCharArray(): CharArray {
		val len = length
		return toCharArray(CharArray(len), 0, 0, len)
	}

	override fun toCharArray(start: Int, len: Int): CharArray {
		return toCharArray(CharArray(len), 0, start, len)
	}

	override fun toCharArray(dest: CharArray): CharArray {
		return toCharArray(dest, 0, start, end - start)
	}

	override fun toCharArray(dest: CharArray, destOffset: Int, start: Int, len: Int): CharArray {
		val st = this.start + start
		source.copyInto(dest, destOffset, st, st + len)
		return dest
	}
}