package uk.fernando.bluetoothtalk.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.util.*

@Entity(tableName = UserEntity.NAME)
data class UserEntity(
    @PrimaryKey
    var address: String,

    val name: String,
    val image: String? = null

) : Serializable {

    companion object {
        const val NAME = "user"
    }
}
