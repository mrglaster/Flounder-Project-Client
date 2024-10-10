package ru.flounder.retrofit

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import ru.flounder.dto.SignupRequestDTO
import ru.flounder.dto.StudyModuleInfoResponseDTO
import ru.flounder.dto.StudyModuleRequest
import ru.flounder.dto.TokenResponse

interface ApiService {

    @POST("api/auth/signin")
    fun login(@Body loginRequest: Map<String, String>): Call<TokenResponse>

    @POST("api/auth/signup")
    fun signUp(@Body signupRequest: SignupRequestDTO): Call<Void>

    @GET("api/study/modules/latest")
    fun getLatestStudyModules(
        @Header("Authorization") authorizationHeader: String,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10
    ): Call<List<StudyModuleInfoResponseDTO>>

    @POST("api/study/modules/search")
    fun searchStudyModules(
        @Header("Authorization") authorizationHeader: String,
        @Body request: Map<String, String>
    ): Call<List<StudyModuleInfoResponseDTO>>

    @GET("download/modules/{module_name}")
    fun downloadModule(
        @Header("Authorization") authorizationHeader: String,
        @Path("module_name") moduleName: String
    ): Call<ResponseBody>

    @POST("api/study/modules/user")
    fun getUserStudyModules(
        @Header("Authorization") authorizationHeader: String,
        @Body requestBody: Map<String, Int?>
    ): Call<List<StudyModuleInfoResponseDTO>>

    @POST("api/study/modules/new")
    fun createStudyModule(
        @Header("Authorization") authorizationHeader: String,
        @Body requestBody: StudyModuleRequest
    ): Call<Void>

}

object RetrofitClient {
    private const val BASE_URL = "http://192.168.0.121:8080/"
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}
