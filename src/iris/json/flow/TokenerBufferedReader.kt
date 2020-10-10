package iris.json.flow

import iris.sequence.CharArrayBuilder
import java.io.Reader

/**
 * @created 20.09.2020
 * @author [Ivan Ivanov](https://vk.com/irisism)
 */
class TokenerBufferedReader(private val reader: Reader, private val buffer: CharArray = CharArray(4*1024)): TokenerAbstractWithSequence() {

	private var isEof = false
	private var pointer = 0
	private var allocated = 0
	private var prevChar: Char? = null

	override fun curChar(): Char? {
		if (isEof) return null
		if (pointer == -1)
			return prevChar

		if (pointer >= allocated) {
			if (allocated != 0)
				prevChar = buffer[allocated-1]
			allocated = reader.read(buffer)
			if (allocated == -1) {
				isEof = true
				return null
			}
			pointer = 0
		}
		return buffer[pointer]
	}

	override fun curCharInc(): Char? {
		val ch = curChar()?: return null
		pointer++
		return ch
	}

	override fun moveNext() {
		curCharInc()
	}

	override fun exception(s: String): IllegalArgumentException {
		TODO(s)
	}

	override fun back() {
		pointer--
	}

	fun close() {
		reader.close()
	}

	override fun sequenceStart(): TokenSequence {
		return TImpl()
	}

	private class TImpl : TokenSequence {

		private val buff = CharArrayBuilder(16)

		override fun finish(shift: Int): CharSequence {
			return buff
		}

		override fun append(char: Char) {
			buff.append(char)
		}
	}
}