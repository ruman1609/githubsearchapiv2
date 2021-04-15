package com.rudyrachman16.githubuserv2.view.activities

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Pair
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.rudyrachman16.githubuserv2.R
import com.rudyrachman16.githubuserv2.data.api.models.SearchUser
import com.rudyrachman16.githubuserv2.databinding.ActivityRecyclerListBinding
import com.rudyrachman16.githubuserv2.view.TouchCallback
import com.rudyrachman16.githubuserv2.view.adapters.ListAdapter
import com.rudyrachman16.githubuserv2.view.viewmodel.ListViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class RecyclerListActivity : AppCompatActivity(), CoroutineScope {
    private var binding: ActivityRecyclerListBinding? = null
    private val bind get() = binding!!

    private var listAdaptered: ListAdapter? = null
    private val listAdapter get() = listAdaptered!!
    private val list = ArrayList<SearchUser>()

    //    private var listViewModeler: ListViewModel? = null
    private val listViewModel: ListViewModel by viewModels()

    companion object {
        @JvmStatic
        private var empty = true
        val transitions = arrayOf(
            "pictureTransition",
            "usernameTransition"
        )
        const val USER_KEY = "user"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecyclerListBinding.inflate(layoutInflater)
        setContentView(bind.root)
        title = getString(R.string.github_search)
        setSupportActionBar(bind.toolbar.root)

        bind.information.visibility = View.VISIBLE

        bind.userSearch.addTextChangedListener {
            bind.information.visibility = View.GONE
            changeTextListener(it.toString())
        }
        bind.userSearch.setOnEditorActionListener { _, actID, _ ->
            if (actID == EditorInfo.IME_ACTION_SEARCH) {
                val imm =
                    getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(bind.userSearch.windowToken, 0)
                bind.userSearch.clearFocus()
                true
            } else false
        }

//        listViewModeler = ViewModelProvider(
//            this,
//            ViewModelProvider.NewInstanceFactory()
//        ).get(ListViewModel::class.java)
        listAdaptered = ListAdapter(this@RecyclerListActivity, { user, fav, position ->
            launch(coroutineContext) {
                val short = async {
                    FavoriteActivity.favoriteShort(
                        user,
                        fav,
                        position,
                        listAdapter,
                        applicationContext,
                        false
                    )
                }
                short.await()
            }
        }) { it1, bind ->
            listAdapter.closeMenu()
            val options = ActivityOptions.makeSceneTransitionAnimation(
                this@RecyclerListActivity,
                Pair.create(bind.imageView, transitions[0]),
                Pair.create(bind.username, transitions[1])
            )
            val move = Intent(this, DetailActivity::class.java).apply {
                putExtra(USER_KEY, it1)
            }
            startActivity(move, options.toBundle())
        }

        bind.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@RecyclerListActivity)
            setHasFixedSize(true)
            adapter = listAdapter
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                setOnScrollChangeListener { _, _, _, _, _ ->
                    listAdapter.closeMenu()
                }
            }
        }
        listAdapter.notifyDataSetChanged()

        object :
            ItemTouchHelper(TouchCallback(listAdapter, applicationContext)) {}.attachToRecyclerView(
            bind.recyclerView
        )

        if (empty) changeInformation(R.color.black)
        listViewModel.getList().observe(this@RecyclerListActivity) {  // Observe must at onCreate()
            if (!empty) {
                list.addAll(it)
                if (list.isEmpty()) changeInformation(R.color.red)
                else listAdapter.setList(list)
                bind.loading.visibility = View.GONE
            } else changeInformation(R.color.black)
        }
    }

    override fun onBackPressed() {
        if (listAdapter.isMenuShown()) listAdapter.closeMenu()
        else super.onBackPressed()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
        listAdaptered = null
//        listViewModeler = null
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        listAdapter.closeMenu()
        when (item.itemId) {
            R.id.locale -> startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
            R.id.favorite -> startActivity(Intent(this, FavoriteActivity::class.java))
            R.id.settings -> startActivity(Intent(this, SettingActivity::class.java))
        }
        return true
    }

    private fun changeInformation(color: Int) {
        bind.information.visibility = View.VISIBLE
        bind.information.text = if (color == R.color.black) getString(R.string.information)
        else getString(R.string.not_found)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            bind.information.setTextColor(
                resources.getColorStateList(
                    color,
                    null
                )
            )
        else
            bind.information.setTextColor(resources.getColor(color))
    }

    private fun changeTextListener(newText: String) {
        if (newText.isEmpty()) clearList()
        else {
            empty = false
            list.clear()
            listAdapter.setList(list)
            bind.loading.visibility = View.VISIBLE
            launch {
                listViewModel.setList(this@RecyclerListActivity, newText)
            }
        }
    }

    private fun clearList() {
        bind.loading.visibility = View.GONE
        changeInformation(R.color.black)
        list.clear()
        listAdapter.setList(list)
        empty = true
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main
}