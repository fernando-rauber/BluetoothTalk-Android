package uk.fernando.bluetoothtalk.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.io.Serializable
import java.util.*

@Entity(
    tableName = MessageEntity.NAME,
    foreignKeys = [ForeignKey(entity = UserEntity::class, parentColumns = ["id"], childColumns = ["user_id"])]
)
data class MessageEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Long? = null,

    val message: String,
    val date: Date = Date(),
    val byMe: Boolean = false,
    val sent: Boolean = false,
    @ColumnInfo(name = "user_id", index = true)
    val userId: String

) : Serializable {

    companion object {
        const val NAME = "message"
    }
}
