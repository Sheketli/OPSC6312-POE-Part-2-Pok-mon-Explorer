package com.pokemon.explorer.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState

/**
 * The three states any network-backed screen can be in.
 *
 * Modelling this explicitly (instead of separate `data` / `loading` / `error` fields)
 * means the UI can't accidentally render a half-loaded screen.
 */
sealed interface LoadState<out T> {
    data object Loading : LoadState<Nothing>
    data class Success<T>(val data: T) : LoadState<T>
    data class Failure(val message: String) : LoadState<Nothing>
}

/** The payload when the load succeeded, otherwise `null`. */
fun <T> LoadState<T>.dataOrNull(): T? = when (this) {
    is LoadState.Success -> data
    else -> null
}

/** True while the request is still in flight. */
val LoadState<*>.isLoading: Boolean get() = this is LoadState.Loading

/**
 * Runs [loader] whenever any entry of [keys] changes and reports the result as a
 * [LoadState]. Retriggering cancels the in-flight call, which is what makes the
 * search field's debounce work: each keystroke restarts the producer.
 */
@Composable
fun <T> rememberLoadState(vararg keys: Any?, loader: suspend () -> T): State<LoadState<T>> =
    produceState<LoadState<T>>(LoadState.Loading, *keys) {
        value = LoadState.Loading
        value = try {
            LoadState.Success(loader())
        } catch (error: Exception) {
            LoadState.Failure(error.message ?: "Unknown error")
        }
    }
