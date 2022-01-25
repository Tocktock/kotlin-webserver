import auth.CheckCookieHandler
import auth.SignInHandler
import http.HttpRequest
import http.ResultDTO
import org.slf4j.LoggerFactory
import session.SessionManager
import tutorial.HelloHandler
import tutorial.ReadJsonHandler
import tutorial.ReadQueryString
import java.net.ServerSocket


internal val router = mutableMapOf<String, (HttpRequest) -> ResultDTO>()
val sessionManager = SessionManager()

class Application

fun main(args: Array<String>) {
    // pr template test
// hh this is tow
    val port = 8080
    val logger = LoggerFactory.getLogger(Application::class.java)
    registerRouter()

    ServerSocket(port).use { listenSocket ->
        logger.info("Web server started. port : $port")
        router.map { logger.info("${it.key} is mapped") }

        while (true) {
            listenSocket.accept()?.let {
                RequestHandler(socket = it).start()
            }
        }
    }
}

private fun registerRouter() {
    val handlers = listOf(HelloHandler(), ReadJsonHandler(), ReadQueryString(), SignInHandler(), CheckCookieHandler())
    handlers.map { router[it.url] = { HttpRequest -> it.handle(HttpRequest) } }
}