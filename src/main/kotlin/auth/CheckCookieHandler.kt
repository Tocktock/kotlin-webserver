package auth

import abstract.AbstractHandler
import http.HttpRequest
import http.ResultDTO

class CheckCookieHandler : AbstractHandler {
    override val url = "/check-cookie"

    override fun handle(req: HttpRequest): ResultDTO {
        req.headers.get("Cookie")?.let { println(it) }
        return ResultDTO(true, "Cookie Check")
    }
}