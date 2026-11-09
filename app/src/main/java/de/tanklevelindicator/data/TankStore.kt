package de.tanklevelindicator.data

import android.content.Context
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import de.tanklevelindicator.domain.InputLimits
import de.tanklevelindicator.domain.TankSpec
import de.tanklevelindicator.domain.TankType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject

data class SavedTank(val id: String, val name: String, val spec: TankSpec)

private val Context.dataStore by preferencesDataStore("tank_level")

/** Speichert Tanks und Fuellgrenze ausschliesslich lokal auf dem Geraet. */
class TankStore(context: Context) {
    private val store = context.applicationContext.dataStore
    private val tanksKey = stringPreferencesKey("tanks")
    private val limitKey = doublePreferencesKey("fill_limit")

    val tanks: Flow<List<SavedTank>> = store.data.map { decode(it[tanksKey]) }
    val fillLimit: Flow<Double> = store.data.map { it[limitKey] ?: InputLimits.DEFAULT_FILL_LIMIT }

    suspend fun setFillLimit(value: Double) {
        store.edit { it[limitKey] = value }
    }

    suspend fun add(tank: SavedTank) {
        store.edit { it[tanksKey] = encode(decode(it[tanksKey]) + tank) }
    }

    suspend fun remove(id: String) {
        store.edit { prefs -> prefs[tanksKey] = encode(decode(prefs[tanksKey]).filterNot { it.id == id }) }
    }

    private fun encode(tanks: List<SavedTank>): String = JSONArray().apply {
        tanks.forEach {
            put(
                JSONObject()
                    .put("id", it.id)
                    .put("name", it.name)
                    .put("type", it.spec.type.name)
                    .put("volume", it.spec.volumeLiters)
                    .put("height", it.spec.heightCm),
            )
        }
    }.toString()

    private fun decode(json: String?): List<SavedTank> {
        if (json == null) return emptyList()
        return runCatching {
            val array = JSONArray(json)
            List(array.length()) { i ->
                val o = array.getJSONObject(i)
                SavedTank(
                    id = o.getString("id"),
                    name = o.getString("name"),
                    spec = TankSpec(
                        type = TankType.valueOf(o.getString("type")),
                        volumeLiters = o.getDouble("volume"),
                        heightCm = o.getDouble("height"),
                    ),
                )
            }
        }.getOrDefault(emptyList())
    }
}
