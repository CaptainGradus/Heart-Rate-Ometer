package net.kibotu.heartrateometer.app

import android.app.PendingIntent.getActivity
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter
import com.jjoe64.graphview.series.BarGraphSeries
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import com.jjoe64.graphview.series.PointsGraphSeries
import kotlinx.android.synthetic.main.activity_stats.*
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.MutableMap as MutableMap1

class StatsActivity : AppCompatActivity() {
    var series = LineGraphSeries<DataPoint>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stats)

        // retrieving history to show
        val historyMap = intent.getSerializableExtra("historyMap") as HashMap<Date, Int>

        for (hm in historyMap.toSortedMap())
            series.appendData(DataPoint(hm.key, hm.value.toDouble()), true, 30)

        // set date label formatter
        statsGraph.gridLabelRenderer.labelFormatter = DateAsXAxisLabelFormatter(this);
        statsGraph.gridLabelRenderer.numHorizontalLabels = 2; // only 4 because of the space

        // set manual x bounds to have nice steps
        statsGraph.viewport.setMinX(series.lowestValueX)
        statsGraph.viewport.setMaxX(series.highestValueX)
        statsGraph.viewport.isXAxisBoundsManual = true;

        // as we use dates as labels, the human rounding to nice readable numbers
        // is not necessary
        statsGraph.gridLabelRenderer.setHumanRounding(false);

        // set manual Y bounds
        statsGraph.viewport.isYAxisBoundsManual = true;
        statsGraph.viewport.setMinY(0.0);
        statsGraph.viewport.setMaxY(200.0);

        // enable scrolling
        statsGraph.viewport.isScrollable = true
        statsGraph.viewport.isScalable = true

        statsGraph.addSeries(series)
    }
}