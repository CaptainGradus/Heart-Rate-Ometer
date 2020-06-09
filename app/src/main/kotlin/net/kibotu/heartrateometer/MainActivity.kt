package net.kibotu.heartrateometer

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import net.kibotu.heartrateometer.app.R
import java.io.File
import java.io.FileInputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.util.*


class MainActivity : AppCompatActivity() {
    enum class Status { Calm, Average, Stressed, Panic }

    private val FILE_TO_STORE_HISTORY = "historyMap"
    private var historyMap: HashMap<Date, Int> = hashMapOf()
    private var status: Status? = null
        set(value) {
            field = value
            statusView.text = value?.toString() ?: "status unknown"
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // retrieving history data
        val f = File("$filesDir/$FILE_TO_STORE_HISTORY")
        if (f.isFile && f.canRead()) {
            val fileInputStream = FileInputStream(f)
            val objectInputStream = ObjectInputStream(fileInputStream)

            historyMap = objectInputStream.readObject() as HashMap<Date, Int>
            objectInputStream.close()
        }

        // retrieving previous saved status
        status = bpmToStat(historyMap.maxBy { it.key }?.value)


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

        // stats
        statsButton.setOnClickListener {
            if (historyMap.size > 1) {
                val intent = Intent(this, StatsActivity::class.java)
                intent.putExtra("historyMap", historyMap)
                startActivity(intent)
            } else {
                val toast = Toast.makeText(applicationContext,
                        "First measure the heartbeat at least 2 time!", Toast.LENGTH_SHORT)
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
            status = bpmToStat(bpm)

            // saving the result in the history
            historyMap[Calendar.getInstance().time] = bpm

            val fileOutputStream = openFileOutput(FILE_TO_STORE_HISTORY, Context.MODE_PRIVATE)
            val objectOutputStream = ObjectOutputStream(fileOutputStream)

            objectOutputStream.writeObject(historyMap)
            objectOutputStream.close()
        }
    }

    private fun bpmToStat(bpm: Int?): Status? {
        if (bpm != null)
            return when {
                bpm <= 70 -> Status.Calm
                bpm in 71..85 -> Status.Average
                bpm in 86..120 -> Status.Stressed
                bpm > 120 -> Status.Panic
                else -> null
            }

        return null
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