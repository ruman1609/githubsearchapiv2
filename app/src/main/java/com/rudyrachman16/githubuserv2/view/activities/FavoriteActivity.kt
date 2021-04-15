package com.rudyrachman16.githubuserv2.view.activities

import android.app.ActivityOptions
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Pair
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.rudyrachman16.githubuserv2.R
import com.rudyrachman16.githubuserv2.data.api.models.SearchUser
import com.rudyrachman16.githubuserv2.data.sqlite.FavUser
import com.rudyrachman16.githubuserv2.databinding.ActivityFavoriteBinding
import com.rudyrachman16.githubuserv2.view.StackWidget
import com.rudyrachman16.githubuserv2.view.TouchCallback
import com.rudyrachman16.githubuserv2.view.adapters.ListAdapter
import com.rudyrachman16.githubuserv2.view.viewmodel.FavoriteViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class FavoriteActivity : AppCompatActivity(), CoroutineScope {
    private var binding: ActivityFavoriteBinding? = null
    private val bind get() = binding!!

    private var listAdapters: ListAdapter? = null
    private val listAdapter get() = listAdapters!!

    companion object {
        suspend fun favoriteShort(
            user: FavUser,
            fav: Boolean,
            pos: Int,
            listAdapter: ListAdapter,
            context: Context,
            consumer: Boolean,
            favoriteViewModel: FavoriteViewModel? = null
        ) {
            val coroutineContext: CoroutineContext = Dispatchers.Main
            val scope = CoroutineScope(coroutineContext)
            if (fav) {
                scope.launch(coroutineContext) {
                    val job = launch(Dispatchers.IO) {
                        val uri = Uri.parse("${FavUser.CONTENT_URI}/${user.id}")
                        context.contentResolver.delete(uri, null, null)
                    }
                    job.join()
                    if (job.isCompleted) {
                        if (consumer) {
                            favoriteViewModel?.setList(context)
                            listAdapter.notifyItemRangeChanged(pos, listAdapter.itemCount)
                        } else listAdapter.notifyItemChanged(pos)
                        Toast.makeText(
                            context,
                            context.getString(R.string.delete_succeed, user.username),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } else {
                scope.launch(coroutineContext) {
                    val job = launch(Dispatchers.IO) {
                        context.contentResolver.insert(
                            FavUser.CONTENT_URI,
                            ContentValues().apply {
                                put(FavUser.ID, user.id)
                                put(FavUser.USERNAME, user.username)
                                put(FavUser.PIC_URL, user.picUrl)
                            })
                    }
                    job.join()
                    if (job.isCompleted) {
                        if (consumer) listAdapter.notifyItemInserted(pos)
                        else listAdapter.notifyItemChanged(pos)
                        Toast.makeText(
                            context,
                            context.getString(R.string.insert_succeed, user.username),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
            StackWidget.updateWidget(context)
        }

        fun cursorMapping(it: Cursor): ArrayList<SearchUser> {
            val list = ArrayList<SearchUser>()
            while (it.moveToNext()) {
                it.apply {
                    list.add(
                        SearchUser(
                            getInt(getColumnIndexOrThrow(FavUser.ID)),
                            getString(getColumnIndexOrThrow(FavUser.USERNAME)),
                            getString(getColumnIndexOrThrow(FavUser.PIC_URL))
                        )
                    )
                }
            }
            it.close()
            return list
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoriteBinding.inflate(layoutInflater)
        setContentView(bind.root)
        setSupportActionBar(bind.toolbar.root)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeButtonEnabled(true)
        }
        title = getString(R.string.favorite_list)

        val viewModel: FavoriteViewModel by viewModels()

//        val viewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())
//            .get(FavoriteViewModel::class.java)

        viewModel.setList(applicationContext)

        listAdapters = ListAdapter(applicationContext, { user, fav, position ->
            launch(coroutineContext) {
                val short =
                    async {
                        favoriteShort(
                            user,
                            fav,
                            position,
                            listAdapter,
                            applicationContext,
                            true,
                            viewModel
                        )
                    }
                short.await()
            }
        }) { it1, bind ->
            val options = ActivityOptions.makeSceneTransitionAnimation(
                this@FavoriteActivity,
                Pair.create(bind.imageView, RecyclerListActivity.transitions[0]),
                Pair.create(bind.username, RecyclerListActivity.transitions[1])
            )
            val move = Intent(this, DetailActivity::class.java).apply {
                putExtra(RecyclerListActivity.USER_KEY, it1)
            }
            startActivity(move, options.toBundle())
        }
        bind.recycler.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(applicationContext)
            adapter = listAdapter
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                setOnScrollChangeListener { _, _, _, _, _ ->
                    listAdapter.closeMenu()
                }
            }
        }
        object :
            ItemTouchHelper(TouchCallback(listAdapter, applicationContext)) {}.attachToRecyclerView(
            bind.recycler
        )
        viewModel.getList().observe(this, {
            val list = cursorMapping(it)
            listAdapter.setList(list)
            bind.loadingFav.visibility = View.GONE
            if (list.isEmpty()) bind.status.visibility = View.VISIBLE
        })
    }

    override fun onBackPressed() {
        if (listAdapter.isMenuShown()) listAdapter.closeMenu()
        else super.onBackPressed()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        menu.findItem(R.id.favorite).isVisible = false
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.locale -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                true
            }
            R.id.settings -> {
                startActivity(Intent(this, SettingActivity::class.java))
                true
            }
            else -> false
        }
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main
}