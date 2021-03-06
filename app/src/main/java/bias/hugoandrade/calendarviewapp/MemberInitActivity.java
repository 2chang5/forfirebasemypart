package bias.hugoandrade.calendarviewapp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import bias.hugoandrade.calendarviewapp.data.USER;

//앱이 실행되고나면 로그인 후에 가장 먼저 보게 되는 액티비티로서, 사용자의 정보를 입력 받는다.
//      Ex) 메인프레그먼트에서는 이 MemberInitActivity가 실행되지 않으면, 계속 MemberInitActivity를 실행하게 된다.

public class MemberInitActivity extends AppCompatActivity {     // 1. 클래스 2. 변수 및 배열 3. Xml데이터(레이아웃, 이미지, 버튼, 텍스트, 등등) 4. 파이어베이스 관련 선언 5. 기타 변수
    // 2. 변수 및 배열
    private String SelectedImagePath;                           // 프로필 이미지
    private int USER_BirthY;
    private int USER_BirthM;
    private int USER_BirthD;

    private int USER_Gender;                           // 대학교
    // 3. Xml데이터(레이아웃, 이미지, 버튼, 텍스트, 등등)
    private ImageView Profile_ImageView;                        // 선택한 이미지를 넣은 ImageView
    private Button Users_Info_Send_Button;                      // 입력한 정보 등록 Button
    private EditText Nickname_EditText;                                  // 닉네임 EditText
    private EditText Name_EditText;
    // 4. 파이어베이스 관련 선언
    private FirebaseUser CurrentUser;                       //파이어베이스 데이터 상의 현재 사용자



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_init);
        //setToolbarTitle("");
        Intent intent = getIntent(); /*데이터 수신*/
        String getgender = null;
        getgender = intent.getStringExtra("getgender");
        String getbithday = null;
        getbithday = intent.getStringExtra("getbithday");
        int b_M = 0;
        int b_D = 1;
        if(getbithday != null){
            int birthday = Integer.parseInt(getbithday);
            b_M = (birthday/100)-1;
            b_D = birthday%100;
        }

        // 현재 사용자를 파이어베이스에서 받아옴
        CurrentUser= FirebaseAuth.getInstance().getCurrentUser();

        // 진행중 레이아웃, 프로필 ImageView, 회원정보 등록 Button, 닉네임 EditText find
        Profile_ImageView = findViewById(R.id.Users_Profile_ImageView);
        Users_Info_Send_Button = findViewById(R.id.Users_Info_Send_Button);
        Nickname_EditText =  findViewById(R.id.Nickname);
        Name_EditText =  findViewById(R.id.Name);

        // 프로필 ImageView, 회원정보 등록 Button setOnClickListener
        Profile_ImageView.setOnClickListener(onClickListener);
        Users_Info_Send_Button.setOnClickListener(onClickListener);

        // 닉네임을 이메일 @앞 부분으로 HINT로 임의지정
        Nickname_EditText.setHint(" "+extractIDFromEmail(CurrentUser.getEmail()));

        // DatePicker를 이용해 생일 지정, 받아서 String 값으로 저장함
        DatePicker BirthDay_Picker = findViewById(R.id.BirthDay_Picker);
        USER_BirthY = 2021;
        USER_BirthM = b_M;
        USER_BirthD = b_D;

        BirthDay_Picker.init(2021, b_M, b_D, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                //BirthDay = year + "/" + (monthOfYear+1) + "/" + dayOfMonth;
                USER_BirthY = year;
                USER_BirthM = monthOfYear;
                USER_BirthD = dayOfMonth;
            }
        });

        // Spinner를 이용해 남,녀 자정
        Spinner Gender = findViewById(R.id.Gender);
        ArrayAdapter monthAdapter = ArrayAdapter.createFromResource(this, R.array.Gender, android.R.layout.simple_spinner_dropdown_item);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Gender.setAdapter(monthAdapter);
        /*
        if(!getgender.equals(null)&&getgender.equals("MALE")){
            Gender.setSelection(1);
        }else{
            Gender.setSelection(0);
        }

         */
        // 해당 listener로 스피너의 position으로 대학교를 결정
        Gender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                USER_Gender = position;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
    }

    // 뒤로 가면 종료
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        moveTaskToBack(true);
        finish();
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0: {

                // GalleryActivity에서 이미지를 골랐다면 RESULT_OK / 고르지 않았다면 RESULT_CANCELED 실행
                if (resultCode == Activity.RESULT_OK) {
                    SelectedImagePath = data.getStringExtra(Util.INTENT_PATH);
                    Glide.with(this).load(SelectedImagePath).centerCrop().override(500).into(Profile_ImageView);
                }
                if(resultCode == Activity.RESULT_CANCELED){
                    SelectedImagePath = null;
                    Glide.with(this).load(R.drawable.non_userprofile_v2).centerInside().into(Profile_ImageView);
                }
                break;
            }
        }
    }

    // 프로필 ImageView, 회원정보 등록 Button의 OnClickListener
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {

                // 프로필 ImageView 클릭시 : GalleryActivity 실행
                case R.id.Users_Profile_ImageView:
                    myStartActivity(GalleryActivity.class);
                    break;

                // 회원정보 등록 Button 클릭시 : MemberInit_Storage_Uploader 함수 실행행
                case R.id.Users_Info_Send_Button:

                    // 스토리지에 사진을 먼저 담는 함수
                    MemberInit_Storage_Uploader();
                    break;

            }
        }
    };

    //스토리지에 사진을 먼저 담는 함수
    private void MemberInit_Storage_Uploader() {

        // 입력받은 닉네임을 받아오고 20자가 넘으면 안됨
        final String USER_Nickname = Nickname_EditText.getText().toString();
        final String USER_Name = Name_EditText.getText().toString();

        if (USER_Nickname.length() < 20) {

            // 파이어베이스 스토리지에 대한 Reference와 현재 유저의 정보를 받아온다.
            FirebaseStorage Firebasestorage = FirebaseStorage.getInstance();
            StorageReference Storagereference = Firebasestorage.getReference();
            CurrentUser = FirebaseAuth.getInstance().getCurrentUser();

            String USER_CoupleUID = "";
            int USER_Level = 0;

            //스토리지의 USER/유저의 UID/이미지 들어가는곳  에다가 넣는다.
            final StorageReference ImageRef_USERS_Uid = Storagereference.child("USER/" + CurrentUser.getUid() + "/USERImage.jpg");

            // if : 선택한 이미지가 없다면 스토리지에 저장할 필요가 없다. ->  UserModel만 만들어서 MemberInit_Store_Uploader로 이동한다.
            if (SelectedImagePath == null) {
                USER user = new USER(USER_Name, USER_Gender, USER_Nickname, USER_BirthY, USER_BirthM, USER_BirthD, USER_CoupleUID, CurrentUser.getUid(), USER_Level);
                user.setUSER_UID(CurrentUser.getUid());
                if(USER_Nickname.equals("")){
                    user.setUSER_NickName(extractIDFromEmail(CurrentUser.getEmail()));
                }
                MemberInit_Store_Uploader(user);
            }
            // else : 선택한 이미지가 있다면, ImageRef_USERS_Uid로 지정한 해당 경로의 이름으로 프로필을 저장한다.
            else {
                try {
                    InputStream Stream = new FileInputStream(new File(SelectedImagePath));
                    UploadTask Uploadtask = ImageRef_USERS_Uid.putStream(Stream);
                    Uploadtask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            return ImageRef_USERS_Uid.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                // 이미지의 스토리지에 대한 저장이 끝났다면 스토어 저장을 위해 UserModel만 만들어서 MemberInit_Store_Uploader로 이동한다.
                                Uri DownloadUri = task.getResult();
                                USER user = new USER(USER_Name, USER_Gender, USER_Nickname, USER_BirthY, USER_BirthM, USER_BirthD, USER_CoupleUID, CurrentUser.getUid(), USER_Level, DownloadUri.toString());
                                user.setUSER_UID(CurrentUser.getUid());
                                if(USER_Nickname.equals("")){
                                    user.setUSER_NickName(extractIDFromEmail(CurrentUser.getEmail()));
                                }
                                MemberInit_Store_Uploader(user);
                            } else {
                                Util.showToast(MemberInitActivity.this, "회원정보를 보내는데 실패하였습니다.");
                            }
                        }
                    });
                } catch (FileNotFoundException e) {
                    Log.e("로그", "에러: " + e.toString());
                }
            }
        }else {
            Util.showToast(MemberInitActivity.this, "닉네임은 최대 20자까지 가능합나다.");
        }
    }

    //Usermodel에다 담은 회원정보를 파이어스토어 USERS/CurrentUser의 Uid에다가 넣는 함수
    private void MemberInit_Store_Uploader(final USER Usermodel) {
        FirebaseFirestore docSet_USERS_Uid = FirebaseFirestore.getInstance();
        docSet_USERS_Uid.collection("USER").document(CurrentUser.getUid()).set(Usermodel.getUSERInfo())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Util.showToast(MemberInitActivity.this, "회원정보 등록을 성공하였습니다.");
                        Intent intent = new Intent(getApplicationContext(), couple_askActivity.class);
                        //intent.putExtra("MOVE_FROM_LOGIN_REQUEST_CODE", MOVE_FROM_LOGIN_REQUEST_CODE);
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Util.showToast(MemberInitActivity.this, "회원정보 등록에 실패하였습니다.");
                    }
                });
    }

    // 이메일에서 @뒤로 잘라서 닉네임으로 이용한다.
    String extractIDFromEmail(String email){
        String[] parts = email.split("@");
        return parts[0];
    }

    private void myStartActivity(Class c) {
        Intent intent = new Intent(this, c);
        startActivityForResult(intent, 0);
    }
}