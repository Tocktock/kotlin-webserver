package handler

import http.HttpRequest

class GetTestHandler : AbstractHandler {
    override fun handle(req: HttpRequest) {
        val params = req.readQueryString()
        println(params)
        req.writeBody(true, "Thank you")
    }
}