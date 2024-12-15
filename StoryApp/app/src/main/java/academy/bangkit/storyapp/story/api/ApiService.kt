package academy.bangkit.storyapp.story.api

import retrofit2.http.GET

interface ApiService {
    @GET("stories")
    suspend fun getStories(): StoryResponse
}