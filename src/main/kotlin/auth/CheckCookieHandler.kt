package auth

import abstract.AbstractHandler
import http.HttpRequest
import http.ResultDTO

class CheckCookieHandler : AbstractHandler {
    override val url = "/check-cookie"

    override fun handle(req: HttpRequest): ResultDTO {
        return ResultDTO(true, req.requestHeader.get("Cookie"))
    }
}