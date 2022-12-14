package com.volum.volumematching.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.os.Handler
import android.os.Looper
import com.volum.volumematching.R

class GameViewModel: ViewModel() {

    private val _inGame = MutableLiveData<Boolean>(false)
    private val _isDone = MutableLiveData<Boolean>(false)
    private val _gameMode = MutableLiveData<GameModes>(GameModes.MODE_NONE)
    private val _gameSize = MutableLiveData<GameSize>(GameSize.SIZE_0)
    private val _gameImages = MutableLiveData<IntArray>()
    private val _gameRoundImages = MutableLiveData<IntArray>()
    private val _gameRoundImageStatus = MutableLiveData<BooleanArray>()
    private val _openedImages = MutableLiveData<Int>(0)
    private val _hasSelectedSlot1 = MutableLiveData<Int>(-1)
    private val _hasSelectedSlot2 = MutableLiveData<Int>(-1)
    private val _score = MutableLiveData<Int>(-1)
    private val _timeLeft = MutableLiveData<Int>(-1)

    private val imageArray = mutableListOf<Int>(
        R.drawable.ic_music01,
        R.drawable.ic_music02,
        R.drawable.ic_music03,
        R.drawable.ic_music04,
        R.drawable.ic_music05,
        R.drawable.ic_music06,
        R.drawable.ic_music07,
        R.drawable.ic_music08,
        R.drawable.ic_music09,
        R.drawable.ic_music10,
        R.drawable.ic_music11,
        R.drawable.ic_music12,
        R.drawable.ic_music13,
        R.drawable.ic_music14,
        R.drawable.ic_music15
    )

    val inGame: LiveData<Boolean> = _inGame
    val isDone: LiveData<Boolean> = _isDone
    val gameSize: LiveData<GameSize> = _gameSize
    val gameRoundImages: LiveData<IntArray> = _gameRoundImages
    val gameRoundImageStatus: LiveData<BooleanArray> = _gameRoundImageStatus
    val score: LiveData<Int> = _score
    val timeLeft: LiveData<Int> = _timeLeft

    init { }

    fun setGameMode(mode: GameModes) {
        if(_inGame.value!!) return
        _gameMode.value = mode
    }

    fun setGameSize(size: GameSize) {
        if(_inGame.value!!) return
        _gameSize.value = size
    }

    fun setUserData(score: Int, time: Int) {
        if(_gameMode.value!! != GameModes.MODE_MANIA) return
        _score.value = score
        _timeLeft.value = time
    }

    fun startGame() {
        if(_gameMode.value!! == GameModes.MODE_NONE) return
        if(_gameSize.value!! == GameSize.SIZE_0) return
        generateLevel()
        _inGame.value = true
    }

    fun stopGame() {
        _isDone.value = false
        _inGame.value = false
        _gameMode.value = GameModes.MODE_NONE
        _gameSize.value = GameSize.SIZE_0

        _gameImages.value = null
        _gameRoundImages.value = null
        _gameRoundImageStatus.value = null
        _openedImages.value = 0

        _hasSelectedSlot1.value = -1
        _hasSelectedSlot2.value = -1

        _score.value = 0
        _timeLeft.value = 0
    }

    fun checkOrSelect(box: Int) {
        var gameRIS = _gameRoundImageStatus.value!!

        if(_hasSelectedSlot1.value != -1 && _hasSelectedSlot2.value != -1) return
        if(gameRIS[box]) return

        if(_hasSelectedSlot1.value == -1) {
            _hasSelectedSlot1.value = box
            gameRIS[box] = true
            _gameRoundImageStatus.value = gameRIS
            return
        }

        if(_hasSelectedSlot2.value == -1) {
            _hasSelectedSlot2.value = box
            gameRIS[box] = true
            _gameRoundImageStatus.value = gameRIS
        }

        if(_hasSelectedSlot1.value != -1 && _hasSelectedSlot2.value != -1) {
            var gameRI = _gameRoundImages.value!!
            if(gameRI[_hasSelectedSlot1.value!!] == gameRI[_hasSelectedSlot2.value!!]) {

                _openedImages.value = _openedImages.value!! + 1

                _hasSelectedSlot1.value = -1
                _hasSelectedSlot2.value = -1
            } else {
                Handler(Looper.getMainLooper()).postDelayed({
                    gameRIS[_hasSelectedSlot1.value!!] = false
                    gameRIS[_hasSelectedSlot2.value!!] = false
                    _gameRoundImageStatus.value = gameRIS

                    _hasSelectedSlot1.value = -1
                    _hasSelectedSlot2.value = -1
                }, 500)
            }
        }

        if(_gameImages.value!!.isNotEmpty() && _openedImages.value!! == _gameImages.value!!.size) {
            if(_gameMode.value!! == GameModes.MODE_MANIA) {
                val levelWeight = when(_gameSize.value!!) {
                    GameSize.SIZE_1 -> 3
                    GameSize.SIZE_2 -> 4
                    GameSize.SIZE_3 -> 5
                    else -> 0
                }
                _score.value = _score.value!! + levelWeight
                _timeLeft.value = _timeLeft.value!! + 5
            }
            _isDone.value = true
            _inGame.value = false
        }
    }

    private fun generateLevel() {
        val uniqueItems = when(_gameSize.value!!) {
            GameSize.SIZE_1 -> 3
            GameSize.SIZE_2 -> 4
            GameSize.SIZE_3 -> 5
            else -> 0
        }

        val shuffledCards = imageArray.shuffled()
        _gameImages.value = shuffledCards.take(uniqueItems).toIntArray()

        val roundCards = _gameImages.value!! + _gameImages.value!!
        val shuffledRoundCards = roundCards.toMutableList().shuffled()
        _gameRoundImages.value = shuffledRoundCards.toIntArray()

        _gameRoundImageStatus.value = BooleanArray(uniqueItems * 2)
    }
}


enum class GameModes {
    MODE_NONE,
    MODE_ARCADE,
    MODE_MANIA
}

enum class GameSize {
    SIZE_0,
    SIZE_1,
    SIZE_2,
    SIZE_3
}