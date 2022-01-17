import org.slf4j.LoggerFactory
import java.net.ServerSocket
import java.net.Socket


class Application {

}

fun main(args: Array<String>) {
    val log = LoggerFactory.getLogger(Application::class.java)
    ServerSocket(8080).use { listenSocket ->
        log.info("hehehe connected")
        // 클라이언트가 연결될때까지 대기한다.
        var connection: Socket?
        while (listenSocket.accept().also { connection = it } != null) {
            val requestHandler = RequestHandler(connection!!)
            requestHandler.start()
        }
    }
}
