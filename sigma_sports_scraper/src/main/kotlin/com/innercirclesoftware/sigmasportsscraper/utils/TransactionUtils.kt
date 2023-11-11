package com.innercirclesoftware.sigmasportsscraper.utils

import org.springframework.transaction.support.TransactionSynchronization
import org.springframework.transaction.support.TransactionSynchronizationManager

inline fun runAfterCommit(crossinline runnable: () -> Unit) {
    TransactionSynchronizationManager.registerSynchronization(object : TransactionSynchronization {
        override fun afterCommit() {
            super.afterCommit()
            runnable()
        }
    })
}