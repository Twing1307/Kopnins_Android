package com.vyapp.doodle.model

data class Player(
    var positionX: Int,
    var positionY: Int,
    var viewWidth: Int,
    var viewHeight: Int
) {

    fun updatePosition(newPositionX: Int, newPositionY: Int){
        positionX = newPositionX
        positionY = newPositionY
    }

}
