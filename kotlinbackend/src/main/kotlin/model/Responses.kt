package model

data class AbstractAPIResponse<T>(val statusCode: Int, val skip: Int = 0, val limit: Int, val dataList: List<T>)
data class ErrorResponse(val title: String, val shortMessage: String, val stacktrace: String)

fun Throwable.getErrorResponse() =
        ErrorResponse(this::class.simpleName.toString(), this.localizedMessage, this.stackTrace.joinToString(separator = "\n"))