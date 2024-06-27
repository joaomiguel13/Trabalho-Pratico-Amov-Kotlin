package pt.isec.locatewiki.utils.firebase

class FirebaseResponseHandler<T>(
    private val successCallback: (T?) -> Unit,
    private val failureCallback: (Exception) -> Unit
) {
    fun onSuccess(result: T?) {
        successCallback.invoke(result)
    }

    fun onFailure(exception: Exception) {
        failureCallback.invoke(exception)
    }
}
