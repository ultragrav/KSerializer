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