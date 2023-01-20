package movie.core.util

@JvmName("requireListNotEmpty")
fun <T> Result<List<T>>.requireNotEmpty() = mapCatching {
    require(it.isNotEmpty()) { "List was empty" }
    it
}

@JvmName("requireIterableNotEmpty")
fun <T> Result<Iterable<T>>.requireNotEmpty() = mapCatching {
    @Suppress("ReplaceSizeCheckWithIsNotEmpty")
    require(it.count() > 0) { "List was empty" }
    it
}