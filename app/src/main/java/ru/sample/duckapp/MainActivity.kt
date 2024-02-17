package ru.sample.duckapp

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.sample.duckapp.domain.Duck
import ru.sample.duckapp.infra.Api
import java.io.ByteArrayInputStream



fun isValid(input: String): Boolean {
    return input.matches("^(|100|101|102|103|200|201|202|203|204|205|206|207|208|226|300|301|302|303|304|305|306|307|308|400|401|402|403|404|405|406|407|408|409|410|411|412|413|414|415|416|417|418|421|422|423|424|425|426|428|429|431|451|500|501|502|503|504|505|506|507|508|510|511)".toRegex())
}

class MainActivity : AppCompatActivity() {
    private lateinit var duckImageView: ImageView

    fun getDuck(){
        Api.ducksApi.getRandomDuck().enqueue(object : Callback<Duck> {
            override fun onResponse(call: Call<Duck>, response: Response<Duck>) {
                if (response.isSuccessful) {
                    val duck = response.body()
                    duck?.let {

                        Picasso.get().load(it.url).resize(duckImageView.width, duckImageView.height).into(duckImageView)
                    }
                } else {
                }
            }

            override fun onFailure(call: Call<Duck>, t: Throwable) {
                val rootView = findViewById<View>(android.R.id.content)
                val message = "Сегодня уточка не пришла..."
                Snackbar.make(rootView, message, Snackbar.LENGTH_LONG).setTextColor(Color.WHITE).setBackgroundTint(Color.BLUE).show()

            }
        })
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val btn = findViewById<Button>(R.id.generateButton)
        duckImageView = findViewById(R.id.imageView)

        val duckCodeEditText = findViewById<EditText>(R.id.textInputEditText)
        duckCodeEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (isValid(s.toString())) {
                    duckCodeEditText.error = null
                    btn.isEnabled = true
                } else {
                    duckCodeEditText.error = "Ошибка: Введите существующий http код!"
                    btn.isEnabled = false
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        btn.setOnClickListener{
                val duckCode = duckCodeEditText.text.toString()
                if (duckCode.isNotEmpty()) {
                    // Выполняем запрос на получение утки по введенному коду
                    Api.ducksApi.getCodeDuck(duckCode).enqueue(object : Callback<ResponseBody> {
                        override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                            if (response.isSuccessful) {
                                // Уверен что можно сделать красивее и проще
                                val duckByteArray = response.body()?.bytes()
                                duckByteArray?.let {
                                    val inputStream = ByteArrayInputStream(it)
                                    val bitmap =
                                        BitmapFactory.decodeStream(inputStream)

                                    duckImageView.setImageBitmap(Bitmap.createScaledBitmap(bitmap, duckImageView.width, duckImageView.height, true))
                                }
                            } else {


                                val rootView = findViewById<View>(android.R.id.content)
                                val message = "Код есть, а вот уточки к нему нету((((((("
                                Snackbar.make(rootView, message, Snackbar.LENGTH_LONG).setTextColor(Color.WHITE).setBackgroundTint(Color.BLUE).show()

                            }
                        }

                        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                            val rootView = findViewById<View>(android.R.id.content)
                            val message = "Сегодня уточка не пришла..."
                            Snackbar.make(rootView, message, Snackbar.LENGTH_LONG).setTextColor(Color.WHITE).setBackgroundTint(Color.BLUE).show()

                        }
                    })
                }
                else {
                    getDuck()
                }
            }
        }
    }


