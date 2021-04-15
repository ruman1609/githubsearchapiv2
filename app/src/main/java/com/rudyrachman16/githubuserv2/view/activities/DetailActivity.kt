package com.rudyrachman16.githubuserv2.view.activities

import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.transition.Fade
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import com.rudyrachman16.githubuserv2.R
import com.rudyrachman16.githubuserv2.data.api.models.SearchUser
import com.rudyrachman16.githubuserv2.data.api.models.User
import com.rudyrachman16.githubuserv2.data.sqlite.FavUser
import com.rudyrachman16.githubuserv2.databinding.ActivityDetailBinding
import com.rudyrachman16.githubuserv2.view.StackWidget
import com.rudyrachman16.githubuserv2.view.adapters.ListAdapter
import com.rudyrachman16.githubuserv2.view.adapters.TabAdapter
import com.rudyrachman16.githubuserv2.view.viewmodel.DetailViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.text.NumberFormat
import kotlin.coroutines.CoroutineContext

class DetailActivity : AppCompatActivity(), CoroutineScope {
    companion object {
        @StringRes
        val tabTitle = intArrayOf(
            R.string.followers,
            R.string.following,
        )
    }

    override val coroutineContext: CoroutineContext = Dispatchers.Main

    private var followers: String? = null
    private var following: String? = null

    private var binding: ActivityDetailBinding? = null
    private val bind get() = binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(bind.root)
        val user: User = intent.getParcelableExtra(RecyclerListActivity.USER_KEY)

        setSupportActionBar(bind.toolbar.root)
        supportActionBar?.apply {
            setDisplayShowHomeEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }
        val fade = Fade().apply {
            excludeTarget(android.R.id.statusBarBackground, true)
            excludeTarget(android.R.id.navigationBarBackground, true)
        }
        window.enterTransition = fade
        window.exitTransition = fade

        title = user.id.toString()
        bind.username.text = user.username
        Glide.with(this)
            .load(user.picUrl)
            .apply(RequestOptions().override(100))
            .into(bind.imageView)

        val numberFormat = NumberFormat.getNumberInstance()
        val decimalFormat = (numberFormat as DecimalFormat).apply {
            isGroupingUsed = true
            applyPattern("#,###.#")
        }

        val detailViewModel: DetailViewModel by viewModels()

//        val detailViewModel = ViewModelProvider(
//            this,
//            ViewModelProvider.NewInstanceFactory()
//        ).get(DetailViewModel::class.java)

        detailViewModel.setDetail(user, applicationContext)
        detailViewModel.setFollowing(user, applicationContext)
        detailViewModel.setFollowers(user, applicationContext)
        detailViewModel.setFavButton(user, applicationContext)

        detailViewModel.getDetail().observe(this) {
            bind.loadingCount.visibility = View.GONE
            bind.loadingDetail.visibility = View.GONE
            bind.followers.text = groupNum(it.followers, decimalFormat)
            bind.following.text = groupNum(it.following, decimalFormat)
            bind.repos.text = groupNum(it.repo, decimalFormat)
            additionalInformation(it.location, bind.location)
            additionalInformation(it.company, bind.company)
            additionalInformation(it.name, bind.name)
            bind.url.setOnClickListener { _ ->
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(it.url)
                    )
                )
            }
        }
        detailViewModel.getFollowers().observe(this) {
            followers = Gson().toJson(it)
            setTab(followers, following)
        }
        detailViewModel.getFollowing().observe(this) {
            following = Gson().toJson(it)
            setTab(followers, following)
        }
        var fav: Boolean
        detailViewModel.getFav().observe(this) { isFav ->
            fav = isFav
            bind.buttonLoading.visibility = View.GONE
            bind.expand.show()
            setIcon(fav, bind.favorite, R.drawable.ic_favorite, R.drawable.ic_unfavorite)
            var clicked = false
            val margin = resources.getDimension(R.dimen.normal)
            bind.expand.setOnClickListener {
                if (!clicked) {
                    bind.favorite.show()
                    bind.share.show()
                    bind.favorite.translationY = -(bind.expand.customSize + margin)
                    bind.share.translationY =
                        -(bind.expand.customSize + bind.favorite.customSize + (margin * 2))
                    clicked = true
                } else {
                    bind.favorite.hide()
                    bind.share.hide()
                    bind.favorite.translationY = 0F
                    bind.share.translationY = 0F
                    clicked = false
                }
                setIcon(clicked, bind.expand, R.drawable.ic_close, R.drawable.ic_add)
            }
            bind.share.setOnClickListener {
                val intent = ListAdapter.share(
                    applicationContext,
                    SearchUser(user.id, user.username, user.picUrl)
                )
                startActivity(
                    Intent.createChooser(
                        intent,
                        getString(R.string.share_send)
                    )
                )
            }
            bind.favorite.setOnClickListener {
                if (fav) {
                    launch(Dispatchers.Main) {
                        val job1 = launch(Dispatchers.IO) {
                            val uri = Uri.parse("${FavUser.CONTENT_URI}/${user.id}")
                            contentResolver.delete(uri, null, null)
                        }
                        job1.join()
                        if (job1.isCompleted) {
                            Toast.makeText(
                                applicationContext,
                                getString(R.string.delete_succeed, user.username),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else {
                    launch(Dispatchers.Main) {
                        val job1 = launch(Dispatchers.IO) {
                            contentResolver.insert(FavUser.CONTENT_URI, ContentValues().apply {
                                put(FavUser.ID, user.id)
                                put(FavUser.USERNAME, user.username)
                                put(FavUser.PIC_URL, user.picUrl)
                            })
                        }
                        job1.join()
                        if (job1.isCompleted) {
                            Toast.makeText(
                                applicationContext,
                                getString(R.string.insert_succeed, user.username),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
                fav = !fav
                setIcon(fav, bind.favorite, R.drawable.ic_favorite, R.drawable.ic_unfavorite)
                StackWidget.updateWidget(applicationContext)
            }
        }
    }

    private fun setIcon(flag: Boolean, button: FloatingActionButton, vararg icon: Int) {
        button.setImageDrawable(
            ContextCompat.getDrawable(
                applicationContext,
                if (flag) icon[0] else icon[1]
            )
        )
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        menu.findItem(R.id.favorite).isVisible = false
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.locale) {
            startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setTab(followers: String?, following: String?) {
        if (following == null || followers == null) return
        bind.loadingFoll.visibility = View.GONE
        val tabAdapter = TabAdapter(this, arrayOf(followers, following))
        bind.viewPager2.adapter = tabAdapter
        TabLayoutMediator(bind.tabLayout, bind.viewPager2) { tabLayout, position ->
            tabLayout.text = getText(tabTitle[position])
        }.attach()  // JANGAN LUPA ATTACH
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (intent.getBooleanExtra(StackWidget.SEND, false)) {
            startActivity(Intent(this, RecyclerListActivity::class.java))
            finish()
        }
    }

    private fun bindNumber(num: Int, decimalFormat: DecimalFormat): String {
        var length: Int = num.toString().length - 1
        var result: Double = num.toDouble()
        if (length > 3) {
            var index = 1
            while (length >= 3) {
                index *= 1000
                length -= 3
            }
            result = num.toDouble() / index
        }
        return decimalFormat.format(result)
    }

    private fun groupNum(num: Int, decimalFormat: DecimalFormat): String = when {
        num.toString().length - 1 >= 9 -> getString(R.string.Bval, bindNumber(num, decimalFormat))
        num.toString().length - 1 in 6..8 -> getString(
            R.string.Mval,
            bindNumber(num, decimalFormat)
        )
        num.toString().length - 1 in 4..5 -> getString(
            R.string.Kval,
            bindNumber(num, decimalFormat)
        )
        else -> decimalFormat.format(num)
    }

    private fun additionalInformation(information: String?, text: TextView) {
        if (information == null) text.visibility = View.GONE
        else text.text = information
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}