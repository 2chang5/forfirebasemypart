package bias.hugoandrade.calendarviewapp

import android.Manifest
import android.animation.ValueAnimator
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Parcelable
import android.util.Log
import android.view.View
import android.view.View.OnTouchListener
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import bias.hugoandrade.calendarviewapp.data.USER
import com.bumptech.glide.Glide
import com.yalantis.ucrop.UCrop
import kotlinx.android.synthetic.main.activity_diary.*
import lv.chi.photopicker.ChiliPhotoPicker
import lv.chi.photopicker.PhotoPickerFragment
import java.io.File


class DiaryActivity : AppCompatActivity(), PhotoPickerFragment.Callback {

    var softKeyboard: SoftKeyboard? = null
    var coverlayout: LinearLayout? = null
    val permission_list = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE,
    )
    var picture_uri: Uri? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diary)



        //최초 이미지uri 변수 비우기
        picture_uri = null

        //이미지뷰 라운딩
        picture_view.clipToOutline = true

        //배경 카드색깔 젤리에 맞추기
        val emotion = intent.getIntExtra("emotion", 1)
        if(emotion == 1){
            diaryScrollCard.setBackgroundResource(R.drawable.for_diary_card1)
        }else if(emotion == 2){
            diaryScrollCard.setBackgroundResource(R.drawable.for_diary_card2)
        }else if(emotion == 3){
            diaryScrollCard.setBackgroundResource(R.drawable.for_diary_card3)
        }else if(emotion == 4){
            diaryScrollCard.setBackgroundResource(R.drawable.for_diary_card4)
        }else if(emotion == 5){
            diaryScrollCard.setBackgroundResource(R.drawable.for_diary_card5)
        }
        //키보드 바도 색깔 맞추기
        if(emotion == 1){
            keyBoardbar.setBackgroundResource(R.drawable.for_keyboard_bar_1)
        }else if(emotion == 2){
            keyBoardbar.setBackgroundResource(R.drawable.for_keyboard_bar_2)
        }else if(emotion == 3){
            keyBoardbar.setBackgroundResource(R.drawable.for_keyboard_bar_3)
        }else if(emotion == 4){
            keyBoardbar.setBackgroundResource(R.drawable.for_keyboard_bar_4)
        }else if(emotion == 5){
            keyBoardbar.setBackgroundResource(R.drawable.for_keyboard_bar_5)
        }


       //키보드 바 설정용 키보드 올라가고 내려가는 이벤트처리
        coverlayout = findViewById<View>(R.id.coverlayout) as LinearLayout
        val controlManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        softKeyboard = SoftKeyboard(coverlayout, controlManager)
        softKeyboard!!.setSoftKeyboardCallback(object : SoftKeyboard.SoftKeyboardChanged {
            override fun onSoftKeyboardHide() {
                Handler(Looper.getMainLooper()).post {
                    if(emotion == 1){
                        keyBoardbar.setBackgroundResource(R.drawable.for_keyboard_bar_1)
                    }else if(emotion == 2){
                        keyBoardbar.setBackgroundResource(R.drawable.for_keyboard_bar_2)
                    }else if(emotion == 3){
                        keyBoardbar.setBackgroundResource(R.drawable.for_keyboard_bar_3)
                    }else if(emotion == 4){
                        keyBoardbar.setBackgroundResource(R.drawable.for_keyboard_bar_4)
                    }else if(emotion == 5){
                        keyBoardbar.setBackgroundResource(R.drawable.for_keyboard_bar_5)
                    }
                }
            }

            override fun onSoftKeyboardShow() {
                Handler(Looper.getMainLooper()).post {
                    keyBoardbar.setBackgroundResource(R.drawable.for_keyboard_bar_up)
                }
            }
        })


        //x버튼 눌렸을떄 돌아가기
        xButton.setOnClickListener {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
        }

        //날짜 표시
        if(intent.hasExtra("nowDate")){
            val nowDate = intent.getStringExtra("nowDate")
            diaryDate.text = nowDate
        } else if (intent.hasExtra("lastDate")){
            val lastDate = intent.getStringExtra("lastDate")
            diaryDate.text = lastDate
        }


        //누구의 몇번째 젤리표시
        val diary_level =  intent.getIntExtra("diary_level", 1)
        val user_name = intent.getStringExtra("user_name")

        whoJelly.text = "${user_name}님의 ${diary_level}번째 젤리"





        //이미지 넣기
        ChiliPhotoPicker.init(
            loader = GlideImageLoader(),
            authority = "lv.chi.real.fileproviderforunion"
        )

        selectPic.setOnClickListener { openPicker() }


        //이미지 뷰어 클릭시 확대되는것
        picture_view.setOnClickListener {
            val intent = Intent(this, PhotoViewer::class.java)
            intent.putExtra("uri",picture_uri.toString())
            intent.putExtra("from","diary")
            startActivity(intent)
        }



        //이미지 x버튼 다없애버리기 파괴왕
        picture_x_button.setOnClickListener {
            picture_view.visibility = View.GONE
            picture_space.visibility = View.GONE
            picture_x_button.visibility = View.GONE
            picture_view.setImageDrawable(null)
        }



        //연인에게 공개시간 설정
        selectTime.setOnClickListener {

        }

        //텍스트 박스 포커스 조절(키보드바 라이브러리에서 자동처리되었음)





        //일기작성완료
        saveButton.setOnClickListener{
            //일기 내용 파이어스토어에 쳐넣기


            //로티 재생
            val animator = ValueAnimator.ofFloat(0f, 1f).setDuration(1300)
            animator.addUpdateListener {
                saveButton.setProgress(it.getAnimatedValue() as Float)
            }
            animator.start()
            //버튼 넘어가기 전까지 막기
            saveButton.setOnTouchListener(OnTouchListener { v, event -> true })
            //이거 로티 재생후 넘어가자
            Handler().postDelayed({
                val intent = Intent(this, FinishDiary::class.java)
                startActivity(intent)
                //버튼 풀어주기
                saveButton.setOnTouchListener(OnTouchListener { v, event -> false })
            }, 1300)
        }
    }

    //키보드 관련 디스트로이
    public override fun onDestroy() {
        super.onDestroy()
        softKeyboard!!.unRegisterSoftKeyboardCallback()
    }

    //이미지 넣기 chilipicker 받은 uri ucrop으로 넣고 이미지뷰 초기화
    override fun onImagesPicked(photos: ArrayList<Uri>) {
        for (i in photos) {
            val sourceUri = i
            val destinationUri = Uri.fromFile(File(cacheDir, "cropped"))
            openCropActivity(sourceUri, destinationUri)
            picture_view.setImageDrawable(null)
        }
    }

    //ucrop 넣기위한 함수 빌드
    private fun openCropActivity(sourceUri: Uri, destinationUri: Uri) {
        UCrop.of(sourceUri, destinationUri)
            .start(this)
    }
    //ucrop 이미지 crop한거 받아서 이미지뷰에 넣기
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            val resultUri = UCrop.getOutput(data!!)
            picture_view.setImageURI(resultUri)
            //이미지 uri 밖으로 빼기
            picture_uri = resultUri
            picture_view.visibility = View.VISIBLE
            picture_space.visibility = View.VISIBLE
            picture_x_button.visibility = View.VISIBLE
        } else if (resultCode == UCrop.RESULT_ERROR) {
            Toast.makeText(this, "이미지를 불러오는데 실패하였습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    //chiliphotopicker관련
    private fun openPicker() {
        PhotoPickerFragment.newInstance(
            multiple = false,
            allowCamera = true,
            maxSelection = 5,
        ).show(supportFragmentManager, "picker")
    }
}