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
import java.io.IOException


@Suppress("UNCHECKED_CAST")
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


                val body =
                    response.body()


                if (body != null) {

                    saveUserSession(body)


                    emit(
                        ApiResponse.Success(body)
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
                    saveUserSession(body)
                    emit(
                        ApiResponse.Success(body)
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


    private suspend fun saveUserSession(
        response: AuthResponse
    ) {


        preferences.saveTokens(
            response.accessToken,
            response.refreshToken
        )


        preferences.saveUserInfo(
            response.user.username,
            response.user.id ?: -1L
        )


        userDao.insertUser(
            response.user.toEntity()
        )

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
                response.body()?.let {


                    userDao.insertUser(
                        it.toEntity()
                    )

                    emit(
                        ApiResponse.Success(it)
                    )

                }

            }
        } catch (e: IOException) {
            emit(ApiResponse.NetworkError)
        }
    }.flowOn(Dispatchers.IO) as Flow<ApiResponse<User>>


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

                response.body()?.let {
                    userDao.insertUser(
                        it.toEntity()
                    )
                    emit(
                        ApiResponse.Success(it)
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


    }.flowOn(Dispatchers.IO) as Flow<ApiResponse<User>>






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


                response.body()?.let {

                    userDao.insertUser(
                        it.toEntity()
                    )

                    emit(
                        ApiResponse.Success(it)
                    )
                }


            }



        }catch(e: IOException){

            emit(ApiResponse.NetworkError)

        }
    }.flowOn(Dispatchers.IO) as Flow<ApiResponse<User>>







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

}
