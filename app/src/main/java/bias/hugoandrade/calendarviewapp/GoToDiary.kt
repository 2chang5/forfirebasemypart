package bias.hugoandrade.calendarviewapp

import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Parcelable
import android.util.Log
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import bias.hugoandrade.calendarviewapp.data.COUPLE
import bias.hugoandrade.calendarviewapp.data.USER
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.takusemba.spotlight.Spotlight
import com.takusemba.spotlight.Target
import com.takusemba.spotlight.shape.Circle
import kotlinx.android.synthetic.main.activity_diary.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.forspotlight.*
import kotlinx.android.synthetic.main.gotodiary.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class GoToDiary : AppCompatActivity() {

    //파이어베이스 변수
    var firestore : FirebaseFirestore? = null
    var plusState : Boolean = false

    private var user = USER()
    private var couple = COUPLE()
    var diary_level : Int? = null


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.gotodiary)


        //파이어스토어 인스턴스 초기화
        firestore = FirebaseFirestore.getInstance()


        // 현재 유저에 대한 파이어베이스 auth 정보
        val CurrentUser : FirebaseUser = FirebaseAuth.getInstance().currentUser
        val Current_Uid : String = CurrentUser.uid
        //유저 정보 받아옴 파이어스토어에 있는거 user 객체에 저장
        getUserModel(Current_Uid)



        //몇번째 일기(젤리) 인지 파이어베이스 에서 받아오기
        getCoupleJellyModel(user.useR_CoupleUID)
        if (user.useR_Gender == 1){
            diary_level =  couple.couplE_LEVEL_MAN
        }else if (user.useR_Gender == 0){
            diary_level =  couple.couplE_LEVEL_GIRL
        }


        //플러스 로티
        plusState =  false

        //최초 이미지 편법
        val animator = ValueAnimator.ofFloat(1f, 1f).setDuration(1)
        animator.addUpdateListener {
            jellyView.setProgress(it.getAnimatedValue() as Float)
        }
        animator.start()
        
        //plusbutton 설정
        plusButton.setOnClickListener {

            jelly_exp.visibility = View.GONE

            if(plusState==false){
                // Custom animation speed or duration.
                val animator = ValueAnimator.ofFloat(1f, 0f).setDuration(2000)
                animator.addUpdateListener {
                    jellyView.setProgress(it.getAnimatedValue() as Float)
                }
                animator.start()

                // 버튼 연타 금지
                plusButton.visibility = View.GONE

                Handler().postDelayed({
                    plusButton.visibility = View.VISIBLE
                }, 1300)


                jelly_button1.visibility = View.VISIBLE
                jelly_button2.visibility = View.VISIBLE
                jelly_button3.visibility = View.VISIBLE
                jelly_button4.visibility = View.VISIBLE
                jelly_button5.visibility = View.VISIBLE



                plusState = true
            } else {
                // Custom animation speed or duration.
                val animator = ValueAnimator.ofFloat(0f, 1f).setDuration(2000)
                animator.addUpdateListener {
                    jellyView.setProgress(it.getAnimatedValue() as Float)
                }
                animator.start()

                // 버튼 연타 금지
                plusButton.visibility = View.GONE

                Handler().postDelayed({
                    plusButton.visibility = View.VISIBLE
                }, 1300)


                jelly_button1.visibility = View.GONE
                jelly_button2.visibility = View.GONE
                jelly_button3.visibility = View.GONE
                jelly_button4.visibility = View.GONE
                jelly_button5.visibility = View.GONE


                plusState= false
            }
        }


        //인텐트시 날짜 넘겨주기 위해 받는거
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd EE")
        val formatted = current.format(formatter)

        //스포트라이트 파트

        //1번젤리
        jelly_button1.setOnClickListener {
            //이파트 이해하고 싶다면 안드로이드 1강 섹션3 37강 부분을 참고하라
            val firstRoot = FrameLayout(this)
            val first = layoutInflater.inflate(R.layout.forspotlight, firstRoot)
            val firstTarget = Target.Builder()
                .setAnchor(jelly_button1)
                .setShape(Circle(120f))
                .setOverlay(first)
                .build()

            val spotlight = Spotlight.Builder(this@GoToDiary)
                .setTargets(firstTarget)
                .setBackgroundColorRes(R.color.spotlightBackground)
                .setDuration(1000L)
                .setAnimation(DecelerateInterpolator(2f))
                .build()

            spotlight.start()

            //나머지 버튼가리기
            first.findViewById<View>(R.id.goToTwo).visibility = View.GONE
            first.findViewById<View>(R.id.goToThree).visibility = View.GONE
            first.findViewById<View>(R.id.goToFour).visibility = View.GONE
            first.findViewById<View>(R.id.goToFive).visibility = View.GONE


            val closeSpotlight = View.OnClickListener {
                //잠시 버튼 죽이기
                first.findViewById<View>(R.id.goToOne).visibility = View.GONE
                //레이아웃 터치 죽여야함
                first.findViewById<View>(R.id.background).visibility = View.GONE
                //스포트라이트 끄기
                spotlight.finish()
                //잠시 시간거쳐 애니메이션 효과후 살리기,백그라운드도 살리기
                Handler().postDelayed({
                    first.findViewById<View>(R.id.goToOne).visibility = View.VISIBLE
                    first.findViewById<View>(R.id.background).visibility = View.VISIBLE
                }, 1000)
                //버튼 다시 살려내기
                first.findViewById<View>(R.id.goToTwo).visibility = View.VISIBLE
                first.findViewById<View>(R.id.goToThree).visibility = View.VISIBLE
                first.findViewById<View>(R.id.goToFour).visibility = View.VISIBLE
                first.findViewById<View>(R.id.goToFive).visibility = View.VISIBLE
            }

            first.findViewById<View>(R.id.background).setOnClickListener(closeSpotlight)

            //다이어리로 넘어가기
            val goToDiary= View.OnClickListener { val intent = Intent(this, DiaryActivity::class.java)
                intent.putExtra("nowDate", formatted)
                intent.putExtra("emotion", 1)
                intent.putExtra("diary_level",diary_level)
                intent.putExtra("user_name",user.useR_Name)
                intent.putExtra("USER_CoupleUID",user.useR_CoupleUID)
                startActivity(intent)
                spotlight.finish()
            }

            first.findViewById<View>(R.id.goToOne).setOnClickListener(goToDiary)
        }

        //2번젤리
        jelly_button2.setOnClickListener {
            val firstRoot = FrameLayout(this)
            val first = layoutInflater.inflate(R.layout.forspotlight, firstRoot)
            val firstTarget = Target.Builder()
                    .setAnchor(jelly_button2)
                    .setShape(Circle(120f))
                    .setOverlay(first)
                    .build()

            val spotlight = Spotlight.Builder(this@GoToDiary)
                    .setTargets(firstTarget)
                    .setBackgroundColorRes(R.color.spotlightBackground)
                    .setDuration(1000L)
                    .setAnimation(DecelerateInterpolator(2f))
                    .build()

            spotlight.start()

            //나머지 버튼가리기
            first.findViewById<View>(R.id.goToOne).visibility = View.GONE
            first.findViewById<View>(R.id.goToThree).visibility = View.GONE
            first.findViewById<View>(R.id.goToFour).visibility = View.GONE
            first.findViewById<View>(R.id.goToFive).visibility = View.GONE


            val closeSpotlight = View.OnClickListener {
                //잠시 버튼 죽이기
                first.findViewById<View>(R.id.goToTwo).visibility = View.GONE
                //레이아웃 터치 죽여야함
                first.findViewById<View>(R.id.background).visibility = View.GONE
                //스포트라이트 끄기
                spotlight.finish()
                //잠시 시간거쳐 애니메이션 효과후 살리기,백그라운드도 살리기
                Handler().postDelayed({
                    first.findViewById<View>(R.id.goToTwo).visibility = View.VISIBLE
                    first.findViewById<View>(R.id.background).visibility = View.VISIBLE
                }, 1000)
                //버튼 다시 살려내기
                first.findViewById<View>(R.id.goToOne).visibility = View.VISIBLE
                first.findViewById<View>(R.id.goToThree).visibility = View.VISIBLE
                first.findViewById<View>(R.id.goToFour).visibility = View.VISIBLE
                first.findViewById<View>(R.id.goToFive).visibility = View.VISIBLE
            }

            first.findViewById<View>(R.id.background).setOnClickListener(closeSpotlight)

            //다이어리로 넘어가기
            val goToDiary= View.OnClickListener { val intent = Intent(this, DiaryActivity::class.java)
                intent.putExtra("nowDate", formatted)
                intent.putExtra("emotion", 2)
                intent.putExtra("diary_level",diary_level)
                intent.putExtra("user_name",user.useR_Name)
                intent.putExtra("USER_CoupleUID",user.useR_CoupleUID)
                startActivity(intent)
                spotlight.finish()
            }

            first.findViewById<View>(R.id.goToTwo).setOnClickListener(goToDiary)

        }

        //3번젤리
        jelly_button3.setOnClickListener {
            val firstRoot = FrameLayout(this)
            val first = layoutInflater.inflate(R.layout.forspotlight, firstRoot)
            val firstTarget = Target.Builder()
                    .setAnchor(jelly_button3)
                    .setShape(Circle(120f))
                    .setOverlay(first)
                    .build()

            val spotlight = Spotlight.Builder(this@GoToDiary)
                    .setTargets(firstTarget)
                    .setBackgroundColorRes(R.color.spotlightBackground)
                    .setDuration(1000L)
                    .setAnimation(DecelerateInterpolator(2f))
                    .build()

            spotlight.start()

            //나머지 버튼가리기
            first.findViewById<View>(R.id.goToOne).visibility = View.GONE
            first.findViewById<View>(R.id.goToTwo).visibility = View.GONE
            first.findViewById<View>(R.id.goToFour).visibility = View.GONE
            first.findViewById<View>(R.id.goToFive).visibility = View.GONE

            val closeSpotlight = View.OnClickListener {
                //잠시 버튼 죽이기
                first.findViewById<View>(R.id.goToThree).visibility = View.GONE
                //레이아웃 터치 죽여야함
                first.findViewById<View>(R.id.background).visibility = View.GONE
                //스포트라이트 끄기
                spotlight.finish()
                //잠시 시간거쳐 애니메이션 효과후 살리기,백그라운드도 살리기
                Handler().postDelayed({
                    first.findViewById<View>(R.id.goToThree).visibility = View.VISIBLE
                    first.findViewById<View>(R.id.background).visibility = View.VISIBLE
                }, 1000)
                //버튼 다시 살려내기
                first.findViewById<View>(R.id.goToOne).visibility = View.VISIBLE
                first.findViewById<View>(R.id.goToTwo).visibility = View.VISIBLE
                first.findViewById<View>(R.id.goToFour).visibility = View.VISIBLE
                first.findViewById<View>(R.id.goToFive).visibility = View.VISIBLE
            }

            first.findViewById<View>(R.id.background).setOnClickListener(closeSpotlight)

            //다이어리로 넘어가기
            val goToDiary= View.OnClickListener { val intent = Intent(this, DiaryActivity::class.java)
                intent.putExtra("nowDate", formatted)
                intent.putExtra("emotion", 3)
                intent.putExtra("diary_level",diary_level)
                intent.putExtra("user_name",user.useR_Name)
                intent.putExtra("USER_CoupleUID",user.useR_CoupleUID)
                startActivity(intent)
                spotlight.finish()
            }

            first.findViewById<View>(R.id.goToThree).setOnClickListener(goToDiary)

        }

        //4번젤리
        jelly_button4.setOnClickListener {
            val firstRoot = FrameLayout(this)
            val first = layoutInflater.inflate(R.layout.forspotlight, firstRoot)
            val firstTarget = Target.Builder()
                    .setAnchor(jelly_button4)
                    .setShape(Circle(120f))
                    .setOverlay(first)
                    .build()

            val spotlight = Spotlight.Builder(this@GoToDiary)
                    .setTargets(firstTarget)
                    .setBackgroundColorRes(R.color.spotlightBackground)
                    .setDuration(1000L)
                    .setAnimation(DecelerateInterpolator(2f))
                    .build()

            spotlight.start()

            //나머지 버튼가리기
            first.findViewById<View>(R.id.goToOne).visibility = View.GONE
            first.findViewById<View>(R.id.goToTwo).visibility = View.GONE
            first.findViewById<View>(R.id.goToThree).visibility = View.GONE
            first.findViewById<View>(R.id.goToFive).visibility = View.GONE


            val closeSpotlight = View.OnClickListener {
                //잠시 버튼 죽이기
                first.findViewById<View>(R.id.goToFour).visibility = View.GONE
                //레이아웃 터치 죽여야함
                first.findViewById<View>(R.id.background).visibility = View.GONE
                //스포트라이트 끄기
                spotlight.finish()
                //잠시 시간거쳐 애니메이션 효과후 살리기,백그라운드도 살리기
                Handler().postDelayed({
                    first.findViewById<View>(R.id.goToFour).visibility = View.VISIBLE
                    first.findViewById<View>(R.id.background).visibility = View.VISIBLE
                }, 1000)
                //버튼 다시 살려내기
                first.findViewById<View>(R.id.goToOne).visibility = View.VISIBLE
                first.findViewById<View>(R.id.goToTwo).visibility = View.VISIBLE
                first.findViewById<View>(R.id.goToThree).visibility = View.VISIBLE
                first.findViewById<View>(R.id.goToFive).visibility = View.VISIBLE
            }

            first.findViewById<View>(R.id.background).setOnClickListener(closeSpotlight)

//            //다이어리로 넘어가기
            val goToDiary= View.OnClickListener { val intent = Intent(this, DiaryActivity::class.java)
                intent.putExtra("nowDate", formatted)
                intent.putExtra("emotion", 4)
                intent.putExtra("diary_level",diary_level)
                intent.putExtra("user_name",user.useR_Name)
                intent.putExtra("USER_CoupleUID",user.useR_CoupleUID)
                startActivity(intent)
                spotlight.finish()
            }

            first.findViewById<View>(R.id.goToFour).setOnClickListener(goToDiary)

        }

        //5번젤리
        jelly_button5.setOnClickListener {
            val firstRoot = FrameLayout(this)
            val first = layoutInflater.inflate(R.layout.forspotlight, firstRoot)
            val firstTarget = Target.Builder()
                    .setAnchor(jelly_button5)
                    .setShape(Circle(120f))
                    .setOverlay(first)
                    .build()

            val spotlight = Spotlight.Builder(this@GoToDiary)
                    .setTargets(firstTarget)
                    .setBackgroundColorRes(R.color.spotlightBackground)
                    .setDuration(1000L)
                    .setAnimation(DecelerateInterpolator(2f))
                    .build()

            spotlight.start()

            //나머지 버튼가리기
            first.findViewById<View>(R.id.goToOne).visibility = View.GONE
            first.findViewById<View>(R.id.goToTwo).visibility = View.GONE
            first.findViewById<View>(R.id.goToThree).visibility = View.GONE
            first.findViewById<View>(R.id.goToFour).visibility = View.GONE

            val closeSpotlight = View.OnClickListener { //잠시 버튼 죽이기
                first.findViewById<View>(R.id.goToFive).visibility = View.GONE
                //레이아웃 터치 죽여야함
                first.findViewById<View>(R.id.background).visibility = View.GONE
                //스포트라이트 끄기
                spotlight.finish()
                //잠시 시간거쳐 애니메이션 효과후 살리기,백그라운드도 살리기
                Handler().postDelayed({
                    first.findViewById<View>(R.id.goToFive).visibility = View.VISIBLE
                    first.findViewById<View>(R.id.background).visibility = View.VISIBLE
                }, 1000)
                //버튼 다시 살려내기
                first.findViewById<View>(R.id.goToOne).visibility = View.VISIBLE
                first.findViewById<View>(R.id.goToTwo).visibility = View.VISIBLE
                first.findViewById<View>(R.id.goToThree).visibility = View.VISIBLE
                first.findViewById<View>(R.id.goToFour).visibility = View.VISIBLE }

            first.findViewById<View>(R.id.background).setOnClickListener(closeSpotlight)

            //다이어리로 넘어가기
            val goToDiary= View.OnClickListener { val intent = Intent(this, DiaryActivity::class.java)
                intent.putExtra("nowDate", formatted)
                intent.putExtra("emotion", 5)
                intent.putExtra("diary_level",diary_level)
                intent.putExtra("user_name",user.useR_Name)
                intent.putExtra("USER_CoupleUID",user.useR_CoupleUID)
                startActivity(intent)
                spotlight.finish()
            }

            first.findViewById<View>(R.id.goToFive).setOnClickListener(goToDiary)

        }

    }

    //파이어스토어 정보 가져오기 민규 오리진
    fun getUserModel(Current_Uid: String?) {
        //이거 원래코드  var documentReference : DocumentReference = FirebaseFirestore.getInstance().collection("USER").document(Current_Uid) 이건데 let으로 오류 잡은거
        var documentReference : DocumentReference? = Current_Uid?.let { firestore?.collection("USER")?.document(it) }
        if (documentReference != null) {
            documentReference.get()
                    .addOnSuccessListener { document ->
                        if (document != null) {
                            var testuser: USER? = USER()
                            testuser = document.toObject(USER::class.java)
                            user = USER(testuser!!.useR_Name, testuser.useR_Gender, testuser.useR_NickName, testuser.useR_BirthY, testuser.useR_BirthM, testuser.useR_BirthD, testuser.useR_CoupleUID, testuser.useR_UID, testuser.useR_Level)
                        }
                    }
        }
    }

    //커플 일기수 (젤리수) 가져오기
    fun getCoupleJellyModel(Couple_UID: String?) {
        var documentReference : DocumentReference? = Couple_UID?.let { firestore?.collection("COUPLE")?.document(it) }
        if (documentReference != null) {
            documentReference.get()
                    .addOnSuccessListener { document ->
                        if (document != null) {
                            var testcouple: COUPLE? = COUPLE()
                            testcouple = document.toObject(COUPLE::class.java)
                            if (testcouple != null) {
                                couple = COUPLE(testcouple.couplE_LEVEL_MAN,testcouple.couplE_LEVEL_GIRL)
                            }
                        }
                    }
        }
    }



    companion object {
        @JvmStatic
        fun makeIntent(context: Context): Intent {
            return Intent(context, GoToDiary::class.java)
        }
    }
}