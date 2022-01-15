import org.slf4j.LoggerFactory
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader
import java.net.Socket

class RequestHandlerCoroutine(
    private val socket: Socket
) {
    private val logger = LoggerFactory.getLogger(RequestHandler::class.java)

}