import handler.GetTestHandler
import handler.HelloHandler
import handler.PostTestHandler
import http.HttpRequest
import org.slf4j.LoggerFactory
import java.net.ServerSocket
import java.net.Socket


internal val router = mutableMapOf<String, (HttpRequest) -> Unit>()

class Application

fun main(args: Array<String>) {
    val hello = HelloHandler()
    val postHandler = PostTestHandler()
    val getHandler = GetTestHandler()
    router["hello"] = { HttpRequest -> hello.handle(HttpRequest) }
    router["post-test"] = { HttpRequest -> postHandler.handle(HttpRequest) }
    router["get-test"] = { HttpRequest -> getHandler.handle(HttpRequest) }

    val logger = LoggerFactory.getLogger(Application::class.java)

    ServerSocket(8080).use { listenSocket ->
        logger.info("Web server started.")
        router.map { logger.info("/${it.key} is mapped") }
        // 클라이언트가 연결될때까지 대기한다.
        var connection: Socket?
        while (listenSocket.accept().also { connection = it } != null) {
            val requestHandler = RequestHandler(connection!!)
            requestHandler.start()
        }
    }
}