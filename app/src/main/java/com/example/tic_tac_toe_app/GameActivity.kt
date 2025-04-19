package com.example.tic_tac_toe_app

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class GameActivity : AppCompatActivity() {

    // 2D array representing the 3x3 grid of buttons
    private lateinit var board: Array<Array<Button>>

    // Keeps track of whose turn it is — true = player1 (X), false = player2/computer (O)
    private var playerTurn = true

    // Number of total moves made (used to detect draws)
    private var moveCount = 0

    // Flag to track if we are in Player-vs-Player mode
    private var isPvP = true

    // Player names — can be custom names or "Computer"
    private var player1 = "Player 1"
    private var player2 = "Computer"

    @SuppressLint("SetTextI18n", "DiscouragedApi") // Android Studio advices
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.game_activity)

        // Receive data passed from MainActivity via Intent
        player1 = intent.getStringExtra("PLAYER_NAME_1") ?: "Player 1"
        player2 = intent.getStringExtra("PLAYER_NAME_2") ?: "Computer"
        isPvP = intent.getBooleanExtra("IS_PVP", true)

        // Update greeting text dynamically depending on selected mode
        val greetingText = findViewById<TextView>(R.id.textGreeting)
        greetingText.text = "Welcome! $player1 vs ${if (isPvP) player2 else "Computer"}"

        // (Firstly was like - val button00 = findViewById<Button>(R.id.button00), val button01 = ...)
        // Prompt: How better to create playing board?
        // Dynamically initialize the 3x3 game board by locating buttons using their IDs
        // Naming pattern like button00, button01, ... button22 allows clean 2D-array usage
        board = Array(3) { row ->
            Array(3) { col ->
                val buttonId = resources.getIdentifier("button$row$col", "id", packageName)
                findViewById<Button>(buttonId).apply {
                    // Set a click listener on each button
                    setOnClickListener { onCellClicked(this) }
                }
            }
        }
    }

    // Called whenever a user clicks on a cell
    private fun onCellClicked(button: Button) {
        // Do nothing if the cell is already filled
        if (button.text.isNotEmpty()) return

        // Mark the cell with current player's symbol
        button.text = if (playerTurn) "X" else "O"
        moveCount++

        // After each move, check for a win or draw
        if (checkWin()) {
            val winner = if (playerTurn) player1 else player2
            showResult("$winner wins!")
        } else if (moveCount == 9) {
            showResult("It's a draw!")
        } else {
            // Switch turns
            playerTurn = !playerTurn

            // If it's the computer's turn, let the AI play automatically
            if (!playerTurn && !isPvP) makeComputerMove()
        }
    }

    // Mostly generated with ChatGPT-4o (with multiple corrections) :prompt: Generate simple AI for tic-tac-toe game on Kotlin
    // Very basic "AI" — tries to win, block, or pick a random move
    private fun makeComputerMove() {

        // Try to win or block
        for (symbol in listOf("O", "X")) {
            for (i in 0..2) {
                for (j in 0..2) {
                    val button = board[i][j]
                    if (button.text.isEmpty()) {
                        button.text = symbol
                        if (checkWin()) {
                            if (symbol == "O") {
                                moveCount++
                                showResult("$player2 wins!")
                                return
                            } else {
                                button.text = "" // undo
                            }
                        } else {
                            button.text = ""
                        }
                    }
                }
            }
        }

        // Random move with delay
        val emptyCells = mutableListOf<Pair<Int, Int>>()
        for (i in 0..2) {
            for (j in 0..2) {
                if (board[i][j].text.isEmpty()) {
                    emptyCells.add(i to j)
                }
            }
        }

        if (emptyCells.isNotEmpty()) {
            val (i, j) = emptyCells.random()

            // Delay the computer's move by 1 second
            android.os.Handler(mainLooper).postDelayed({
                board[i][j].text = "O"
                moveCount++

                when {
                    checkWin() -> showResult("$player2 wins!")
                    moveCount == 9 -> showResult("It's a draw!")
                    else -> {
                        playerTurn = true
                    }
                }
            }, 1000L)
        }
    }

    // Checks if any player has won the game
    private fun checkWin(): Boolean {
        // Convert the current board into a 2D array of Strings
        val symbols = Array(3) { row -> Array(3) { col -> board[row][col].text.toString() } }

        // Check rows and columns
        for (i in 0..2) {
            if (symbols[i][0] == symbols[i][1] && symbols[i][1] == symbols[i][2] && symbols[i][0].isNotEmpty()) return true
            if (symbols[0][i] == symbols[1][i] && symbols[1][i] == symbols[2][i] && symbols[0][i].isNotEmpty()) return true
        }
        // Check diagonals
        if (symbols[0][0] == symbols[1][1] && symbols[1][1] == symbols[2][2] && symbols[0][0].isNotEmpty()) return true
        if (symbols[0][2] == symbols[1][1] && symbols[1][1] == symbols[2][0] && symbols[0][2].isNotEmpty()) return true

        return false
    }

    // Displays the result in a popup dialog with options to restart or quit
    private fun showResult(message: String) {
        AlertDialog.Builder(this) //:prompt: How to create alert box? ; How AlertDialog works?
            .setTitle("Game Over")
            .setMessage(message)
            .setPositiveButton("Play Again") { _, _ -> resetBoard() }
            .setNegativeButton("Exit") { _, _ -> finish() }
            .show()
    }

    // Resets the board and counters so that a new game can be played
    private fun resetBoard() {
        for (i in 0..2) {
            for (j in 0..2) {
                board[i][j].text = ""
            }
        }
        playerTurn = true
        moveCount = 0
    }
}
