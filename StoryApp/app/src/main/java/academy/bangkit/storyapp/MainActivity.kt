package academy.bangkit.storyapp

import academy.bangkit.storyapp.databinding.ActivityMainBinding
import academy.bangkit.storyapp.login.preferences.LoginPreferences
import academy.bangkit.storyapp.ui.HomeActivity
import academy.bangkit.storyapp.ui.LoginActivity
import academy.bangkit.storyapp.ui.RegisterActivity
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    private lateinit var loginPreferences: LoginPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loginPreferences = LoginPreferences.getInstance(applicationContext)

        lifecycleScope.launch {
            showLoading(true)
            loginPreferences.getLoginStatus().collect { isLoggedIn ->
                if (isLoggedIn) {
                    Toast.makeText(this@MainActivity, "Anda sudah login", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@MainActivity, HomeActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                showLoading(false)
            }
        }
        val textView = binding.textView
        val logo = binding.logoApp
        val btnLogin = binding.buttonLogin
        val btnRegister = binding.buttonRegister

        val textAnim = ObjectAnimator.ofFloat(textView, "translationY", -300f, 0f)
        textAnim.duration = 1000
        val logoAnim = ObjectAnimator.ofFloat(logo, "alpha", 0f, 1f)
        logoAnim.duration = 1000
        val buttonLoginAnim = ObjectAnimator.ofFloat(btnLogin, "alpha", 0f, 1f)
        buttonLoginAnim.duration = 1000
        val buttonRegisterAnim = ObjectAnimator.ofFloat(btnRegister, "alpha", 0f, 1f)
        buttonRegisterAnim.duration = 1000

        val animatorSet = AnimatorSet()
        animatorSet.playSequentially(textAnim, logoAnim, buttonLoginAnim, buttonRegisterAnim)
        animatorSet.start()




        binding.buttonRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            val options = ActivityOptions.makeSceneTransitionAnimation(
                this,
                logo, "logoTransition"
            )

            startActivity(intent, options.toBundle())
        }
        binding.buttonLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            val options = ActivityOptions.makeSceneTransitionAnimation(
                this,
                logo, "logoTransition"
            )
            startActivity(intent, options.toBundle())
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
