package tutorial

import abstract.AbstractHandler
import http.HttpRequest
import http.ResultDTO

class HelloHandler : AbstractHandler {
    override val url = "/hello"
    override fun handle(req: HttpRequest): ResultDTO {
        return ResultDTO(true, "heelo my friends")
    }
}