package tutorial

import abstract.AbstractHandler
import http.HttpRequest
import http.ResultDTO

class ReadQueryString : AbstractHandler {
    override val url = "/tutorial-get"
    override fun handle(req: HttpRequest): ResultDTO {
        println(req.getParam("fieldOne"))
        println(req.getParam("fieldTwo"))
        return ResultDTO(true, "Read Query String Controller")
    }
}