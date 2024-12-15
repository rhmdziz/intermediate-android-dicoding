package academy.bangkit.storyapp.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity


import academy.bangkit.storyapp.R
import academy.bangkit.storyapp.databinding.ActivityHomeBinding
import academy.bangkit.storyapp.login.preferences.LoginPreferences
import academy.bangkit.storyapp.story.StoryAdapter
import academy.bangkit.storyapp.story.StoryViewModel
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.core.graphics.drawable.DrawableCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class HomeActivity : AppCompatActivity() {
    private var _binding: ActivityHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var loginPreferences: LoginPreferences
    private val storyViewModel: StoryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loginPreferences = LoginPreferences.getInstance(applicationContext)


        val toolbar: Toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = getString(R.string.app_name)
            setDisplayHomeAsUpEnabled(false)
        }
        toolbar.setTitleTextColor(Color.WHITE)
        val overflowIcon: Drawable? = toolbar.overflowIcon
        overflowIcon?.let {
            DrawableCompat.setTint(it, Color.WHITE)
        }

        if (isNetworkAvailable()) {
            storyViewModel.getStories().observe(this, Observer { stories ->
                if (stories != null) {
                    val adapter = StoryAdapter(stories)
                    binding.rvHome.layoutManager = LinearLayoutManager(this)
                    binding.rvHome.adapter = adapter
                } else {
                    binding.tvError.visibility = View.VISIBLE
                    Toast.makeText(this, getString(R.string.intro_register), Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            binding.tvError.visibility = View.VISIBLE
            Toast.makeText(this, getString(R.string.error_connection), Toast.LENGTH_SHORT).show()
        }

        binding.fabAddStory.setOnClickListener {
            val intent = Intent(this, AddStoryActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_home, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_language -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                Toast.makeText(this, "Pilih Bahasa Inggris / Indonesia", Toast.LENGTH_SHORT).show()
                return true
            }
            R.id.action_logout -> {
                MainScope().launch {
                    loginPreferences.clearSession()
                    Toast.makeText(this@HomeActivity, getString(R.string.logout), Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@HomeActivity, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
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
    override fun onResume() {
        super.onResume()
        storyViewModel.getStories()
    }
}