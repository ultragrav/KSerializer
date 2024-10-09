import kotlinx.serialization.Serializable
import net.ultragrav.kserializer.Serializers
import net.ultragrav.kserializer.Wrapper
import net.ultragrav.kserializer.json.JsonObject
import net.ultragrav.kserializer.json.JsonType
import net.ultragrav.kserializer.kotlinx.KJson

@Serializable
class VaultData {
    val vaults: MutableMap<String, Array<ByteArray?>> = mutableMapOf()
}

fun main() {
    val vaultData = VaultData()
    vaultData.vaults["test"] = arrayOf(byteArrayOf(1, 2, 3), null, byteArrayOf(4, 5, 6))
    val json = KJson.encode(vaultData)
    println(json.toString())
    val decoded = KJson.decode<VaultData>(json)
}