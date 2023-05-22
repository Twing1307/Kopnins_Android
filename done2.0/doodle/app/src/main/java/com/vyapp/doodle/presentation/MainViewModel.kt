package com.vyapp.doodle.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vyapp.doodle.data.ScoreEntity
import com.vyapp.doodle.data.ScoreRepository

class MainViewModel : ViewModel() {

    private val repository = ScoreRepository.get()

    var isOver: MutableLiveData<Boolean> = MutableLiveData()

    private var _nameLiveData: MutableLiveData<String> = MutableLiveData()
    var nameLiveData: LiveData<String> = _nameLiveData
    var isAcc = false

    var scoreLiveData: LiveData<ScoreEntity?> = repository.getValue(0)

    fun setName(name: String){
        _nameLiveData.value = name
    }

    fun setOver(state: Boolean){
        isOver.value = state
    }


}