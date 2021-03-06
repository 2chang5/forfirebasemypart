package bias.hugoandrade.calendarviewapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import bias.hugoandrade.calendarviewapp.data.Event;
import bias.hugoandrade.calendarviewapp.data.CALENDAR;
import bias.hugoandrade.calendarviewapp.data.USER;
import bias.hugoandrade.calendarviewapp.helpers.YMDCalendar;
import bias.hugoandrade.calendarviewapp.uihelpers.CalendarDialog;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class CalendarViewWithNotesActivitySDK21 extends AppCompatActivity  {

    private final static int CREATE_EVENT_REQUEST_CODE = 100;
    private final static int MOVE_FROM_LOGIN_REQUEST_CODE = 200;

    private String[] mShortMonths;
    private CalendarView mCalendarView;
    private CalendarDialog mCalendarDialog;
    private final List<Event> mEventList = new ArrayList<>();
    private final List<CALENDAR> mFire_EventList = new ArrayList<>();
    private ListenerRegistration listenerUsers;
    private FirebaseFirestore Firestore= FirebaseFirestore.getInstance();
    private static int shape;
//    private List<CALENDAR> sorted_CALENDARList = new ArrayList<>();
    private CalendarView.CalendarPagerAdapter calendarPagerAdapter;
    private USER user = new USER();
    private String CurrentUid;
    private FirebaseUser CurrentUser;
    private int DataYear = 0;
    private int DataMonth = 0;
    private List<String> Check_Month_Visited_List = new ArrayList<>();



    private List<View> monthview;
    private int sizex;

    public static Intent makeIntent(Context context) { return new Intent(context, CalendarViewWithNotesActivitySDK21.class); }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        CurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        CurrentUid =CurrentUser.getUid();
        Log.d("asddddd","CurrentUid : " + CurrentUid);
        getUserModel(CurrentUid);




    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //Log.d("?????????","onRestart() ?????? : ");
    }
    @Override
    protected void onResume() {
        super.onResume();
        //Log.d("?????????","onResume() ?????? : ");
    }


    private void initializeUI() {

        setContentView(R.layout.activity_calendar_view_with_notes_sdk_21);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        mCalendarView = findViewById(R.id.calendarView);
        mCalendarView.setOnMonthChangedListener(new CalendarView.OnMonthChangedListener() {
            @Override
            public void onMonthChanged(int month, int year) {
                if (getSupportActionBar() != null) {

                    DataYear = year;
                    DataMonth = month+1;
                    Log.d("asdddd","DataYear : " + DataYear);
                    Log.d("asddddd","DataMonth22 : " + DataMonth);
                    getSupportActionBar().setTitle(mShortMonths[month]);
                    getSupportActionBar().setSubtitle(Integer.toString(year));

                    String Gender = user.getUSER_Gender() == 0 ? "CALENDAR_GIRL" : "CALENDAR_MAN";
                    String O_Gender = user.getUSER_Gender() == 0 ? "CALENDAR_MAN" : "CALENDAR_GIRL";
                    listenerUsers = Firestore.collection("CALENDAR").document(user.getUSER_CoupleUID()).collection(O_Gender).document(DataYear + "_" + DataMonth).collection(DataYear + "_" + DataMonth)
                            .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(@Nullable QuerySnapshot value,
                                                    @Nullable FirebaseFirestoreException e) {
                                    if (e != null) { return; }
                                    Toast.makeText(getApplicationContext(), "??? ?????????(????????? ?????????)", Toast.LENGTH_LONG).show();
                                    int check = 0;
                                    List<CALENDAR> new_O_User_Calendar_List= new ArrayList<>();
                                    for (QueryDocumentSnapshot doc : value) {
                                        CALENDAR CALENDAR = create_event_firebase(doc);//????????? ?????? ???????????????????????? ?????? ??????????????? ????????? ??????????????????????????? ?????? ??????
                                        for(int i=0;i<mEventList.size();i++){
                                            if(mEventList.get(i).getCALENDAR_UID().equals(CALENDAR.getCALENDAR_UID())){
                                                check = 1;
                                                break;
                                            }
                                        }if(check == 0){
                                            new_O_User_Calendar_List.add(CALENDAR);
                                        }check = 0;
                                    }
                                    if(new_O_User_Calendar_List.size() != 0){
                                        for(int i=0;i<new_O_User_Calendar_List.size();i++){
                                            Insert_Calendar(convert_event(new_O_User_Calendar_List.get(i)));
                                        }
                                        mCalendarDialog.setEventList(mEventList);
                                    }
                                }
                            });


                    int check_month_visited = 0;
                    for(int i = 0 ; i < Check_Month_Visited_List.size() ; i++){
                        Log.d("zxc","Check_Month_Visited_List : " + Check_Month_Visited_List);
                        if(Check_Month_Visited_List.get(i).equals(DataYear + "_" + DataMonth)){
                            check_month_visited = 0;
                            break;
                        }else{
                            check_month_visited = 1;
                        }
                    }
                    if (check_month_visited == 1) {

                        CollectionReference collectionReference_girl = FirebaseFirestore.getInstance().collection("CALENDAR").document(user.getUSER_CoupleUID()).collection(Gender).document(DataYear + "_" + DataMonth).collection(DataYear + "_" + DataMonth);                // ????????????????????? posts??????
                            collectionReference_girl.get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                List<CALENDAR> sorted_CALENDARList = new ArrayList<>();
                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                    CALENDAR CALENDAR = create_event_firebase(document);
                                                    sorted_CALENDARList.add(CALENDAR);
                                                }
                                                Collections.sort(sorted_CALENDARList);
                                                for(int i = 0; i< sorted_CALENDARList.size() ; i++){
                                                    Insert_Calendar(convert_event(sorted_CALENDARList.get(i)));
                                                }
                                                mCalendarDialog.setEventList(mEventList);
                                            }
                                        }
                                    });
                            Check_Month_Visited_List.add(DataYear + "_" + DataMonth);
                    }

                    Log.d("zxc","Check_Month_Visited_List 222 : " + Check_Month_Visited_List);

                }
            }
        });

        /* ???????????? ????????? ????????? ?????? ?????? ?????? ?????????*/
        mCalendarView.setOnItemClickedListener(new CalendarView.OnItemClickListener() {
            @Override
            public void onItemClicked(List<CalendarView.CalendarObject> calendarObjects,
                                      Calendar previousDate,
                                      Calendar selectedDate) {

                if (calendarObjects.size() != 0) {
                    mCalendarDialog.setSelectedDate(selectedDate);
                    mCalendarDialog.show();
                }
                else {
                    if (diffYMD(previousDate, selectedDate) == 0)
                        createEvent(selectedDate);
                }
            }
        });

        DataMonth = mCalendarView.getCurrentDate().get(Calendar.MONTH)+1;
        DataYear = mCalendarView.getCurrentDate().get(Calendar.YEAR);

        From_Login();

        Log.d("asddddd","DataMonth : " + DataMonth);
        /* ???????????? ????????? ????????? ?????? ?????? ?????? ?????????*/
        mCalendarView.setOnItemTouchedListener(new CalendarView.OnItemTouchListener() {
            @Override
            public void onItemTouched(String title, int count, Calendar selectedDate, Calendar startDate, String Uid) {

                String Gender = user.getUSER_Gender() == 0 ? "CALENDAR_GIRL" : "CALENDAR_MAN";
                CollectionReference collectionReference_man = FirebaseFirestore.getInstance().collection("CALENDAR").document(user.getUSER_CoupleUID()).collection(Gender).document(DataYear + "_" + DataMonth).collection(DataYear + "_" + DataMonth);                // ????????????????????? posts??????
                collectionReference_man.whereEqualTo("CALENDAR_UID",Uid).get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        CALENDAR CALENDAR = create_event_firebase(document);
                                        Event event = convert_event(CALENDAR);
                                        Drag_Insert_Calendar(event);
                                    }mCalendarDialog.setEventList(mEventList);
                                }
                                FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
                                final DocumentReference Calendar_Docu =firebaseFirestore.collection("CALENDAR").document(user.getUSER_CoupleUID());
                                final DocumentReference Gender_Docu = Calendar_Docu.collection(Gender).document(DataYear + "_" + DataMonth);
                                final DocumentReference documentReference =Gender_Docu.collection(DataYear + "_" + DataMonth).document(Uid);

                                Calendar endDate = YMDCalendar.toCalendar(new YMDCalendar(selectedDate.get(Calendar.DATE)+count-1,selectedDate.get(Calendar.MONTH), selectedDate.get(Calendar.YEAR)));

                                String id = generateID();
                                CALENDAR EventFirebase = new CALENDAR(
                                        Uid,
                                        id,
                                        title,
                                        startDate.get(Calendar.YEAR),
                                        startDate.get(Calendar.MONTH),
                                        startDate.get(Calendar.DATE),
                                        endDate.get(Calendar.YEAR),
                                        endDate.get(Calendar.MONTH),
                                        endDate.get(Calendar.DATE),
                                        startDate.get(Calendar.YEAR),
                                        startDate.get(Calendar.MONTH),
                                        startDate.get(Calendar.DATE),
                                        count
                                );

                                Event mOriginalEventFirebase = new Event(
                                        Uid,
                                        id,
                                        title,
                                        YMDCalendar.toCalendar(new YMDCalendar(startDate.get(Calendar.DATE),startDate.get(Calendar.MONTH),startDate.get(Calendar.YEAR))),
                                        YMDCalendar.toCalendar(new YMDCalendar(startDate.get(Calendar.DATE),startDate.get(Calendar.MONTH),startDate.get(Calendar.YEAR))),
                                        YMDCalendar.toCalendar(new YMDCalendar(endDate.get(Calendar.DATE),endDate.get(Calendar.MONTH),endDate.get(Calendar.YEAR))),
                                        count
                                );

                                storeUpload(documentReference, EventFirebase);
                                Insert_Calendar(mOriginalEventFirebase);
                                mCalendarDialog.setEventList(mEventList);
                            }
                        });
            }
        });

        /* ????????? */
        if (getSupportActionBar() != null) {
            int month = mCalendarView.getCurrentDate().get(Calendar.MONTH);
            int year = mCalendarView.getCurrentDate().get(Calendar.YEAR);
            getSupportActionBar().setTitle(mShortMonths[month]);
            getSupportActionBar().setSubtitle(Integer.toString(year));
        }

        /* ?????? ?????? ??????*/
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createEvent(mCalendarView.getSelectedDate());
            }
        });

        /* ??????????????? ????????? ???????????????*/
        mCalendarDialog = CalendarDialog.Builder.instance(this)
                .setEventList(mEventList)
                .setOnItemClickListener(new CalendarDialog.OnCalendarDialogListener() {
                    @Override
                    public void onEventClick(Event event) {
                        onEventSelected(event);
                    }

                    @Override
                    public void onCreateEvent(Calendar calendar) {
                        createEvent(calendar);
                    }
                    @Override
                    public void onRightClick(Event event){ Drag_Insert_Calendar(event); mCalendarDialog.setEventList(mEventList);}
                })
                .create();
    }

    private static String generateID() {
        return Long.toString(System.currentTimeMillis());
    }

    private void storeUpload(DocumentReference documentReference, final CALENDAR mOriginalEventFirebase) {
        documentReference.set(mOriginalEventFirebase.getScheduleInfo())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
    }

    private void onEventSelected(Event event) {
        Activity context = CalendarViewWithNotesActivitySDK21.this;
        Intent intent = Create_Schadule.Revise_Schadle_Intent(context, event);
        startActivityForResult(intent, CREATE_EVENT_REQUEST_CODE);
        overridePendingTransition( R.anim.slide_in_up, R.anim.stay );
    }

    private void createEvent(Calendar selectedDate) {
        Activity context = CalendarViewWithNotesActivitySDK21.this;
        Intent intent = Create_Schadule.Create_Schadle_Intent(context, selectedDate);
        startActivityForResult(intent, CREATE_EVENT_REQUEST_CODE);
        overridePendingTransition( R.anim.slide_in_up, R.anim.stay );
    }

    private void DragcreateEvent(String title, int count, Calendar selectedDate, String Uid) {
        Activity context = CalendarViewWithNotesActivitySDK21.this;
        Intent intent = DragCreate_Schedule.DragCreate_Schedle_Intent(context, selectedDate);
        intent.putExtra("DragTitle", title);
        intent.putExtra("DragCount", count);
        intent.putExtra("DragEndDate", selectedDate);
        intent.putExtra("DragUid", Uid);
        startActivityForResult(intent, CREATE_EVENT_REQUEST_CODE);
        overridePendingTransition( R.anim.slide_in_up, R.anim.stay );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar_calendar_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_today: {
                mCalendarView.setSelectedDate(Calendar.getInstance());
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    /* ?????? ?????????????????? ????????? ????????? ??????*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CREATE_EVENT_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                int Create_action = Create_Schadule.extractActionFromIntent(data);
                Event Create_event = Create_Schadule.extractEventFromIntent(data);

                switch (Create_action) {
                    case Create_Schadule.ACTION_CREATE: {
                        Insert_Calendar(Create_event);
                        mCalendarDialog.setEventList(mEventList);
                        break;
                    }

                    case Create_Schadule.ACTION_EDIT: {
//                        Event oldEvent = null;
//                        for (Event e : mEventList) {
//                            if (Objects.equals(event.getID(), e.getID())) {
//                                oldEvent = e;
//                                break;
//                            }
//                        }
//                        if (oldEvent != null) {
//                            mEventList.remove(oldEvent);
//                            mEventList.add(event);
//
//                            mCalendarView.removeCalendarObjectByID(parseCalendarObject(oldEvent));
//                            mCalendarView.addCalendarObject(parseCalendarObject(event));
//                            mCalendarDialog = CalendarDialog.Builder.instance(this)
//                                    .setEventList(mEventList)
//                                    .setOnItemClickListener(new CalendarDialog.OnCalendarDialogListener() {
//                                        @Override
//                                        public void onEventClick(Event event) {
//                                            onEventSelected(event);
//                                        }
//
//                                        @Override
//                                        public void onCreateEvent(Calendar calendar) {
//                                            createEvent(calendar);
//                                        }
//                                    })
//                                    .create();
//                        }
                        break;
                    }
                }
            }else if (resultCode == DragCreate_Schedule.RESULT_DRAG) {
                int Drag_action = DragCreate_Schedule.extractActionFromIntent(data);
                Event Drag_event = DragCreate_Schedule.extractEventFromIntent(data);
                switch (Drag_action) {
                    case DragCreate_Schedule.ACTION_DRAG: {
                        Insert_Calendar(Drag_event);
                        mCalendarDialog.setEventList(mEventList);
                        break;
                    }
                }
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /* ????????? ????????? ????????? ????????? ??????*/
    public static int diffYMD(Calendar date1, Calendar date2) {
        if (date1.get(Calendar.YEAR) == date2.get(Calendar.YEAR) &&
                date1.get(Calendar.MONTH) == date2.get(Calendar.MONTH) &&
                date1.get(Calendar.DAY_OF_MONTH) == date2.get(Calendar.DAY_OF_MONTH))
            return 0;

        return date1.before(date2) ? -1 : 1;
    }

    /* ????????? ????????? ???????????? object??? ?????????
    171?????? ???  mCalendarView.addCalendarObject(parseCalendarObject(event));
    ??? ????????? ??????*/
    private static CalendarView.CalendarObject parseCalendarObject(Event event) {
        return new CalendarView.CalendarObject(
                event.getCALENDAR_id(),
                event.getCALENDAR_MarkedDate(),
                event.getCALENDAR_StartDate(),
                event.getCALENDAR_EndDate(),
                event.getCALENDAR_UID(),
                shape,
                event.getCALENDAR_DateCount());
    }

    //Event_firebase ??? ???????????? ??????
    CALENDAR create_event_firebase(QueryDocumentSnapshot doc){
        CALENDAR e_firebase = new CALENDAR(
                doc.getData().get("CALENDAR_UID").toString(),
                doc.getData().get("CALENDAR_id").toString(),
                doc.getData().get("CALENDAR_Schedule").toString(),
                YMDCalendar.toCalendar(new YMDCalendar(Integer.parseInt(doc.getData().get("CALENDAR_StartD").toString()),
                        Integer.parseInt(doc.getData().get("CALENDAR_StartM").toString()),
                        Integer.parseInt(doc.getData().get("CALENDAR_StartY").toString())
                )),
                YMDCalendar.toCalendar(new YMDCalendar(Integer.parseInt(doc.getData().get("CALENDAR_EndD").toString()),
                        Integer.parseInt(doc.getData().get("CALENDAR_EndM").toString()),
                        Integer.parseInt(doc.getData().get("CALENDAR_EndY").toString())
                )),
                YMDCalendar.toCalendar(new YMDCalendar(Integer.parseInt(doc.getData().get("CALENDAR_FixD").toString()),
                        Integer.parseInt(doc.getData().get("CALENDAR_FixM").toString()),
                        Integer.parseInt(doc.getData().get("CALENDAR_FixY").toString())
                )),
                Integer.parseInt(doc.getData().get("CALENDAR_DateCount").toString())
        );
        return e_firebase;
    }

    //???????????? ???????????? event??? ???????????? ??????(Event_firebase -> Event)
    Event convert_event(CALENDAR CALENDAR){
        Event C_event = new Event(CALENDAR.getCALENDAR_UID(), CALENDAR.getCALENDAR_id(), CALENDAR.getCALENDAR_Schedule(),
                CALENDAR.getCALENDAR_StartDate(), CALENDAR.getCALENDAR_StartDate(), CALENDAR.getCALENDAR_EndDate(), CALENDAR.getCALENDAR_DateCount());
        return C_event;
    }

    //?????? day??? Event_firebase??? date??? ?????? ??? convert_event??? ?????? ?????????.
    Event day_convert_event(CALENDAR CALENDAR, Calendar day, Calendar startday){
        Calendar date = YMDCalendar.toCalendar(new YMDCalendar(day.get(Calendar.DATE), day.get(Calendar.MONTH), day.get(Calendar.YEAR)));
        CALENDAR f_event=new CALENDAR(CALENDAR.getCALENDAR_UID(), CALENDAR.getCALENDAR_id(), CALENDAR.getCALENDAR_Schedule(),
                startday, CALENDAR.getCALENDAR_EndDate(),startday, CALENDAR.getCALENDAR_DateCount());
        Event c_event = new Event(); //?????????
        c_event =convert_event(f_event);   //????????? ?????? event_firebase -> evnet ????????? ??????
        c_event.setCALENDAR_MarkedDate(date);
        return c_event;
    }

    Event event_convert_event(Event event, Calendar day, Calendar startday){
        Calendar date = YMDCalendar.toCalendar(new YMDCalendar(day.get(Calendar.DATE), day.get(Calendar.MONTH), day.get(Calendar.YEAR)));
        Event f_event=new Event(event.getCALENDAR_UID(),event.getCALENDAR_id(),event.getCALENDAR_Schedule(),
                startday,startday,event.getCALENDAR_EndDate(), event.getCALENDAR_DateCount());
        f_event.setCALENDAR_MarkedDate(date);
        return f_event;
    }

    void Insert_Calendar(Event event){
        Calendar day = event.getCALENDAR_StartDate();
        Calendar endday = event.getCALENDAR_EndDate();
        Calendar startday = event.getCALENDAR_MarkedDate();
        if(day.get(Calendar.DATE) == (endday.get(Calendar.DATE)) && day.get(Calendar.MONTH) == (endday.get(Calendar.MONTH))){
            shape = 0;
            mEventList.add(event);
            mCalendarView.addCalendarObject(parseCalendarObject(event));
        } else{
            int count = 0;
            while (!day.after(endday)){
                if(day.get(Calendar.DATE) == (endday.get(Calendar.DATE))){    //?????????
                    shape = 2;
                } else{   //??????
                    if(count == 0){
                        shape = 1;
                    }else{
                        shape = 3;
                    }
                }
                count++;
                Event c_event = new Event(); //?????????
                c_event = event_convert_event(event,day,startday); //????????? day??? ???????????? ??????
                mEventList.add(c_event);
                mCalendarView.addCalendarObject(parseCalendarObject(c_event));
                day.add(Calendar.DATE,1);
            }day.add(Calendar.DATE,-count);
        }
    }

    public void Drag_Insert_Calendar(Event event){
        Log.d("??????","11111111111111111 : ");
        Calendar startday = event.getCALENDAR_StartDate();
        Calendar endday = event.getCALENDAR_EndDate();
        Calendar markedday = event.getCALENDAR_MarkedDate();
        if(startday.get(Calendar.DATE) == (endday.get(Calendar.DATE)) && startday.get(Calendar.MONTH) == (endday.get(Calendar.MONTH))){
            shape = 0;
            mEventList.remove(event);
            mCalendarView.removeCalendarObjectByID(parseCalendarObject(event));
            //mCalendarDialog.setEventList(mEventList);
        } else{
            ArrayList<Event> oldEvent = new ArrayList<>();
            for (Event e : mEventList) {
                if (Objects.equals(event.getCALENDAR_UID(), e.getCALENDAR_UID())) {
                    oldEvent.add(e);
                }
            }
            for(int i = 0; i < oldEvent.size() ; i++){
                mEventList.remove(oldEvent.get(i));
                mCalendarView.removeCalendarObjectByID(parseCalendarObject(oldEvent.get(i)));
            }
        }
    }

    void From_Login(){
        int logincheck = getIntent().getIntExtra("MOVE_FROM_LOGIN_REQUEST_CODE",0);
        //if(MOVE_FROM_LOGIN_REQUEST_CODE == logincheck){
        Check_Month_Visited_List.add(DataYear + "_" + DataMonth);
        Log.d("zxc","Check_Month_Visited_List0000 : " + Check_Month_Visited_List);
        CollectionReference collectionReference_man = FirebaseFirestore.getInstance().collection("CALENDAR").document(user.getUSER_CoupleUID()).collection("CALENDAR_MAN").document(DataYear + "_" + DataMonth).collection(DataYear + "_" + DataMonth);                // ????????????????????? posts??????
        collectionReference_man.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<CALENDAR> sorted_CALENDARList = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                CALENDAR CALENDAR = create_event_firebase(document);
                                sorted_CALENDARList.add(CALENDAR);
                            }
                            Collections.sort(sorted_CALENDARList);
                            for(int i = 0; i< sorted_CALENDARList.size() ; i++){
                                Insert_Calendar(convert_event(sorted_CALENDARList.get(i)));
                            }
                            mCalendarDialog.setEventList(mEventList);
                        }
                    }
                });
        CollectionReference collectionReference_girl = FirebaseFirestore.getInstance().collection("CALENDAR").document(user.getUSER_CoupleUID()).collection("CALENDAR_GIRL").document(DataYear + "_" + DataMonth).collection(DataYear + "_" + DataMonth);                // ????????????????????? posts??????
        collectionReference_girl.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<CALENDAR> sorted_CALENDARList = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                CALENDAR CALENDAR = create_event_firebase(document);
                                sorted_CALENDARList.add(CALENDAR);
                            }
                            Collections.sort(sorted_CALENDARList);
                            for(int i = 0; i< sorted_CALENDARList.size() ; i++){
                                Insert_Calendar(convert_event(sorted_CALENDARList.get(i)));
                            }
                            mCalendarDialog.setEventList(mEventList);
                        }
                    }
                });
        logincheck = 0;
        // }
    }

    public void getUserModel(String CurrentUid){
        final DocumentReference documentReference = FirebaseFirestore.getInstance().collection("USER").document(CurrentUid);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        if (document.exists()) {  //???????????? ????????????
                            USER test = new USER();
                            test =  document.toObject(USER.class);
                            user = new USER(test.getUSER_Name(),test.getUSER_Gender(),test.getUSER_NickName(),test.getUSER_BirthY()
                                    ,test.getUSER_BirthM(),test.getUSER_BirthD(),test.getUSER_CoupleUID(),test.getUSER_UID(),test.getUSER_Level());
                            Log.d("asddddd","user.getUSER_CoupleUID()2222 : " + user.getUSER_CoupleUID());

                            mShortMonths = new DateFormatSymbols().getShortMonths();
                            initializeUI();
                        }
                    }
                }
            }
        });
    }

}