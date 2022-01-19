package handler

import http.HttpRequest

interface AbstractHandler {
    fun handle(httpRequest: HttpRequest)
}