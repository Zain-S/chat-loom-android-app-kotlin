package com.saeedtechies.chatloom.extension

import android.graphics.Rect
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

// Extension function to wrap setValue with coroutine support
suspend fun DatabaseReference.awaitCompletion(): String? {
    return suspendCancellableCoroutine { continuation ->
        this.setValue(null) { databaseError, databaseReference ->
            if (databaseError != null) {
                continuation.resumeWithException(databaseError.toException())
            } else {
                continuation.resume(databaseReference.key)
            }
        }
    }
}

suspend fun DatabaseReference.setValue(value: Any): DatabaseReference =
    suspendCancellableCoroutine { continuation ->
        this.setValue(value) { databaseError, databaseReference ->
            if (databaseError != null) {
                continuation.resumeWithException(databaseError.toException())
            } else {
                continuation.resume(databaseReference)
            }
        }
    }

fun RecyclerView.attachSoftKeyboardListener(){
    viewTreeObserver.addOnGlobalLayoutListener {
        val rect = Rect()
        getWindowVisibleDisplayFrame(rect)
        val screenHeight = rootView.height
        val keypadHeight = screenHeight - rect.bottom

        if (keypadHeight > screenHeight * 0.15) {
            // Keyboard is open, scroll to the last item
            scrollToEndOfRecyclerView()
        }
    }
}

fun RecyclerView.scrollToEndOfRecyclerView() {
    if (adapter?.itemCount != 0)
        post {
        adapter?.let {
            smoothScrollToPosition(it.itemCount - 1)
        }
    }
}