package tutorial

import abstract.AbstractHandler
import http.HttpRequest
import http.ResultDTO

class ReadJsonHandler : AbstractHandler {
    override val url = "/tutorial-json"
    override fun handle(req: HttpRequest): ResultDTO {
        val data = req.readJsonBody(TClass::class.java)
        println(data)
        return ResultDTO(true, TClass("read json handler is hit", "and i love you"))
    }
}

data class TClass(
    val fieldOne: String = "",
    val fieldTwo: String = "",
)