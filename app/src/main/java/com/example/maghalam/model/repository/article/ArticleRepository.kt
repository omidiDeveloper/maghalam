import com.example.maghalam.model.data.Article
import com.example.maghalam.model.net.api.ApiResponse
import com.example.maghalam.model.net.dto.request.ArticleGenerationRequest
import com.example.maghalam.model.net.dto.response.ArticleResponse
import kotlinx.coroutines.flow.Flow

interface ArticleRepository {
    fun getArticles(page: Int, size: Int): Flow<ApiResponse<List<Article>>>
    fun getArticleById(id: Long): Flow<ApiResponse<Article>>
    fun searchArticles(query: String, page: Int, size: Int): Flow<ApiResponse<List<Article>>>
    fun createArticle(title: String, content: String, summary: String?, keywords: String?): Flow<ApiResponse<Article>>
//    fun updateArticle(id: Long, title: String?, content: String?, summary: String?, keywords: String?): Flow<ApiResponse<Article>>
//    fun deleteArticle(id: Long): Flow<ApiResponse<Unit>>
    fun generateArticle(request: ArticleGenerationRequest): Flow<ApiResponse<Article>>
}