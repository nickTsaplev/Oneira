package com.lesterade.oneira

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lesterade.oneira.gameHandling.GameMaster

class GameMasterViewModel: ViewModel() {
    val master = MutableLiveData<GameMaster>()
}