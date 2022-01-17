import org.junit.jupiter.api.Test
import java.io.InputStream
import java.io.InputStreamReader
import java.net.http.HttpRequest

class HttpReadTest {

    private val reqContent =
        """GET /get/post?title=hiMyLoad&author=jiyoung HTTP/1.1
       Host: localhost:8080
       Connection: keep-alive
       Accept: */*
    """.trimIndent()

    @Test
    fun getHeaderTest() {
    }
}