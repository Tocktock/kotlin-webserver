package auth

import abstract.AbstractHandler
import http.HttpRequest
import http.ResultDTO
import sessionManager

class SignInHandler : AbstractHandler {
    override val url = "/sign-in"

    override fun handle(req: HttpRequest): ResultDTO {
        val dto = req.readJsonBody(SignInDTO::class.java)
        if (dto.id == "test@test.com" && dto.password == "testPassword") {
            val sessionId = sessionManager.createNewSession("test@test.com")
            req.responseHeader.set("Set-Cookie", "sessionId=$sessionId;Path=/;Max-Age=120;")
//            req.setCookieTest(sessionId)
            return ResultDTO(true, sessionId)
        }
        return ResultDTO(false, "아이디와 비밀번호를 다시 확인해주세요.")
    }
}

data class SignInDTO(
    val id: String = "",
    val password: String = "",
)