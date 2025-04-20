package com.example.endertoolsbox.utils

import java.util.*

// Extension pour capitaliser la première lettre d'une chaîne
fun String.capitalize(): String {
    return this.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
}

