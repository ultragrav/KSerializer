package net.ultragrav.kserializer.json.format

import net.ultragrav.kserializer.json.JsonArray
import net.ultragrav.kserializer.json.JsonObject
import java.io.Reader

class JsonReader(val reader: Reader) {
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
            val ch = reader.read().toChar()
            if (!ch.isWhitespace()) return ch
        }
    }

    private fun expect(str: String) {
        for (ch in str) {
            if (reader.read().toChar() != ch) {
                throw IllegalStateException("Expected '$ch' but got '$ch'")
            }
        }
    }

    private fun readString(): String {
        val builder = StringBuilder()
        var escaped = false

        while (true) {
            val ch = reader.read().toChar()

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
                }
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
            val ch = nextNWSP()

            when (ch) {
                '}' -> return obj
                '"' -> {
                    val key = readString()

                    // Skip whitespace
                    if (nextNWSP() != ':') {
                        throw IllegalStateException("Expected ':' but got '$ch'")
                    }

                    val value = read()
                    obj[key] = value
                }
            }
        }
    }

    private fun readArray(): JsonArray {
        val array = JsonArray()

        while (true) {
            read()

            val ch = nextNWSP()

            when (ch) {
                ']' -> return array
                ',' -> continue
                else -> throw IllegalStateException("Expected ',' or ']' but got '$ch'")
            }
        }
    }

    private fun readNumber(current: Char): Number {
        val builder = java.lang.StringBuilder()

        var ch = current
        while (true) {
            if (ch != '-' && !Character.isDigit(ch) && ch != 'e' && ch != 'E' && ch != '+' && ch != '.') {
                break
            }
            builder.append(ch)
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
            throw IllegalArgumentException("Invalid JSON number")
        }
    }
}