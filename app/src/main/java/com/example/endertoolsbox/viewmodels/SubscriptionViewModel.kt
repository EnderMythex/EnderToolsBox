package com.example.endertoolsbox.viewmodels

import androidx.lifecycle.ViewModel
import com.example.endertoolsbox.models.Subscription
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SubscriptionViewModel : ViewModel() {
    private val _subscriptions = MutableStateFlow<List<Subscription>>(emptyList())
    val subscriptions: StateFlow<List<Subscription>> = _subscriptions.asStateFlow()

    fun addSubscription(subscription: Subscription) {
        _subscriptions.value = _subscriptions.value + subscription
        if (subscription.reminderEnabled) {
            scheduleReminder(subscription)
        }
    }

    fun removeSubscription(subscription: Subscription) {
        _subscriptions.value = _subscriptions.value - subscription
        cancelReminder(subscription)
    }

    private fun scheduleReminder(subscription: Subscription) {
        // Implémentation des rappels à faire via WorkManager
    }

    private fun cancelReminder(subscription: Subscription) {
        // Implémentation de l'annulation des rappels
    }
}