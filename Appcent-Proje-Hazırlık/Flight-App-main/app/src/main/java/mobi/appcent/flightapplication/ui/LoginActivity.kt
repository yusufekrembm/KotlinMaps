package mobi.appcent.flightapplication.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_login.*
import mobi.appcent.flightapplication.R

class LoginActivity : AppCompatActivity() {
    private val email = "appcent@appcent.mobi"
    private val password = "123456"
    private var isRemember = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        btnLogin.setOnClickListener {
            if (email == etEmail.text.toString() && password == etPassword.text.toString()) {
                if (isRemember) {
                    rememberMe()
                }
                val loginIntent = Intent(this, HomeActivity::class.java)
                startActivity(loginIntent)
                finish()
            } else {
                Toast.makeText(
                    this,
                    "Bilgileriniz hatalıdır. Lütfen bilgilerinizi kontrol ediniz.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        cbRememberMe.setOnCheckedChangeListener { _, isChecked ->
            isRemember = isChecked
        }
    }

    private fun rememberMe(){
        val sharedPreferences = getSharedPreferences("MySharedPreferences", Context.MODE_PRIVATE)
        val infoEdit = sharedPreferences.edit()
        infoEdit.putString("email",etEmail.text.toString())
        infoEdit.putString("password",etPassword.text.toString())
        infoEdit.putBoolean("isRemember",isRemember)
        infoEdit.commit()
    }
}