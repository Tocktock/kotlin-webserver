package handler

import http.HttpRequest

class PostTestHandler : AbstractHandler {
    override fun handle(req: HttpRequest) {
        val data = req.readJsonBody(TClass::class.java)
        println(data)
        req.writeBody(true, "post handler is hitted")
    }
}

data class TClass(
    val fieldOne: String = "",
    val fieldTwo: String = "",
)