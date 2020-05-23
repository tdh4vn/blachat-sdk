package com.blameo.chatsdk.screens

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.blameo.chatsdk.MainActivity
import com.blameo.chatsdk.R
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {

    private val token_user1 = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJjaGFubmVsIjoiJGNoYXQ6ZTk3Y2FkMTktYjFhNC00MzY5LTljNDctMzhjODhkMjc2MGFhIiwiY2xpZW50IjoiZTk3Y2FkMTktYjFhNC00MzY5LTljNDctMzhjODhkMjc2MGFhIiwiZXhwIjoxNTkyMzc5NDEyLCJzdWIiOiJlOTdjYWQxOS1iMWE0LTQzNjktOWM0Ny0zOGM4OGQyNzYwYWEiLCJ1c2VySWQiOiJlOTdjYWQxOS1iMWE0LTQzNjktOWM0Ny0zOGM4OGQyNzYwYWEifQ.qb8i9SHHQ0bg_xaoMYuzW--FOOIYXEiVHFZO4BCugFY"

    private val tokenWs_user1 = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJjaGFubmVsIjoiJGNoYXQ6ZTk3Y2FkMTktYjFhNC00MzY5L" +
            "TljNDctMzhjODhkMjc2MGFhIiwiY2xpZW50IjoiZTk3Y2FkMTktYjFhNC00MzY5LTljNDctMzhjODhkMjc2MGFhIiwiZXhwIjoxNTg5NjUz" +
            "MjY2LCJzdWIiOiJlOTdjYWQxOS1iMWE0LTQzNjktOWM0Ny0zOGM4OGQyNzYwYWEiLCJ1c2VySWQiOiJlOTdjYWQxOS1iMWE0LTQzNjktOW" +
            "M0Ny0zOGM4OGQyNzYwYWEifQ.1g799Cka7FBMyflB1sEjP2WYnA99rdJEYWci8_z_a2U"

    private val tokenWs_user2 = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJjaGFubmVsIjoiJGNoYXQ6Y2E5NTU5ZWUtNjUwMC00OTc" +
            "wLTg2NWUtMTJhOTY3MWUzM2I0IiwiY2xpZW50IjoiY2E5NTU5ZWUtNjUwMC00OTcwLTg2NWUtMTJhOTY3MWUzM2I0IiwiZXhwIjoxNTg" +
            "5Nzg3MTM2LCJzdWIiOiJjYTk1NTllZS02NTAwLTQ5NzAtODY1ZS0xMmE5NjcxZTMzYjQiLCJ1c2VySWQiOiJjYTk1NTllZS02NTA" +
            "wLTQ5NzAtODY1ZS0xMmE5NjcxZTMzYjQifQ.RA_VJD5RXvdDOF5Ar9IaEQKENzxusdiRYaAu6Pvmw_8"

    private val tokenWs_user3 = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJjaGFubmVsIjoiJGNoYXQ6MzM2OTY0MzItND" +
            "Q0MC00NzhiLTk3ODctOGFiOWRiYjFmMzgwIiwiY2xpZW50IjoiMzM2OTY0MzItNDQ0MC00NzhiLTk3ODctOGFiOWRiYjFmM" +
            "zgwIiwiZXhwIjoxNTg5Nzg3MTkzLCJzdWIiOiIzMzY5NjQzMi00NDQwLTQ3OGItOTc4Ny04YWI5ZGJiMWYzODAiLCJ1c2Vy" +
            "SWQiOiIzMzY5NjQzMi00NDQwLTQ3OGItOTc4Ny04YWI5ZGJiMWYzODAifQ.HZfTxnrW4qDbk1q4A64LohnC4bOAaxTr_OzD7jjVozY"

    private val token_user2 = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJjaGFubmVsIjoiJGNoYXQ6Y2E5NTU5ZWUtNjUwMC00OTcwLTg2NWUtMTJhOTY3MWUzM2I0IiwiY2xpZW50IjoiY2E5NTU5ZWUtNjUwMC00OTcwLTg2NWUtMTJhOTY3MWUzM2I0IiwiZXhwIjoxNTkyMzc5Mzc0LCJzdWIiOiJjYTk1NTllZS02NTAwLTQ5NzAtODY1ZS0xMmE5NjcxZTMzYjQiLCJ1c2VySWQiOiJjYTk1NTllZS02NTAwLTQ5NzAtODY1ZS0xMmE5NjcxZTMzYjQifQ.2EWnIejT-exZnXS0KCI48f-4LeB3l1VAkHlh7CwQkoA"

    private val token_user3 = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJjaGFubmVsIjoiJGNoYXQ6MzM2OTY0MzItNDQ0MC00Nzh" +
            "iLTk3ODctOGFiOWRiYjFmMzgwIiwiY2xpZW50IjoiMzM2OTY0MzItNDQ0MC00NzhiLTk3ODctOGFiOWRiYjFmMzgwIiwiZXhwIjoxNTg3" +
            "OTU4ODY3LCJzdWIiOiIzMzY5NjQzMi00NDQwLTQ3OGItOTc4Ny04YWI5ZGJiMWYzODAiLCJ1c2VySWQiOiIzMzY5NjQzMi00NDQw" +
            "LTQ3OGItOTc4Ny04YWI5ZGJiMWYzODAifQ.kl38LgvhjwKk61WXuoqL2CuChZZ338BXA-4tlU35efA"

    private val my_id_user1 = "e97cad19-b1a4-4369-9c47-38c88d2760aa"
    private val my_id_user2 = "ca9559ee-6500-4970-865e-12a9671e33b4"
    private val my_id_user3 = "33696432-4440-478b-9787-8ab9dbb1f380"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val intent = Intent(this, MainActivity::class.java)
        btn_user1.setOnClickListener {
            intent.putExtra("USER_ID", my_id_user1)
            intent.putExtra("TOKEN", token_user1)
            intent.putExtra("TOKEN_WS", tokenWs_user1)
            startActivity(intent)
        }

        btn_user2.setOnClickListener {
            intent.putExtra("USER_ID", my_id_user2)
            intent.putExtra("TOKEN", token_user2)
            intent.putExtra("TOKEN_WS", tokenWs_user2)
            startActivity(intent)
        }

        btn_user3.setOnClickListener {
            intent.putExtra("USER_ID", my_id_user3)
            intent.putExtra("TOKEN", token_user3)
            intent.putExtra("TOKEN_WS", tokenWs_user3)
            startActivity(intent)
        }

    }
}
