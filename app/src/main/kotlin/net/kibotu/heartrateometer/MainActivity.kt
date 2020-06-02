package net.kibotu.heartrateometer

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import net.kibotu.heartrateometer.app.GameActivity
import net.kibotu.heartrateometer.app.MeasureActivity
import net.kibotu.heartrateometer.app.R
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.time.LocalDate


class MainActivity : AppCompatActivity() {
    enum class Status { Calm, Average, Stressed, Panic }

    private var historyMap: MutableMap<LocalDate, Int> = mutableMapOf()
    private var status: Status? = null
        set(value) {
            field = value
            statusView.text = value.toString()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // retrieving previous saved status
        val sharedPref = this.getPreferences(Context.MODE_PRIVATE)
        val stat = sharedPref.getString("Status", null)
        if (stat != null)
            status = Status.valueOf(stat)

        // retrieving history data
//        val fileInputStream = FileInputStream("historyMap")
//        val objectInputStream = ObjectInputStream(fileInputStream)
//
//        historyMap = objectInputStream.readObject() as MutableMap<LocalDate, Int>
//        objectInputStream.close()

        // measure
        measureButton.setOnClickListener {
            val intent = Intent(this, MeasureActivity::class.java)
            startActivityForResult(intent, 1)
        }

        // play
        playButton.setOnClickListener {
            if (status != null) {
                val intent = Intent(this, GameActivity::class.java)
                intent.putExtra("velocityMove", getSpeed())
                startActivity(intent)
            } else {
                val toast = Toast.makeText(applicationContext,
                        "First measure the heartbeat!", Toast.LENGTH_SHORT)
                toast.show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // working with measurement results
        if (data != null)
            when (requestCode) {
                1 -> setStatus(data.getIntExtra("bpm", 0))
            }
    }

    private fun setStatus(bpm: Int) {
        if (bpm != 0) {
            // assigning status according to the measurement
            when {
                bpm <= 70 -> status = Status.Calm
                bpm in 71..85 -> status = Status.Average
                bpm in 86..120 -> status = Status.Stressed
                bpm > 120 -> status = Status.Panic
            }

            // saving the result
            val sharedPref = this.getPreferences(Context.MODE_PRIVATE)
            with(sharedPref.edit()) {
                putString("Status", status.toString())
                apply()
            }

            // saving the result in the history
//            val fileOutputStream = FileOutputStream("historyMap")
//            val objectOutputStream = ObjectOutputStream(fileOutputStream)
//
//            objectOutputStream.writeObject(historyMap)
//            objectOutputStream.close()
        }
    }

    private fun getSpeed(): Int {
        // status to int
        return when (status) {
            null -> 0
            Status.Calm -> 5
            Status.Average -> 7
            Status.Stressed -> 10
            Status.Panic -> 15
        }
    }
}