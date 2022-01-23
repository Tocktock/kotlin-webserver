package http

class HttpHeader {
    private val headers = mutableMapOf<String, String>()
    fun set(key: String, value: String) {
        headers[key] = value
    }

    fun get(key: String): String? = headers[key]
    fun getAll() = headers.toMap()
}