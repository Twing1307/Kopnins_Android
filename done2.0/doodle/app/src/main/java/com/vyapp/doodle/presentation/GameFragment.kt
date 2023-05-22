package com.vyapp.doodle.presentation

import android.content.Context
import android.content.pm.ActivityInfo
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.vyapp.doodle.MainActivity

import com.vyapp.doodle.R
import com.vyapp.doodle.data.GlobalScore
import com.vyapp.doodle.databinding.FragmentGameOverBinding
import com.vyapp.doodle.view.GameMainView

class GameFragment : Fragment() {

    private lateinit var gameView: GameMainView
    private lateinit var sensor: Sensor
    private lateinit var sensorManager: SensorManager
    private lateinit var sensorListener: SensorEventListener


    private val binding: FragmentGameOverBinding by lazy {
        FragmentGameOverBinding.inflate(layoutInflater)
    }

    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(requireActivity())[MainViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        gameView = GameMainView(requireContext().applicationContext)
        gameView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        //requireActivity().setContentView(gameView)

        return gameView
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sensorManager = requireActivity().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)

        viewModel.setOver(false)

        //https://www.youtube.com/watch?v=4Q-2il82MCc&t=227s
        sensorListener = (object : SensorEventListener {

            override fun onSensorChanged(p0: SensorEvent?) {
                val rm = FloatArray(16)
                val _rm = FloatArray(16)
                SensorManager.getRotationMatrixFromVector(rm, p0?.values)
                SensorManager.remapCoordinateSystem(
                    rm,
                    SensorManager.AXIS_X,
                    SensorManager.AXIS_Z,
                    _rm
                )

                val orientation = FloatArray(3)
                SensorManager.getOrientation(_rm,orientation)

                for(i in 0..2){
                   orientation[i] = Math.toDegrees(orientation[i].toDouble()).toFloat()
                }


                if(gameView.over){
                    viewModel.setOver(true)
                }

                if(viewModel.isAcc){
                    if(orientation[2] > 0){
                        gameView.movePlayerRight = true
                        gameView.movePlayerLeft = false
                    }

                    if(orientation[2] < 0){
                        gameView.movePlayerLeft = true
                        gameView.movePlayerRight = false
                    }
                }

            }

            override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        viewModel.scoreLiveData.observe(viewLifecycleOwner) {
            if (it != null) {
                GlobalScore.score = it.score
            }
        }

        viewModel.nameLiveData.observe(viewLifecycleOwner) {

            Toast.makeText(
                requireContext(),
                requireContext().getString(R.string.hello) + " " + it,
                Toast.LENGTH_LONG
            ).show()

        }

        viewModel.isOver.observe(viewLifecycleOwner){
            if(it){
                (requireActivity() as MainActivity).toGameOverFragment()
            }
        }

    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(sensorListener)
        gameView.pause()
    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(sensorListener, sensor, SensorManager.SENSOR_DELAY_FASTEST)
        gameView.resume()
    }
}