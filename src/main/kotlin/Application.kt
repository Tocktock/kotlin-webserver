import java.io.IOException
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import java.nio.channels.ServerSocketChannel
import java.nio.channels.SocketChannel


class Application {

}
//fun main(args: Array<String>) {
//    val log = LoggerFactory.getLogger(Application::class.java)
//    ServerSocket(8080).use { listenSocket ->
//        log.info("hehehe connected")
//        // 클라이언트가 연결될때까지 대기한다.
//        var connection: Socket?
//        val startTime = LocalDateTime.now()
//        while (listenSocket.accept().also { connection = it } != null) {
//            val requestHandler = RequestHandler(connection!!)
//            requestHandler.start()
//        }
//    }
//}
