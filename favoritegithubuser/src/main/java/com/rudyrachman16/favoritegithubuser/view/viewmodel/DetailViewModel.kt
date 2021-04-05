package com.rudyrachman16.favoritegithubuser.view.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rudyrachman16.favoritegithubuser.data.api.models.DetailUser
import com.rudyrachman16.favoritegithubuser.data.api.models.SearchUser
import com.rudyrachman16.favoritegithubuser.data.api.models.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

class DetailViewModel : ViewModel() {
    private val detailUser = MutableLiveData<DetailUser>()
    private val following = MutableLiveData<ArrayList<SearchUser>>()
    private val followers = MutableLiveData<ArrayList<SearchUser>>()

    fun setDetail(user: User, context: Context) {
        viewModelScope.launch(Dispatchers.Main) {
            try {
                val result = withContext(Dispatchers.IO) { user.getDetail() }
                detailUser.postValue(result)
            } catch (e: Throwable) {
                e.printStackTrace()
                throwable(e, context)
            }
        }
    }

    fun setFollowing(user: User, context: Context) {
        viewModelScope.launch(Dispatchers.Main) {
            try {
                val result = withContext(Dispatchers.IO) { user.getFollowing() }
                following.postValue(result)
            } catch (e: Throwable) {
                e.printStackTrace()
                throwable(e, context)
            }
        }
    }

    fun setFollowers(user: User, context: Context) {
        viewModelScope.launch(Dispatchers.Main) {
            try {
                val result = withContext(Dispatchers.IO) { user.getFollowers() }
                followers.postValue(result)
            } catch (e: Throwable) {
                e.printStackTrace()
                throwable(e, context)
            }
        }
    }

    private fun throwable(e: Throwable, context: Context) {
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

    fun getDetail() = detailUser
    fun getFollowers() = followers
    fun getFollowing() = following
}