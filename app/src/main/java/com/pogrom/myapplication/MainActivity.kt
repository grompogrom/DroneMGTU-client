package com.pogrom.myapplication

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.androidplot.xy.*
import com.google.android.material.textfield.TextInputLayout
import com.pogrom.myapplication.models.DroneStatus
import com.pogrom.myapplication.tools.readIpFromDataStore
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
    private lateinit var plot: XYPlot
    lateinit var series1Format : LineAndPointFormatter
    val viewModel: MainViewModel by viewModels()



    private fun generateScatter(xyArray: List<List<Double>>): XYSeries {
        val series = SimpleXYSeries("")
        for(point in xyArray){
            series.addLast(
                point[0],
                point[1]
            )
        }
        return series
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        series1Format = LineAndPointFormatter(this, R.xml.point_formater)

        // initialize our XYPlot reference:
        plot = findViewById<View>(R.id.plot) as XYPlot
        val buttonConnect = findViewById<Button>(R.id.btn_connect)
        val buttonScan = findViewById<Button>(R.id.btn_scan)
        val buttonMap = findViewById<Button>(R.id.btn_get_map)
        val buttonApplyIp = findViewById<Button>(R.id.btnApplyIp)
        val addresText = findViewById<TextInputLayout>(R.id.editTextTextChangeIp)
        val text_server_connected = findViewById<TextView>(R.id.test_server_status_connected)
        val text_server_disconnected = findViewById<TextView>(R.id.test_server_status_disconnected)
        val text_drones_status = findViewById<TextView>(R.id.test_drones_status)
        val series1 = generateScatter(listOf<List<Double>>())

        // create formatters to use for drawing a series using LineAndPointRenderer
        // and configure them from xml:

        // add each series to the xyplot:
        plot.addSeries(series1, series1Format)

        // reduce the number of range labels
        plot.setLinesPerRangeLabel(3)
        lifecycleScope.launch {
            readIpFromDataStore(this@MainActivity)
                .filter { it.isNotBlank() }
                .onEach {
                    addresText.editText?.setText(it)
                }
                .collect()
        }

        buttonConnect.setOnClickListener{
            viewModel.connectToDrones()
        }
        buttonScan.setOnClickListener{
            viewModel.startScan()
        }
        buttonMap.setOnClickListener {
            viewModel.onButtonGetMapClick()
        }

        buttonApplyIp.setOnClickListener {
            addresText.editText?.let {
                val addres = it.text.toString()
                viewModel.changeIp(addres, this)
            }

        }

        viewModel.serverStatusConnected.observe(this){
            Log.d("serverStatus", "status is: $it")
            if (it) {text_server_connected.visibility = View.VISIBLE
                text_server_disconnected.visibility = View.GONE

            } else{
                text_server_connected.visibility = View.GONE
                text_server_disconnected.visibility = View.VISIBLE
            }
            buttonConnect.isActivated = !it
        }

        viewModel.dronesStatus.observe(this){
            when(it){
                DroneStatus.DISCONNECTED -> {
                    text_drones_status.text = "Disconnected"
                    text_drones_status.setTextColor(getResources().getColor(R.color.red))
                }
                DroneStatus.CONNECTED -> {
                    text_drones_status.text = "Connected"
                    text_drones_status.setTextColor(getResources().getColor(R.color.teal_200))
                }
                DroneStatus.FLYING -> {
                    text_drones_status.text = "Flying"
                    text_drones_status.setTextColor(getResources().getColor(R.color.purple_200))
                }
            }
        }


        viewModel.plotLiveData.observe(this){
            updatePlotData(it)
        }

        viewModel.isMapUpdating.observe(this){
            if (it)
                buttonMap.setBackgroundColor(getResources().getColor(R.color.teal_200))
            else
                buttonMap.setBackgroundColor(getResources().getColor(R.color.purple_200))
        }
    }

    fun updatePlotData(xyList: List<List<Double>>){
        plot.clear()
        plot.addSeries( generateScatter(xyList), series1Format)
        plot.redraw()
    }
}

