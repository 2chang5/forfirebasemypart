package bias.hugoandrade.calendarviewapp

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_photo_viewer.*

class PhotoViewer : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_viewer)

        val picture_uri = intent.getStringExtra("uri")
        val fromWhere = intent.getStringExtra("from")

        //이미지 넣기
        if(picture_uri == null){
            photo_view.setImageDrawable(null)
        }else{
            photo_view.setImageURI(Uri.parse(picture_uri))
        }

        // 왔던데로 돌아가기(x버튼)
        go_out_button.setOnClickListener {
            if(fromWhere == "diary") {
                onBackPressed()
            }
        }

    }
}