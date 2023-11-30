package ja.insepector.base.base.mvvm

import ja.insepector.base.bean.ResResponse
import ja.insepector.base.http.RetrofitUtils
import ja.insepector.base.request.Api


open class BaseRepository {

    val mServer by lazy {
        RetrofitUtils.getInstance().createCoroutineRetrofit(
            Api::class.java,
            UrlManager.getServerUrl()
        )
    }

    suspend fun <T : Any> apiCall(call: suspend () -> ResResponse<T>): ResResponse<T> {
        return call.invoke()
    }

}