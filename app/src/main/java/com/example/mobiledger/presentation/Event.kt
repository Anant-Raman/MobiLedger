package com.example.mobiledger.presentation

import androidx.lifecycle.Observer
import com.example.mobiledger.domain.AppResult
import kotlinx.coroutines.Deferred

open class Event<out T>(private val content: T) {

    var hasBeenHandled = false
        private set // Allow external read but not write

    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    fun peekContent(): T = content
}

class OneTimeObserver<T>(private val onEventUnhandledContent: (T) -> Unit) : Observer<Event<T>> {
    override fun onChanged(event: Event<T>?) {
        event?.getContentIfNotHandled()?.let { value ->
            onEventUnhandledContent(value)
        }
    }
}

class ConditionalOneTimeObserver<T>(private val onEventUnhandledContent: (T) -> Boolean) :
    Observer<Event<T>> {
    override fun onChanged(event: Event<T>?) {
        event?.peekContent()?.let {
            if (!event.hasBeenHandled && onEventUnhandledContent(it)) {
                event.getContentIfNotHandled()
            }
        }
    }
}

class NormalObserver<T>(private val onEventUnhandledContent: (T) -> Unit) : Observer<Event<T>> {
    override fun onChanged(event: Event<T>?) {
        event?.peekContent()?.let { value ->
            onEventUnhandledContent(value)
        }
    }
}

suspend fun getResultFromJobs(list: List<Deferred<AppResult<Unit>>>): AppResult<Unit> {
    list.forEach {
        val a = it.await()
        if (a is AppResult.Failure) return a
    }
    return AppResult.Success(Unit)
}




