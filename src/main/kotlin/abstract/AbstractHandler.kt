package abstract

import http.HttpRequest
import http.ResultDTO

interface AbstractHandler {
    val url: String

    fun handle(req: HttpRequest): ResultDTO
}