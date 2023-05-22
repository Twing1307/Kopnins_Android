package com.vyapp.doodle

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.vyapp.doodle.presentation.GameFragment
import com.vyapp.doodle.presentation.GameOverFragment
import com.vyapp.doodle.presentation.StartFragment


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


    }


    fun toGameOverFragment() {
        supportFragmentManager.beginTransaction().replace(R.id.nav_host_fragment,GameOverFragment()).commit()
    }

    fun toGameFragment() {
        supportFragmentManager.beginTransaction().replace(R.id.nav_host_fragment,GameFragment()).commit()
    }

    fun toStartFragment() {
        supportFragmentManager.beginTransaction().replace(R.id.nav_host_fragment, StartFragment()).commit()
    }
}