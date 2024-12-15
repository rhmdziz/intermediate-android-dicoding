package academy.bangkit.storyapp.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import academy.bangkit.storyapp.R
import academy.bangkit.storyapp.databinding.ActivityRegisterBinding
import academy.bangkit.storyapp.register.api.ApiConfig
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.graphics.drawable.DrawableCompat
import com.google.android.material.transition.platform.MaterialContainerTransform
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterActivity : AppCompatActivity() {
    private var _binding: ActivityRegisterBinding? = null
    private val binding get() = _binding!!

    private lateinit var edRegisterName: EditText
    private lateinit var edRegisterEmail: EditText
    private lateinit var edRegisterPassword: EditText
    private lateinit var buttonRegister: Button
    private lateinit var logo: ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityRegisterBinding.inflate(layoutInflater)
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
            title = getString(R.string.register)
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

        edRegisterName = binding.edRegisterName
        edRegisterEmail = binding.edRegisterEmail
        edRegisterPassword = binding.edRegisterPassword
        buttonRegister = binding.buttonRegister

        binding.buttonRegister.setOnClickListener {
            val name = binding.edRegisterName.text.toString().trim()
            val email = binding.edRegisterEmail.text.toString().trim()
            val password = binding.edRegisterPassword.text.toString().trim()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, getString(R.string.intro_register), Toast.LENGTH_SHORT).show()
            } else {
                showLoading(true)
                registerUser(name, email, password)
            }
        }

        binding.textViewLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun registerUser(name: String, email: String, password: String) {
        CoroutineScope(Dispatchers.Main).launch {
            if (!isNetworkAvailable()) {
                Toast.makeText(this@RegisterActivity, getString(R.string.error_connection), Toast.LENGTH_SHORT).show()
                showLoading(false)
                return@launch
            }
            try {
                val response = withContext(Dispatchers.IO) {
                    ApiConfig.getApiService().register(name, email, password)
                }
                if (response.error == false) {
                    Toast.makeText(this@RegisterActivity, "Registration Successful", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this@RegisterActivity, response.message ?: "Unknown error", Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {
                Toast.makeText(this@RegisterActivity, "Registration failed: ${e.message}", Toast.LENGTH_SHORT).show()
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


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}