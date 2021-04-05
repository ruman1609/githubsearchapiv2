package com.rudyrachman16.githubuserv2.view.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rudyrachman16.githubuserv2.data.DataRepos
import com.rudyrachman16.githubuserv2.data.api.models.SearchUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

class ListViewModel : ViewModel() {
    private val list = MutableLiveData<ArrayList<SearchUser>>()

    private var jobTemp: Job? = null
    private lateinit var dataRepos: DataRepos

    suspend fun setList(context: Context, newText: String) {
        dataRepos = DataRepos(context)
        if (newText != "") {
            jobTemp?.cancel()
            val job = viewModelScope.launch {
                try {
                    val result = withContext(Dispatchers.IO) { dataRepos.getSearch(newText) }
                    list.postValue(result.items)
                } catch (e: Throwable) {
                    e.printStackTrace()
                    when (e) {
                        is IOException -> {
                            Toast.makeText(
                                context,
                                "Error occurred\nNetwork Error",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        is HttpException -> {
                            Toast.makeText(
                                context,
                                "Error occurred\nHttp ${e.code()}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            }
            jobTemp = job
            job.join()
        }
    }

    fun getList(): LiveData<ArrayList<SearchUser>> = list
}