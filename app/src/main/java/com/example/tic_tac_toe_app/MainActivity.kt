package com.example.tic_tac_toe_app

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

// Was used to get the idea how project has to look like, and find what to start with.
// https://www.geeksforgeeks.org/how-to-build-a-tic-tac-toe-game-with-both-offline-and-online-mode-in-android/
// Briefly watched, mostly to get how XML file should we written
// https://youtu.be/POFvcoRo3Vw?si=338lDvTTpX26zqYY

// MainActivity is responsible for letting the player(s) enter their name(s) and choose the game mode
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Load the UI layout defined in main_activity.xml
        setContentView(R.layout.main_activity)

        // References to UI elements
        val nameInput1 = findViewById<EditText>(R.id.editTextName1)
        val nameInput2 = findViewById<EditText>(R.id.editTextName2)
        val modeGroup = findViewById<RadioGroup>(R.id.radioGroupMode)
        val startButton = findViewById<Button>(R.id.buttonStart)

        // Show second player name input only if PvP mode is selected
        // This improves UX by hiding unnecessary input when playing against computer
        modeGroup.setOnCheckedChangeListener { _, checkedId ->
            nameInput2.visibility = if (checkedId == R.id.radioPvP) View.VISIBLE else View.GONE
        }

        // When the Start button is clicked
        startButton.setOnClickListener {
            // Get player 1 name, or use a default if left empty
            val player1 = nameInput1.text.toString().ifBlank { "Player 1" }

            // Determine if the user selected PvP mode
            val isPvP = modeGroup.checkedRadioButtonId == R.id.radioPvP

            // If playing PvP, get the second player's name (or default)
            // If playing PvC, always assign the name "Computer" to player 2
            // This prevents a bug where the second player’s name (from previous PvP input) would persist in PvC mode
            val player2 = if (isPvP) {
                nameInput2.text.toString().ifBlank { "Player 2" }
            } else {
                "Computer"
            }

            // Use an Intent to start the GameActivity and pass along the player names and selected mode
            val intent = Intent(this, GameActivity::class.java)
            intent.putExtra("PLAYER_NAME_1", player1)
            intent.putExtra("PLAYER_NAME_2", player2)
            intent.putExtra("IS_PVP", isPvP)
            startActivity(intent)
        }
    }
}
