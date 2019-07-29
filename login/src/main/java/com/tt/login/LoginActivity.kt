/**
 *
 *@author tanzhiqiang
 */

package com.tt.login

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.tt.route.annotation.Extra
import com.tt.route.annotation.Route

@Route(path = "/login/login")
class LoginActivity : AppCompatActivity() {

    @Extra
    var msg: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }
}