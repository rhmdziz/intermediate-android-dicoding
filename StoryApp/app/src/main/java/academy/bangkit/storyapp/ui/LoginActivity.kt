package academy.bangkit.storyapp.ui

import academy.bangkit.storyapp.R
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import academy.bangkit.storyapp.databinding.ActivityLoginBinding
import academy.bangkit.storyapp.login.api.ApiConfig
import academy.bangkit.storyapp.login.preferences.LoginPreferences
import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.graphics.drawable.DrawableCompat
import com.google.android.material.transition.platform.MaterialContainerTransform
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {

    private var _binding: ActivityLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var edLoginEmail: EditText
    private lateinit var edLoginPassword: EditText
    private lateinit var buttonLogin: Button
    private lateinit var loginPreferences: LoginPreferences

    private lateinit var logo: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        logo = binding.logoApp
        val materialContainerTransform = MaterialContainerTransform().apply {
            duration = 500
            startView = logo
            endView = logo
            scrimColor = Color.TRANSPARENT
            fadeMode = MaterialContainerTransform.FADE_MODE_IN
        }

        window.sharedElementEnterTransition = materialContainerTransform

        val toolbar: Toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = getString(R.string.login)
            setDisplayHomeAsUpEnabled(false)
        }
        val drawable: Drawable? = toolbar.navigationIcon
        drawable?.let {
            DrawableCompat.setTint(it, Color.WHITE)
            toolbar.navigationIcon = it
        }
        toolbar.setTitleTextColor(Color.WHITE)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }


        edLoginEmail = binding.edLoginEmail
        edLoginPassword = binding.edLoginPassword
        buttonLogin = binding.buttonLogin

        loginPreferences = LoginPreferences.getInstance(applicationContext)

        buttonLogin.setOnClickListener {
            val email = edLoginEmail.text.toString().trim()
            val password = edLoginPassword.text.toString().trim()

            // Validasi input
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, getString(R.string.required_data), Toast.LENGTH_SHORT).show()
            } else {
                showLoading(true)
                loginUser(email, password)
            }
        }

        binding.textViewRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    private fun loginUser(email: String, password: String) {

        CoroutineScope(Dispatchers.Main).launch {
            if (!isNetworkAvailable()) {
                showToast(getString(R.string.error_connection))
                showLoading(false)
                return@launch
            }

            try {
                val response = withContext(Dispatchers.IO) {
                    ApiConfig.getApiService().login(email, password)
                }

                if (response.error == false && response.loginResult != null) {
                    val loginResult = response.loginResult
                    loginPreferences.saveSession(true, loginResult.token ?: "")

                    Toast.makeText(this@LoginActivity, "Login Successful", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this@LoginActivity, response.message ?: "Unknown error", Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {
                Toast.makeText(this@LoginActivity, "Login failed: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                showLoading(false)
            }
        }

    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
    @SuppressLint("ServiceCast")
    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
        return networkCapabilities != null && networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
    private fun showToast(message: String?) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}