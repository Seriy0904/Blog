package uz.urgench.blog

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class BlogSelected : AppCompatActivity() {
    private lateinit var time:TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blog_selected)
        time = findViewById(R.id.time)
        val df = GregorianCalendar(TimeZone.getTimeZone("gmt"))
        time.text = df.get(Calendar.HOUR).toString()
        df.timeZone = GregorianCalendar().timeZone
        time.text = time.text.toString()+" "+df.get(Calendar.HOUR_OF_DAY).toString()
    }
}