package bias.hugoandrade.calendarviewapp.data

import java.io.Serializable
import java.util.*

data class DIARY ( val DIARY_Body: String? = null,
                  val DIARY_Y: Int? = null,
                  val DIARY_M: Int? = null,
                  val DIARY_D: Int? = null,
                  val DIARY_Photo: String? = null,
                  val DIARY_Open_Time: Int? = null,
                  val DIARY_Id: String? = null,
                  val DIARY_Uid: String? = null,) {

    fun getDiaryInfo(): Map<String, Any>? {
        val docData = hashMapOf(
            "DIARY_Body" to "DIARY_Body",
            "DIARY_Y" to "DIARY_Y",
            "DIARY_M" to "DIARY_M",
            "DIARY_D" to "DIARY_D",
            "DIARY_Photo" to "DIARY_Photo",
            "DIARY_Open_Time" to "DIARY_Open_Time",
            "DIARY_Id" to "DIARY_Id",
            "DIARY_Uid" to "DIARY_Uid",
            )
        return docData
    }

}