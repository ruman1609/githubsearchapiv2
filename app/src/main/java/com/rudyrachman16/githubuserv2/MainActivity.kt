package com.rudyrachman16.githubuserv2

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.rudyrachman16.githubuserv2.databinding.ActivityMainBinding
import com.rudyrachman16.githubuserv2.view.activities.RecyclerListActivity

class MainActivity : AppCompatActivity() {
    var binding: ActivityMainBinding? = null
    val bind get() = binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bind.root)
        Handler().postDelayed({
            startActivity(
                Intent(this, RecyclerListActivity::class.java),
                ActivityOptions.makeSceneTransitionAnimation(this).toBundle()
            )
            finish()
        }, 2000)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}