package handler

import http.HttpRequest

class HelloHandler : AbstractHandler {
    override fun handle(req: HttpRequest) {
        req.writeBody(true, "hello my friends!")
    }
}