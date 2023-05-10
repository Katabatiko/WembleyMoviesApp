package com.gonpas.wembleymoviesapp.utils

import com.gonpas.wembleymoviesapp.domain.DomainMovie
import java.text.NumberFormat


private val PUNCTUATION = listOf(", ", "; ", ": ", " ")

fun String.smartTruncate(length: Int): String {
    val words = split(" ")
    var added = 0
    var hasMore = false
    val builder = StringBuilder()
    for (word in words) {
        if (builder.length > length) {
            hasMore = true
            break
        }
        builder.append(word)
        builder.append(" ")
        added += 1
    }

    PUNCTUATION.map {
        if (builder.endsWith(it)) {
            builder.replace(builder.length - it.length, builder.length, "")
        }
    }

    if (hasMore) {
        builder.append("...")
    }
    return builder.toString()
}

fun localNumberFormat(number: Int): String{
    return NumberFormat.getInstance().format(number)
}


class MovieListener(val clickListener: (movie: DomainMovie) -> Unit){
    fun onClick(movie: DomainMovie) = clickListener(movie)
}
class PersonListener(val clickListener: (personId: Int) -> Unit){
    fun onClick(personId: Int) = clickListener(personId)
}