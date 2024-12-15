package academy.bangkit.storyapp.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import academy.bangkit.storyapp.databinding.ActivityDetailBinding
import academy.bangkit.storyapp.story.api.ListStoryItem
import android.graphics.Color
import androidx.appcompat.widget.Toolbar
import androidx.core.graphics.drawable.DrawableCompat
import com.bumptech.glide.Glide

class DetailActivity : AppCompatActivity() {
    private var _binding: ActivityDetailBinding? = null
    private val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val toolbar: Toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            val story = intent.getParcelableExtra<ListStoryItem>("story")
            story?.let {
                title = "Story from ${it.name}"
            }
            setDisplayHomeAsUpEnabled(true)
            setHomeButtonEnabled(true)
        }
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        toolbar.navigationIcon?.let {
            DrawableCompat.setTint(it, Color.WHITE)
        }
        toolbar.setTitleTextColor(Color.WHITE)


        val story = intent.getParcelableExtra<ListStoryItem>("story")

        story?.let {
            binding.tvDetailName.text = it.name
            binding.tvDetailDescription.text = it.description

            Glide.with(this)
                .load(it.photoUrl)
                .into(binding.ivDetailPhoto)
        }

    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}