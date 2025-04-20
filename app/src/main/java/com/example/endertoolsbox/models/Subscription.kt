package com.example.endertoolsbox.models

import java.time.LocalDateTime
import java.util.UUID

data class Subscription(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val price: Double,
    val period: Period,
    val reminderEnabled: Boolean,
    val nextPayment: LocalDateTime
)

enum class Period {
    DAILY, MONTHLY, YEARLY
}