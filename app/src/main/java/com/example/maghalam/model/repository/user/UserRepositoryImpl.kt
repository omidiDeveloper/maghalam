package com.example.maghalam.model.repository.user


import com.example.maghalam.model.data.User
import com.example.maghalam.model.db.dao.UserDao
import com.example.maghalam.model.db.entity.toDomain
import com.example.maghalam.model.db.entity.toEntity
import com.example.maghalam.model.net.api.ApiService
import com.example.maghalam.model.net.dto.*
import com.example.maghalam.utills.SharedPreferencesManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import android.util.Base64
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import java.io.IOException
import retrofit2.Response


class UserRepositoryImpl(
    private val apiService: ApiService,
    private val userDao: UserDao,
    private val preferences: SharedPreferencesManager
) : UserRepository {


    override fun login(
        request: LoginRequest
    ): Flow<ApiResponse<AuthResponse>> = flow {

        emit(ApiResponse.Loading)


        try {

            val response =
                apiService.login(request)


            if (response.isSuccessful) {
                val body = response.body()

                if (body != null) {
                    saveUserSession(body, request.username)
                    emit(ApiResponse.Success(body))
                } else {
                    emit(ApiResponse.Error(response.code(), "Empty login response"))
                }
            } else {


                emit(
                    ApiResponse.Error(
                        response.code(),
                        response.errorMessage()
                    )
                )

            }


        } catch (e: IOException) {


            emit(ApiResponse.NetworkError)

        } catch (e: Exception) {
            emit(ApiResponse.Error(-1, e.message ?: "Login failed"))
        }

    }.flowOn(Dispatchers.IO)


    override fun register(
        request: RegisterRequest
    ): Flow<ApiResponse<AuthResponse>> = flow {

        emit(ApiResponse.Loading)
        try {
            val response =
                apiService.register(request)
            if (response.isSuccessful) {
                val body =
                    response.body()
                if (body != null) {
                    saveUserSession(body, request.username)
                    emit(
                        ApiResponse.Success(body)
                    )
                } else {
                    emit(ApiResponse.Error(response.code(), "Empty register response"))
                }
            } else {
                emit(
                    ApiResponse.Error(
                        response.code(),
                        response.errorMessage()
                    )
                )

            }
        } catch (e: IOException) {
            emit(ApiResponse.NetworkError)
        } catch (e: Exception) {
            emit(ApiResponse.Error(-1, e.message ?: "Register failed"))
        }


    }.flowOn(Dispatchers.IO)


    private suspend fun saveUserSession(
        response: AuthResponse,
        fallbackUsername: String
    ) {
        val accessToken = response.accessToken
            ?.takeIf { it.isNotBlank() }
            ?: error("Authentication response did not include an access token")
        val user = response.userProfile(fallbackUsername)
        val role = resolveRole(response, user.role)


        preferences.saveTokens(
            accessToken,
            response.refreshToken.orEmpty()
        )


        preferences.saveUserInfo(
            user.username.orEmpty(),
            user.id,
            role,
            user.fullName.orEmpty(),
            user.email.orEmpty()
        )


        userDao.insertUser(
            user.toEntity()
        )

    }

    private fun resolveRole(response: AuthResponse, userRole: String?): String {
        if (listOfNotNull(
                userRole,
                response.role,
                extractRole(response.roles),
                extractRole(response.authorities),
                extractRoleFromToken(response.accessToken)
            ).any { it.contains("ADMIN", ignoreCase = true) }
        ) {
            return "ADMIN"
        }

        return listOfNotNull(
            userRole,
            response.role,
            extractRole(response.roles),
            extractRole(response.authorities),
            extractRoleFromToken(response.accessToken)
        ).firstOrNull { it.contains("ADMIN", ignoreCase = true) || it.contains("USER", ignoreCase = true) }
            ?: "USER"
    }

    private fun extractRole(source: JsonElement?): String? {
        if (source == null || source.isJsonNull) return null

        if (source.containsAdminRole()) return "ADMIN"

        return when {
            source.isJsonPrimitive -> source.asString
            source.isJsonArray -> source.asJsonArray.firstNotNullOfOrNull { extractRole(it) }
            source.isJsonObject -> {
                val json = source.asJsonObject
                listOf("role", "name", "authority")
                    .firstNotNullOfOrNull { key ->
                        json.get(key)?.takeIf { !it.isJsonNull }?.asString
                    }
            }
            else -> null
        }
    }

    private fun JsonElement?.containsAdminRole(): Boolean {
        if (this == null || isJsonNull) return false

        return when {
            isJsonPrimitive -> asString.contains("ADMIN", ignoreCase = true)
            isJsonArray -> asJsonArray.any { it.containsAdminRole() }
            isJsonObject -> asJsonObject.entrySet().any { it.value.containsAdminRole() }
            else -> false
        }
    }

    private fun extractRoleFromToken(token: String?): String? {
        if (token.isNullOrBlank()) return null

        return runCatching {
            val payload = token.split(".").getOrNull(1) ?: return@runCatching null
            val decoded = String(Base64.decode(payload, Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP))
            val json = JsonParser.parseString(decoded).asJsonObject

            listOf("role", "roles", "authorities", "scope")
                .firstNotNullOfOrNull { key -> extractRole(json.get(key)) }
        }.getOrNull()
    }

    private fun Response<*>.errorMessage(): String {
        return errorBody()?.string()
            ?.takeIf { it.isNotBlank() }
            ?: message()
            ?: "Request failed"
    }


    override fun logout(): Flow<ApiResponse<Unit>> = flow {


        emit(ApiResponse.Loading)


        try {


            val response =
                apiService.logout()


            preferences.clearAll()

            userDao.deleteAllUsers()



            if (response.isSuccessful) {

                emit(
                    ApiResponse.Success(Unit)
                )

            } else {

                emit(
                    ApiResponse.Error(
                        response.code(),
                        response.message()
                    )
                )
            }


        } catch (e: IOException) {


            preferences.clearAll()
            userDao.deleteAllUsers()


            emit(ApiResponse.NetworkError)

        }


    }.flowOn(Dispatchers.IO)


    override fun getUserProfile(): Flow<ApiResponse<User>> = flow {
        emit(ApiResponse.Loading)
        val userId =
            preferences.getUserId()
        userDao.getUserById(userId)
            ?.let {

                emit(
                    ApiResponse.Success(
                        it.toDomain()
                    )
                )

            }
        try
        {
            val response =
                apiService.getUserProfile()

            if (response.isSuccessful) {
                response.body()?.let { profile ->


                    userDao.insertUser(
                        profile.toEntity()
                    )

                    emit(
                        ApiResponse.Success(profile.toUser())
                    )

                }

            }
        } catch (e: IOException) {
            emit(ApiResponse.NetworkError)
        }
    }.flowOn(Dispatchers.IO)


    override fun updateProfile(
        fullName: String,
        email: String
    ): Flow<ApiResponse<User>> = flow {


        emit(ApiResponse.Loading)


        try {

            val response =
                apiService.updateProfile(
                    mapOf(
                        "fullName" to fullName,
                        "email" to email
                    )
                )
            if (response.isSuccessful) {

                response.body()?.let { profile ->
                    userDao.insertUser(
                        profile.toEntity()
                    )
                    emit(
                        ApiResponse.Success(profile.toUser())
                    )

                }

            } else {
                emit(
                    ApiResponse.Error(
                        response.code(),
                        response.message()
                    )
                )

            }


        } catch (e: IOException) {

            emit(ApiResponse.NetworkError)

        }


    }.flowOn(Dispatchers.IO)






    override fun updateSettings(
        darkMode:Boolean,
        fontSize:String
    ): Flow<ApiResponse<User>> = flow {


        emit(ApiResponse.Loading)

        try {
            val response =
                apiService.updateSettings(
                    mapOf(
                        "darkMode" to darkMode,
                        "fontSize" to fontSize
                    )
                )
            if(response.isSuccessful){


                response.body()?.let { profile ->

                    userDao.insertUser(
                        profile.toEntity()
                    )

                    emit(
                        ApiResponse.Success(profile.toUser())
                    )
                }


            }



        }catch(e: IOException){

            emit(ApiResponse.NetworkError)

        }
    }.flowOn(Dispatchers.IO)







    override fun changePassword(
        currentPassword:String,
        newPassword:String
    ): Flow<ApiResponse<Unit>> = flow {


        emit(ApiResponse.Loading)


        try {


            val response =
                apiService.changePassword(
                    mapOf(
                        "currentPassword" to currentPassword,
                        "newPassword" to newPassword
                    )
                )


            if(response.isSuccessful){

                emit(
                    ApiResponse.Success(Unit)
                )

            }else{

                emit(
                    ApiResponse.Error(
                        response.code(),
                        response.message()
                    )
                )
            }



        }catch(e: IOException){

            emit(ApiResponse.NetworkError)

        }


    }.flowOn(Dispatchers.IO)

    override fun getUsers(): Flow<ApiResponse<List<User>>> = flow {
        emit(ApiResponse.Loading)

        try {
            val adminResponse = apiService.getAdminUsers()
            val response = if (adminResponse.isSuccessful) {
                adminResponse
            } else {
                apiService.getUsers()
            }
            if (response.isSuccessful) {
                val users = response.body().orEmpty()
                userDao.deleteAllUsers()
                users.forEach { userDao.insertUser(it.toEntity()) }
                emit(ApiResponse.Success(users.map { it.toUser() }))
                return@flow
            } else {
                emit(ApiResponse.Error(response.code(), response.errorMessage()))
                return@flow
            }
        } catch (e: IOException) {
            emit(ApiResponse.NetworkError)
            return@flow
        }
    }.flowOn(Dispatchers.IO)

    override fun deleteUser(userId: Long): Flow<ApiResponse<Unit>> = flow {
        emit(ApiResponse.Loading)

        try {
            val response = apiService.deleteUserById(userId)
            if (!response.isSuccessful) {
                emit(ApiResponse.Error(response.code(), response.message()))
                return@flow
            }
        } catch (_: IOException) {
            // Keep local admin actions responsive when the network drops.
        }

        userDao.deleteUserById(userId)
        emit(ApiResponse.Success(Unit))
    }.flowOn(Dispatchers.IO)

}
