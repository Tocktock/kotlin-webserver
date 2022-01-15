import java.io.DataOutputStream
import java.io.IOException
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import java.nio.channels.ServerSocketChannel
import java.nio.channels.SocketChannel
import java.util.*

data class Server(val id: String, val channel: ServerSocketChannel)

/**
 * Represents a client.
 */
data class Client(val server: Server, val channel: SocketChannel) {

    private val buffer = ByteBuffer.allocate(512)!!

    val id = UUID.randomUUID().toString()

    var active = true

    private fun send(data: String) {
        buffer.clear()
//        val header =
//                    "HTTP/1.1 200 OK \r\n"
//                    "Content-Type: text/html;charset=utf-8 \r\n"
//                    "Content-Length: ${data.length} \r\n \r\n"
//                    "$data \r\n"
        buffer.put("HTTP/1.1 200 OK \r\n" .toByteArray())
        buffer.put("Content-Type: text/html;charset=utf-8 \r\n".toByteArray())
        buffer.put("Content-Length: ${data.length} \r\n \r\n".toByteArray())
        buffer.put("$data ".toByteArray())
        buffer.flip()
        channel.write(buffer)
    }

    fun handleMessage() {
        buffer.clear()
        val bytesRead = channel.read(buffer)
        if (bytesRead == -1) {
            close()
        } else {
            handleMessage(String(buffer.array(), 0, bytesRead))
        }
    }

    private fun handleMessage(data: String) {
            send("\n -- Bye Bye!\n\n")
            close()
    }

    private fun close() {
        channel.close()
        active = false
    }

}

fun main(args: Array<String>) {
    val selector = Selector.open()
    arrayOf(8080, 8081, 8082, 8083)
        .map { createServer(it) }
        .mapIndexed { i: Int, channel: ServerSocketChannel -> Server("server-${i + 1}", channel) }
        .forEach { it.channel.register(selector, SelectionKey.OP_ACCEPT, it) }

    while (true) {
        selector.select()
        val iterator = selector.selectedKeys().iterator()
        while (iterator.hasNext()) {
            val selection = iterator.next()
            iterator.remove()

            if (selection.isAcceptable) {
                val server: Server = selection.attachment() as Server
                val client = Client(server, server.channel.accept())
                client.channel.configureBlocking(false)
                client.channel.register(selector, SelectionKey.OP_READ, client)
                println("Server `${server.id}` got a new client with id `${client.id}`.")
            } else if (selection.isReadable) {
                val client: Client = selection.attachment() as Client
                println("client with id `${client.id}` is readable.")
                client.handleMessage()
                if (!client.active) {
                    println("Server `${client.server.id}` lost client `${client.id}`.")
                }
            }
        }
    }
}

fun createServer(port: Int): ServerSocketChannel {
    val serverChannel = ServerSocketChannel.open()
    serverChannel.socket().bind(InetSocketAddress(port))
    serverChannel.configureBlocking(false)
    println("Listing on ${serverChannel.socket().inetAddress}:${serverChannel.socket().localPort}")
    return serverChannel
}
