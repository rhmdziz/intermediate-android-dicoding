package academy.bangkit.storyapp.story

import academy.bangkit.storyapp.login.preferences.LoginPreferences
import academy.bangkit.storyapp.story.api.ApiConfig
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.liveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first

class StoryViewModel(application: Application) : AndroidViewModel(application) {

    private val loginPreferences = LoginPreferences.getInstance(application)

    fun getStories() = liveData(Dispatchers.IO) {
        val token = loginPreferences.getToken().first()
        try {
            val response = ApiConfig.getApiService(token ?: "").getStories()
            if (response.error == false) {
                emit(response.listStory)
            } else {
                emit(emptyList())
            }
        } catch (e: Exception) {
            emit(emptyList())
        }
    }
}