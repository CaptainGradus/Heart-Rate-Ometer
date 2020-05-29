package net.kibotu.heartrateometer

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import net.kibotu.heartrateometer.app.MeasureActivity
import net.kibotu.heartrateometer.app.R
import com.unity3d.player.UnityPlayerActivity

class MainActivity : AppCompatActivity() {
    enum class Status { Calm, Average, Stressed, Panic }
    private var status: Status? = null  // todo save status for next time

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        measureButton.setOnClickListener {
            val intent = Intent(this, MeasureActivity::class.java)
            startActivityForResult(intent, 1)
        }

        playButton.setOnClickListener {
            val intent = Intent(this, UnityPlayerActivity::class.java)
            intent.putExtra("velocityMove", 5)
            startActivity(intent)
        }
        // TODO E/Unity: NullReferenceException: Object reference not set to an instance of an object.
        //      at PlayerControls.PathLength () [0x00000] in <00000000000000000000000000000000>:0
        //      at ScoreScript.get_Score () [0x00000] in <00000000000000000000000000000000>:0
        //      at ScoreScript.Update () [0x00000] in <00000000000000000000000000000000>:0
        // TODO assign proper speed
        // TODO screen size
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (data == null)
            return

        when (requestCode) {
            1 -> setStatus(data.getIntExtra("bpm", 0))
        }
    }

    private fun setStatus(bpm: Int) {
        if (bpm != 0) {
            when {
                bpm <= 70 -> status = Status.Calm
                bpm in 71..85 -> status = Status.Average
                bpm in 86..120 -> status = Status.Stressed
                bpm > 120 -> status = Status.Panic
            }

            statusView.text = status.toString()
        }
    }
}