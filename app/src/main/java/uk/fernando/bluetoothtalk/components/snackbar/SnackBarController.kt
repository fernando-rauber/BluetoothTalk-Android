package uk.fernando.bluetoothtalk.components.snackbar

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarDuration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * If a snackbar is visible and the user triggers a second snackbar to show, it will remove
 * the first one and show the second. Likewise with a third, fourth, ect...
 *
 * If a mechanism like this is not used, snackbar get added to the Scaffolds "queue", and will
 * show one after another.
 *
 */
@ExperimentalMaterialApi
class SnackBarController
constructor(private val scope: CoroutineScope) {

    private var snackBarJob: Job? = null

    init {
        cancelActiveJob()
    }

    fun getScope() = scope

    fun showSnackBar(
        scaffoldState: ScaffoldState,
        message: String = "",
        longDuration: Boolean
    ) {
        if (snackBarJob == null) {
            snackBarJob = scope.launch {
                scaffoldState.snackbarHostState.showSnackbar(
                    message = message,
                    duration = if(longDuration) SnackbarDuration.Long else SnackbarDuration.Short
                )
                cancelActiveJob()
            }
        } else {
            cancelActiveJob()
            snackBarJob = scope.launch {
                scaffoldState.snackbarHostState.showSnackbar(
                    message = message,
                    duration = if(longDuration) SnackbarDuration.Long else SnackbarDuration.Short
                )
                cancelActiveJob()
            }
        }
    }

    private fun cancelActiveJob() {
        snackBarJob?.let { job ->
            job.cancel()
            snackBarJob = Job()
        }
    }
}
