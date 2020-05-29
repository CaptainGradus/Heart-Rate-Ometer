package net.kibotu.heartrateometer

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import net.kibotu.heartrateometer.app.MeasureActivity
import net.kibotu.heartrateometer.app.R

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
        }
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