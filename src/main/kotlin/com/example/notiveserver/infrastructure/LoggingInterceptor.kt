package com.example.notiveserver.infrastructure

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor

@Component
class LoggingInterceptor : HandlerInterceptor {
    private val log: Logger = LoggerFactory.getLogger(LoggingInterceptor::class.java)

    override fun preHandle(
        req: HttpServletRequest,
        res: HttpServletResponse,
        handler: Any
    ): Boolean {
        log.info("Incoming preview request: path='{}'", req.requestURI)
        return true
    }
}