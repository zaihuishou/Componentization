package com.tt.componentization

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.tt.route.annotation.Route
import kotlinx.android.synthetic.main.activity_main.*

@Route(path = "/main/main")
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btn_start.setOnClickListener {
        }
    }
}
