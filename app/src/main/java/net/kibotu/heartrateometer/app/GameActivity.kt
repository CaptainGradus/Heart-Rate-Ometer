package net.kibotu.heartrateometer.app

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.unity3d.player.UnityPlayerActivity

class GameActivity : AppCompatActivity() {
    // activity to run UnityPlayerActivity (so the app doesn't close with it)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // running the game
        val intentForUnity = Intent(this, UnityPlayerActivity::class.java)
        intentForUnity.putExtra("velocityMove", intent.getIntExtra("velocityMove", 0))
        startActivity(intentForUnity)
    }
}