package net.ultragrav.kserializer.json.format

import net.ultragrav.kserializer.json.JsonArray
import net.ultragrav.kserializer.json.JsonObject
import java.io.Reader
import java.nio.file.Files.readString

class JsonReader(reader: Reader) {
    val reader = reader.buffered()
    private var line: Int = 1
    private var column: Int = 0

    fun read(): Any? {
        while (true) {
            val ch = nextNWSP()
            when (ch) {
                '{' -> return readObject()
                '[' -> return readArray()
                '"' -> return readString()
                't' -> {
                    expect("rue")
                    return true
                }
                'f' -> {
                    expect("alse")
                    return false
                }
                'n' -> {
                    expect("ull")
                    return null
                }
            }

            if (ch.isDigit() || ch == '-') {
                return readNumber(ch)
            }
        }
    }

    private fun nextNWSP(): Char {
        while (true) {
            column++
            val ch = reader.read().toChar()
            if (!ch.isWhitespace()) return ch
            if (ch == '\n') {
                line++
                column = 0
            }
        }
    }

    private fun expect(str: String) {
        for (ch in str) {
            column++
            if (reader.read().toChar() != ch) {
                throw IllegalStateException("Expected '$ch' but got '$ch', at line $line, column $column")
            }
        }
    }

    private fun readString(): String {
        val builder = StringBuilder()
        var escaped = false

        while (true) {
            val ch = reader.read().toChar()
            column++

            if (escaped) {
                when (ch) {
                    '"', '\\', '/' -> builder.append(ch)
                    'b' -> builder.append('\b')
                    'f' -> builder.append('\u000C')
                    'n' -> builder.append('\n')
                    'r' -> builder.append('\r')
                    't' -> builder.append('\t')
                    'u' -> {
                        val hex = StringBuilder()
                        for (i in 0..<4) {
                            hex.append(reader.read().toChar())
                        }
                        builder.append(hex.toString().toInt(16).toChar())
                    }
                    else -> {
                        throw IllegalStateException("Invalid escape character: '$ch', at line $line, column $column")
                    }
                }
                escaped = false
            } else if (ch == '\\') {
                escaped = true
            } else if (ch == '"') {
                return builder.toString()
            } else {
                builder.append(ch)
            }
        }
    }

    private fun readObject(): JsonObject {
        val obj = JsonObject()

        while (true) {
            when (val ch = nextNWSP()) {
                '}' -> return obj
                '"' -> {
                    val key = readString()

                    // Skip whitespace
                    if (nextNWSP() != ':') {
                        throw IllegalStateException("Expected ':' but got '$ch', at line $line, column $column")
                    }

                    val value = read()
                    obj[key] = value
                }
            }
        }
    }

    private fun readArray(): JsonArray {
        val array = JsonArray()

        reader.mark(99)
        val lineMark = line
        val columnMark = column
        val nextChar = nextNWSP()
        line = lineMark
        column = columnMark
        reader.reset()
        if (nextChar == ']') {
            return array
        }

        while (true) {
            val el = read()
            array.add(el)

            when (val ch = nextNWSP()) {
                ']' -> return array
                ',' -> continue
                else -> throw IllegalStateException("Expected ',' or ']' but got '$ch', at line $line, column $column")
            }
        }
    }

    private fun readNumber(current: Char): Number {
        val builder = java.lang.StringBuilder()

        var ch = current
        while (true) {
            if (ch != '-' && !Character.isDigit(ch) && ch != 'e' && ch != 'E' && ch != '+' && ch != '.') {
                reader.reset()
                break
            }
            builder.append(ch)
            column++
            reader.mark(2)
            ch = reader.read().toChar()
        }

        val str = builder.toString()
        try {
            return str.toInt()
        } catch (ignored: NumberFormatException) {
        }
        try {
            return str.toLong()
        } catch (ignored: NumberFormatException) {
        }
        return try {
            str.toDouble()
        } catch (ex: NumberFormatException) {
            throw IllegalArgumentException("Invalid JSON number, at line $line, column $column")
        }
    }
}