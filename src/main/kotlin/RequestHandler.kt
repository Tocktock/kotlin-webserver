import org.slf4j.LoggerFactory
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.net.Socket


class RequestHandler(
    private val socket: Socket
) : Thread() {

    private val logger = LoggerFactory.getLogger(RequestHandler::class.java)

    override fun run() {
        logger.debug("new request is created!! ${socket.inetAddress} : ${socket.port}")
        try {
            val inStream = socket.getInputStream()
            val outStream = socket.getOutputStream()

            val buffer = BufferedReader(InputStreamReader(inStream, "UTF-8"))
            var line = buffer.readLine()
            while (!line.isNullOrBlank()) {
                line = buffer.readLine()
                logger.debug("$line")
            }
            val dos = DataOutputStream(outStream)
            val body = "Hello World".toByteArray()
            response200Header(dos, body.size)
            responseBody(dos, body)
        }catch (e : RuntimeException) {
            logger.error("request is failed")
        }
    }
    private fun response200Header(dos: DataOutputStream, lengthOfBodyContent: Int) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n")
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n")
            dos.writeBytes("Content-Length: $lengthOfBodyContent\r\n")
            dos.writeBytes("\r\n")
        } catch (e: IOException) {
            logger.error(e.message)
        }
    }

    private fun responseBody(dos: DataOutputStream, body: ByteArray) {
        try {
            dos.write(body, 0, body.size)
            dos.flush()
        } catch (e: IOException) {
            logger.error(e.message)
        }
    }

}