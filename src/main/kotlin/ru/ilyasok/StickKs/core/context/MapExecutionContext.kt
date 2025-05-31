package ru.ilyasok.StickKs.core.context

import ru.ilyasok.StickKs.core.context.ExecutionContext

@ru.ilyasok.StickKs.dsl.FeatureDSL
class MapExecutionContext(map: Map<String, Any?> = mutableMapOf()) : ExecutionContext() {
    private val contextMap: MutableMap<String, Any?> = map.toMutableMap()

    fun put(kv: Pair<String, Any?>) {
        contextMap[kv.first] = kv.second
    }

    fun remove(key: String) {
        contextMap.remove(key)
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> get(key: String): T {
        return contextMap[key] as? T ?: throw RuntimeException("Failed to get value by $key")
    }
}

class MapExecutionContextBuilder {
    private val map = mutableMapOf<String, Any?>()

    infix fun String.to(value: Any?) {
        map[this] = value
    }

    fun build(): MapExecutionContext = MapExecutionContext(map)
}


@ru.ilyasok.StickKs.dsl.FeatureDSL
inline fun ru.ilyasok.StickKs.dsl.FeatureBlockBuilder<MapExecutionContext>.contextMap(
    block: MapExecutionContextBuilder.() -> Unit
): MapExecutionContext {
    val ec = MapExecutionContextBuilder().apply(block).build()
    this.context = ec
    return ec
}