package com.example.endertoolsbox.utils

import java.util.Locale

/**
 * Fonction d'extension pour mettre en majuscule la première lettre d'une chaîne
 */
fun String.capitalizeFirst(): String {
    return if (this.isEmpty()) this
    else this.substring(0, 1).uppercase(Locale.getDefault()) + this.substring(1)
} 