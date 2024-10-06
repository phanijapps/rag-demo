package com.perficient.automation.rag.demo.util

import org.slf4j.Logger
import org.slf4j.LoggerFactory

// Inline function for logging
inline fun <reified T> T.logInfo(message: () -> String) {
    val log: Logger = LoggerFactory.getLogger(T::class.java)
    if (log.isInfoEnabled) {
        log.info(message())
    }
}

inline fun <reified T> T.logError(message: () -> String) {
    val log: Logger = LoggerFactory.getLogger(T::class.java)
    if (log.isErrorEnabled) {
        log.error(message())
    }
}
