package com.rudyrachman16.favoritegithubuser.view.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.rudyrachman16.favoritegithubuser.R
import com.rudyrachman16.favoritegithubuser.databinding.ActivitySettingBinding
import com.rudyrachman16.favoritegithubuser.view.fragments.SettingFragment

class SettingActivity : AppCompatActivity() {
    private var binding: ActivitySettingBinding? = null
    private val bind get() = binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(bind.root)

        title = getString(R.string.settings)
        setSupportActionBar(bind.toolbar.root)
        supportActionBar?.apply {
            setHomeButtonEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }

        supportFragmentManager.commit {
            add(R.id.fragment, SettingFragment(), SettingFragment::class.java.simpleName)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}