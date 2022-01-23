package session

import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

class SessionManager {
    private val sessionMap = mutableMapOf<String, SessionInfo>()
    private val expireMinute = 20L

    fun createNewSession(userId: String): String {
        val sessionId = UUID.randomUUID().toString()

        sessionMap[sessionId] =
            SessionInfo(
                userId = userId,
                issuedAt = LocalDateTime.now(ZoneId.of("Asia/Seoul")),
                expiredAt = LocalDateTime.now(ZoneId.of("Asia/Seoul")).plusMinutes(expireMinute)
            )
        return sessionId
    }

    fun getSession(sessionId: String): SessionInfo {
        val sessionInfo = checkNotNull(sessionMap[sessionId]) { "sessionId 가 존재하지 않습니다." }
        check(sessionInfo.validateExpire()) { "만료된 sessionId 입니다." }
        return sessionInfo
    }
}

data class SessionInfo(
    val userId: String,
    val issuedAt: LocalDateTime,
    val expiredAt: LocalDateTime,
) {
    fun validateExpire(): Boolean {
        if (expiredAt < LocalDateTime.now(ZoneId.of("Asia/Seoul")))
            return false
        return true
    }
}