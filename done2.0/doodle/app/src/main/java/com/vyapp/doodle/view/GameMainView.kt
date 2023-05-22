package com.vyapp.doodle.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.vyapp.doodle.R
import com.vyapp.doodle.data.GlobalScore
import com.vyapp.doodle.data.ScoreRepository
import com.vyapp.doodle.model.Player
import java.util.*

private const val CREATE_BRICK_TIME = 2000
private const val PLAYER_MOVE_SPEED = 40

/*
class takes in a Context, AttributeSet, and defStyleAttr as parameters.
It is used to create instances of the GameMainView class and initialize
its properties. The @JvmOverloads annotation generates additional constructors
 with default parameter values to simplify the instantiation of the class.
*/
class GameMainView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : SurfaceView(context, attrs, defStyleAttr), Runnable {


    private var brickMove = 7 // represents the speed at which the bricks move.

    private var surfaceHolder: SurfaceHolder = holder //is an instance of SurfaceHolder that provides access and control over the underlying surface of the SurfaceView.
    private var paint: Paint = Paint() //is an instance of the Paint class used for drawing and styling the game elements.
    private lateinit var gameThread: Thread // is a thread used for running the game loop.
    private var running = false // is a boolean flag that indicates whether the game is currently running.
    var over = false // is a boolean flag that indicates whether the game is over.
    private var viewWidth = 0 //store the dimensions of the view.
    private var viewHeight = 0
    private var buttonOffset = 0 // represents the horizontal offset for buttons.
    private lateinit var backgroundBitmap: Bitmap
    private lateinit var playerBitmap: Bitmap
    private lateinit var moveLeftBitmap: Bitmap // representing different images used in the game.
    private lateinit var moveRightBitmap: Bitmap
    private var brick: Queue<Rect> // is a queue that stores rectangles representing bricks in the game.
    var movePlayerRight = false //are boolean flags indicating whether the player is moving right or left.
    var movePlayerLeft = false
    private var buttonY = 0
    private var leftButtonX = 0 // store the coordinates of the buttons on the view.
    private var rightButtonX = 0
    private var prevTimeSpawn: Long = 0 //store the previous timestamps for spawning bricks and moving them, respectively.
    private var prevTimeMoved: Long = 0

    lateinit var player: Player
    private var score: Long = 0 // stores the player's score.

    private val repository: ScoreRepository by lazy { // used for managing and updating the score.
        ScoreRepository.get()
    }

    init {
        paint.setColor(Color.DKGRAY)
        brick = LinkedList()
    }

    /*
    The class contains various methods for controlling the game, such as run(), removeBrick(), createBrick(),
     moveBrick(), and isPlayerCollided(). It also overrides onSizeChanged() to handle changes
      in the view size, and onTouchEvent() to handle touch events.
     */
    override fun run() {
        val startTime = System.currentTimeMillis() // It starts by capturing the current system time as the startTime for measuring the score and initializing
        var prevTimeDifficultyIncreased = startTime // the prevTimeDifficultyIncreased variable.
        var canvas: Canvas
        while (running) {
            if (surfaceHolder.surface.isValid) { // It checks if the surface of the SurfaceView is valid before proceeding to render the game elements.
                canvas = surfaceHolder.lockCanvas() // It locks the Canvas object from the SurfaceHolder to start drawing on the surface.
                canvas.save() // It saves the current state of the canvas.
                canvas.drawBitmap(backgroundBitmap, 0f, 0f, paint) // It draws the background bitmap on the canvas at the position (0, 0).

                if (System.currentTimeMillis() - prevTimeDifficultyIncreased > 1000) { //It checks if a certain time interval (1000 milliseconds)
                    brickMove += 1 // has passed since the last increase in difficulty. If so, it increments the brickMove variable, which controls the speed of the bricks.
                    prevTimeDifficultyIncreased = System.currentTimeMillis()
                }
                createBrick() // It calls the createBrick() method to create new bricks.
                moveBrick() // It calls the moveBrick() method to move the existing bricks.

                for (obstacle in brick) { // It iterates over each brick in the brick queue and draws them on the canvas using a gray color.
                    paint.color = Color.DKGRAY
                    canvas.drawRect(obstacle, paint)
                }
                removeBrick() // It calls the removeBrick() method to remove bricks that have moved out of the view.
                val textPaint = Paint() // It creates a Paint object called textPaint for drawing the score text.
                textPaint.color = resources.getColor(R.color.black)
                textPaint.textSize = 55f

                score = (System.currentTimeMillis() - startTime) / 10 //It calculates the current score based on the time elapsed since the game started.
                val scoreTxt = "Score: $score"
                canvas.drawText( // It draws the score text on the canvas at a specific position.
                    scoreTxt,
                    (viewWidth - viewWidth / 3).toFloat(),
                    (viewHeight / 10).toFloat(),
                    textPaint
                )
                if (movePlayerRight) { //If the movePlayerRight flag is true, it updates the player's position by moving it to the right.
                    var newX: Int
                    if (player.positionX + player.viewWidth + PLAYER_MOVE_SPEED > viewWidth) { //If the new position exceeds the view width, it adjusts it to ensure the player remains within the view.
                        newX = viewWidth - player.viewWidth
                    } else {
                        newX = player.positionX + PLAYER_MOVE_SPEED
                    }
                    player.updatePosition(newX, player.positionY)
                } else if (movePlayerLeft) { //If the movePlayerLeft flag is true, it updates the player's position by moving it to the left.
                    val newX =
                        if (player.positionX - PLAYER_MOVE_SPEED > 0) //If the new position is less than 0, it sets it to 0 to prevent the player from going off-screen.
                            player.positionX - PLAYER_MOVE_SPEED
                        else 0
                    player.updatePosition(newX, player.positionY)
                }

                playerBitmap.let { //It draws the player bitmap on the canvas at the player's current position.
                    canvas.drawBitmap(
                        it,
                        player.positionX.toFloat(),
                        player.positionY.toFloat(),
                        paint
                    )
                }

                buttonY = viewHeight - (moveLeftBitmap.height + buttonOffset) //It sets the positions for the left and right buttons used for player movement.
                leftButtonX = buttonOffset
                rightButtonX = viewWidth - (moveRightBitmap.width + buttonOffset)
                canvas.drawBitmap(moveLeftBitmap, leftButtonX.toFloat(), buttonY.toFloat(), paint)
                canvas.drawBitmap( //It draws the left and right button bitmaps on the canvas.
                    moveRightBitmap,
                    rightButtonX.toFloat(),
                    buttonY.toFloat(),
                    paint
                )
                if (isPlayerCollided()) {   //It checks if the player has collided with any of the bricks using the
                                            // isPlayerCollided() method. If a collision is detected, it updates the high score if the current score is greater
                                            // than the existing global score, sets the over flag to true, and stops the game loop by setting running to false.

                    if (score >= GlobalScore.score) {
                        repository.updateScore(score)
                    }

                    over = true
                    running = false

                }
                canvas.restore() //It restores the canvas to its previous state.
                surfaceHolder.unlockCanvasAndPost(canvas) //It unlocks the canvas and posts it, allowing it to be displayed on the surface.
            }
        }
    }

    /*
    This function is responsible for removing bricks from the brick queue that have moved out of the view.
    It checks the top position of each brick and removes it from the queue if it is below the view height.
    */
    private fun removeBrick() {
        while (brick.peek() != null) {
            if (brick.peek()!!.top > viewHeight) {
                brick.remove()
            } else {
                break
            }
        }
    }
    /*
    This function is called periodically to create new bricks.
    It checks if a certain time interval (CREATE_BRICK_TIME) has passed since the last brick creation.
    If the condition is met, it generates random values for the left and right positions of the brick and adds a new Rect object representing the brick to the brick queue.
     */
    private fun createBrick() {
        val currentTime = System.currentTimeMillis()
        if (currentTime - prevTimeSpawn > CREATE_BRICK_TIME) {
            prevTimeSpawn = currentTime
            val left = (Math.random() * 500).toInt()
            var right = (left + Math.random() * 500).toInt()
            right =
                if (right - left < 150) right * 2 else right
            brick.add(Rect(left, 0, right, 50))
        }
    }

    /*
    This function is responsible for moving the existing bricks downward.
    It is called periodically at a frequency of 60 times per second.
    It iterates over each brick in the brick queue and updates their positions by adding the brickMove value to their top and bottom coordinates.
    This effectively moves the bricks vertically downward.
     */
    private fun moveBrick() {

        val currentTime = System.currentTimeMillis()
        if (System.currentTimeMillis() - prevTimeMoved > 1000 / 60) {
            prevTimeMoved = currentTime
            for (obstacle in brick) {
                obstacle[obstacle.left, obstacle.top + brickMove, obstacle.right] =
                    obstacle.bottom + brickMove
            }
        }
    }

    /*
    This function checks if the player has collided with any of the bricks.
    It iterates over each brick in the brick queue and uses the intersect() method of the Rect class to check if
     there is an intersection between the player's bounding rectangle and the brick's rectangle. If a collision is detected,
     it returns true; otherwise, it returns false.
     */
    private fun isPlayerCollided(): Boolean {
        for (obstacle in brick) {
            if (obstacle.intersect(
                    player.positionX,
                    player.positionY,
                    player.positionX + player.viewWidth,
                    player.positionY + player.viewHeight
                )
            ) {

                return true
            }
        }
        return false
    }

    /*
    This function is called when the size of the game view changes.
    It is responsible for initializing various properties and resources based on the new dimensions of the view.
    It sets the viewWidth and viewHeight variables to the new width and height values, respectively.
    It also loads and assigns bitmap resources for the background, left button, right button, and player.
    The player object is created and positioned at the center of the screen, slightly above the buttons.
     */
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(viewWidth, viewHeight, oldw, oldh)
        viewWidth = w
        viewHeight = h
        backgroundBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.backgound)
        moveLeftBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.left_btn_img)
        moveRightBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.right_btn_img)
        buttonOffset = viewWidth / 15

        playerBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.player_img)
        val playerLocationX = (viewWidth + playerBitmap.getWidth()) / 2
        val playerLocationY =
            viewHeight - (buttonOffset * 2 + moveLeftBitmap.getHeight() + playerBitmap.getHeight())
        player = Player(
            playerLocationX,
            playerLocationY,
            playerBitmap.getWidth(),
            playerBitmap.getHeight()
        )

    }

    /*
    This function handles touch events on the game view.
    It is called when the user interacts with the view, such as touching the screen.
    It extracts the coordinates of the touch event and checks if the touch occurred within the boundaries of the left or right buttons.
     Depending on the touch action (down or up), it sets the movePlayerLeft or movePlayerRight flags accordingly.
     The invalidate() method is called to trigger a redraw of the view.
     */
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x.toInt()
        val y = event.y.toInt()

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {

                if (x > leftButtonX && x < leftButtonX + moveLeftBitmap.width
                    && y > buttonY && y < buttonY + moveLeftBitmap.width
                ) {
                    movePlayerLeft = true
                }

                if (x > rightButtonX && x < rightButtonX + moveRightBitmap.width
                    && y > buttonY && y < buttonY + moveRightBitmap.width
                ) {
                    movePlayerRight = true
                }

                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                movePlayerRight = false
                movePlayerLeft = false
                invalidate()
            }
        }
        return true
    }

    /*
    This function is called to pause the game.
    It sets the running flag to false, indicating that the game loop should stop running.
     It also waits for the gameThread to finish executing by calling join() on it.
     */
    fun pause() {
        running = false
        try {
            gameThread.join()
        } catch (e: InterruptedException) {
        }
    }
    /*
    This function is called to resume the game.
    It sets the running flag to true, indicating that the game loop should start running or continue running.
    It creates a new gameThread and starts it.
     */
    fun resume() {
        running = true
        gameThread = Thread(this@GameMainView)
        gameThread.start()
    }
}