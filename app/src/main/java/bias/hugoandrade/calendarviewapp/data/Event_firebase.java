package bias.hugoandrade.calendarviewapp.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Hugo Andrade on 25/03/2018.
 */

/* 여기에 getter setter 박고 파베랑 연동하면 쓸수 있지 않을까? */
public class Event_firebase implements Parcelable, Comparable<Event_firebase>{


    private String CALENDAR_UID;
    private String CALENDAR_id;
    private String CALENDAR_Schedule;

    private Calendar CALENDAR_StrartDate;
    private Calendar CALENDAR_EndDate;
    private Calendar CALENDAR_FixDate;
//    private int Color;
//    private boolean IsCompleted;

    private int CALENDAR_StartY;
    private int CALENDAR_StartM;
    private int CALENDAR_StartD;

    private int CALENDAR_FixY;
    private int CALENDAR_FixM;
    private int CALENDAR_FixD;

    private int CALENDAR_EndY;
    private int CALENDAR_EndM;
    private int CALENDAR_EndD;

    private int CALENDAR_DateCount;

    public Event_firebase(){}

    public Event_firebase(String CALENDAR_UID, String CALENDAR_id, String CALENDAR_Schedule, Calendar CALENDAR_StrartDate, Calendar CALENDAR_EndDate, Calendar CALENDAR_FixDate, int CALENDAR_DateCount) {
        this.CALENDAR_UID = CALENDAR_UID;
        this.CALENDAR_id = CALENDAR_id;
        this.CALENDAR_Schedule = CALENDAR_Schedule;
        this.CALENDAR_StrartDate = CALENDAR_StrartDate;
        this.CALENDAR_EndDate = CALENDAR_EndDate;
        this.CALENDAR_FixDate = CALENDAR_FixDate;
        this.CALENDAR_DateCount = CALENDAR_DateCount;
    }

    public Event_firebase(String CALENDAR_UID, String CALENDAR_id, String CALENDAR_Schedule, int CALENDAR_StartY, int CALENDAR_StartM, int CALENDAR_StartD, int CALENDAR_EndY, int CALENDAR_EndM, int CALENDAR_EndD, int CALENDAR_FixY, int CALENDAR_FixM, int CALENDAR_FixD, int CALENDAR_DateCount) {
        this.CALENDAR_UID = CALENDAR_UID;
        this.CALENDAR_id = CALENDAR_id;
        this.CALENDAR_Schedule = CALENDAR_Schedule;
        this.CALENDAR_StartY = CALENDAR_StartY;
        this.CALENDAR_StartM = CALENDAR_StartM;
        this.CALENDAR_StartD = CALENDAR_StartD;
        this.CALENDAR_EndY = CALENDAR_EndY;
        this.CALENDAR_EndM = CALENDAR_EndM;
        this.CALENDAR_EndD = CALENDAR_EndD;
        this.CALENDAR_FixY = CALENDAR_FixY;
        this.CALENDAR_FixM = CALENDAR_FixM;
        this.CALENDAR_FixD = CALENDAR_FixD;
        this.CALENDAR_DateCount = CALENDAR_DateCount;
    }




    public Map<String, Object> getScheduleInfo(){
        Map<String, Object> docData = new HashMap<>();
        docData.put("CALENDAR_UID", CALENDAR_UID);
        docData.put("CALENDAR_Schedule", CALENDAR_Schedule);
        docData.put("CALENDAR_StartY", CALENDAR_StartY);
        docData.put("CALENDAR_StartM", CALENDAR_StartM);
        docData.put("CALENDAR_StartD", CALENDAR_StartD);
        docData.put("CALENDAR_EndY", CALENDAR_EndY);
        docData.put("CALENDAR_EndM", CALENDAR_EndM);
        docData.put("CALENDAR_EndD", CALENDAR_EndD);
        docData.put("CALENDAR_FixY", CALENDAR_FixY);
        docData.put("CALENDAR_FixM", CALENDAR_FixM);
        docData.put("CALENDAR_FixD", CALENDAR_FixD);
        docData.put("CALENDAR_id", CALENDAR_id);
        docData.put("CALENDAR_DateCount", CALENDAR_DateCount);
        return  docData;
    }

    public String getCALENDAR_id() {
        return this.CALENDAR_id;
    }

    public String getCALENDAR_Schedule() {
        return this.CALENDAR_Schedule;
    }

    public Calendar getCALENDAR_StrartDate() {
        return this.CALENDAR_StrartDate;
    }
    public void setCALENDAR_StrartDate(Calendar CALENDAR_StrartDate) { this.CALENDAR_StrartDate = CALENDAR_StrartDate; }

    public Calendar getCALENDAR_EndDate() {
        return this.CALENDAR_EndDate;
    }
    public Calendar getCALENDAR_FixDate() {
        return this.CALENDAR_FixDate;
    }

    public int getCALENDAR_DateCount() {
        return this.CALENDAR_DateCount;
    }


    protected Event_firebase(Parcel in) {
        CALENDAR_UID = in.readString();
        CALENDAR_id = in.readString();
        CALENDAR_Schedule = in.readString();
        CALENDAR_StrartDate = (Calendar) in.readSerializable();
        CALENDAR_EndDate = (Calendar) in.readSerializable();
        CALENDAR_FixDate = (Calendar) in.readSerializable();
        CALENDAR_DateCount = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(CALENDAR_UID);
        dest.writeString(CALENDAR_id);
        dest.writeString(CALENDAR_Schedule);
        dest.writeSerializable(CALENDAR_StrartDate);
        dest.writeSerializable(CALENDAR_EndDate);
        dest.writeSerializable(CALENDAR_FixDate);
        dest.writeInt(CALENDAR_DateCount);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Event_firebase> CREATOR = new Creator<Event_firebase>() {
        @Override
        public Event_firebase createFromParcel(Parcel in) {
            return new Event_firebase(in);
        }

        @Override
        public Event_firebase[] newArray(int size) {
            return new Event_firebase[size];
        }
    };

    public String getCALENDAR_UID() {
        return this.CALENDAR_UID;
    }

    public void setCALENDAR_UID(String Event_Uid) {
        this.CALENDAR_UID = Event_Uid;
    }

    @Override
    public int compareTo(Event_firebase s) {
        if (this.CALENDAR_StrartDate.before(s.getCALENDAR_StrartDate())) {
            return -1;
        } else if (this.CALENDAR_StrartDate.after(s.getCALENDAR_StrartDate())) {
            return 1;
        }
        return 0;
    }

}
