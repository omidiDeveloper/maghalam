package com.example.maghalam.model.repository.article

import com.example.maghalam.model.data.Article
import com.example.maghalam.model.db.dao.ArticleDao
import com.example.maghalam.model.db.entity.toArticle
import com.example.maghalam.model.db.entity.toEntity
import com.example.maghalam.model.net.api.ApiService
import com.example.maghalam.model.net.dto.ApiResponse
import com.example.maghalam.model.net.dto.ArticleGenerationRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.io.IOException


class ArticleRepositoryImpl(
    private val articleApiService: ApiService,
    private val articleDao: ArticleDao
) : ArticleRepository {


    override fun getArticles(): Flow<ApiResponse<List<Article>>> {

        return flow {

            emit(ApiResponse.Loading)


            try {

                val response =
                    articleApiService.getArticles()


                if(response.isSuccessful){

                    val articles =
                        response.body().orEmpty()


                    articleDao.insertArticles(
                        articles.map {
                            it.toEntity()
                        }
                    )

                }


            } catch(e: IOException){

                // offline mode

            }


            articleDao.getAllArticles()
                .map { entities ->

                    ApiResponse.Success(
                        entities.map {
                            it.toArticle()
                        }
                    )

                }
                .collect { emit(it) }

        }
    }



    override fun getArticleById(
        id: Long
    ): Flow<ApiResponse<Article>> = flow {


        emit(ApiResponse.Loading)


        val local =
            articleDao.getArticleById(id)


        local?.let {

            emit(
                ApiResponse.Success(
                    it.toArticle()
                )
            )

        }


        try {

            val response =
                articleApiService.getArticleById(id)


            if(response.isSuccessful){

                response.body()?.let {

                    articleDao.insertArticle(
                        it.toEntity()
                    )

                    emit(
                        ApiResponse.Success(it)
                    )
                }

            }


        }catch(e: IOException){

            if(local == null){

                emit(
                    ApiResponse.Error(
                        -1,
                        "اینترنت در دسترس نیست"
                    )
                )
            }
        }

    }



    override fun deleteArticle(
        id: Long
    ): Flow<ApiResponse<Boolean>> = flow {


        emit(ApiResponse.Loading)


        try {


            val response =
                articleApiService.deleteArticle(id)


            if(response.isSuccessful){


                articleDao.deleteArticleById(id)


                emit(
                    ApiResponse.Success(true)
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


            articleDao.deleteArticleById(id)


            emit(
                ApiResponse.Success(true)
            )
            return@flow


            emit(
                ApiResponse.Error(
                    -1,
                    "اینترنت در دسترس نیست"
                )
            )

        }

    }




    override fun insertArticle(
        article: Article
    ): Flow<ApiResponse<Boolean>> = flow {


        emit(ApiResponse.Loading)

        articleDao.insertArticle(
            article.toEntity()
        )


        try {


            val response =
                articleApiService.insertArticle(article)



            if(response.isSuccessful){


                response.body()?.let {


                    articleDao.insertArticle(
                        it.toEntity()
                    )

                }


                emit(
                    ApiResponse.Success(true)
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


            emit(
                ApiResponse.Success(true)
            )
            return@flow


            emit(
                ApiResponse.Error(
                    -1,
                    "اینترنت در دسترس نیست"
                )
            )

        }

    }


    override fun generateArticle(
        request: ArticleGenerationRequest
    ): Flow<ApiResponse<Article>> = flow {


        emit(ApiResponse.Loading)


        try {


            val response =
                articleApiService.generateArticle(request)



            if(response.isSuccessful){


                response.body()?.let {


                    articleDao.insertArticle(
                        it.toEntity()
                    )


                    emit(
                        ApiResponse.Success(it)
                    )

                }



            }else{


                emit(
                    ApiResponse.Error(
                        response.code(),
                        response.message()
                    )
                )

            }



        }catch(e: IOException){


            emit(
                ApiResponse.Error(
                    -1,
                    "اینترنت در دسترس نیست"
                )
            )

        }

    }

//    override fun downloadArticle(article: Article) {
//    }


}
