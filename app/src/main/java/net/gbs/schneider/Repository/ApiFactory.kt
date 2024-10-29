package net.gbs.scheinder.Repository

import com.google.gson.GsonBuilder
import net.gbs.schneider.Base.MainActivity.Companion.BASE_URL
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit


class ApiFactory {
    companion object{
        private var retrofit:Retrofit?=null
        private const val REQUEST_TIMEOUT = 60L
        private var okHttpClient:OkHttpClient?=null
        fun getInstance ():Retrofit?{
            if (okHttpClient==null) initOkHttpClient()
            if (retrofit==null){
                retrofit = okHttpClient?.let {
                    val gson = GsonBuilder()
                        .setLenient()
                        .create()
                    Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .client(it)
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .build()
                }
            }
            return retrofit
        }
        private fun initOkHttpClient (){
            val httpClient = OkHttpClient().newBuilder()
                .connectTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
            val interceptor = HttpLoggingInterceptor()
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
            httpClient.addInterceptor(interceptor)
            httpClient.addInterceptor(object : Interceptor {
                @Throws(IOException::class)
                override fun intercept(chain: Interceptor.Chain): Response {
                    val original: Request = chain.request()
                    val requestBuilder = original.newBuilder()
                        .addHeader("Accept", "application/json")
                        .addHeader("Content-Type", "application/json")
                    val request: Request = requestBuilder.build()
                    return chain.proceed(request)
                }
            })

            okHttpClient = httpClient.build()
        }
    }
}