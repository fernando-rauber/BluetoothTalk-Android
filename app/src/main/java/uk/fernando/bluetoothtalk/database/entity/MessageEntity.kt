package uk.fernando.bluetoothtalk.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.io.Serializable
import java.util.*

@Entity(
    tableName = MessageEntity.NAME,
    foreignKeys = [ForeignKey(entity = UserEntity::class, parentColumns = ["address"], childColumns = ["user_address"])]
)
data class MessageEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Long? = null,

    val message: String,
    val date: Date = Date(),
    val byMe: Boolean = false,
    @ColumnInfo(name = "user_address")
    val userAddress: String

) : Serializable {

    companion object {
        const val NAME = "message"
    }
}
