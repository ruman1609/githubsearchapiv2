package com.rudyrachman16.githubuserv2.view.viewmodel

import android.content.Context
import android.database.Cursor
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rudyrachman16.githubuserv2.data.sqlite.FavUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FavoriteViewModel : ViewModel() {

    private val list = MutableLiveData<Cursor>()

    fun setList(context: Context) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                context.contentResolver.query(FavUser.CONTENT_URI, null, null, null, null, null)
            }
            list.postValue(result!!)
        }
    }

    fun getList(): LiveData<Cursor> = list
}