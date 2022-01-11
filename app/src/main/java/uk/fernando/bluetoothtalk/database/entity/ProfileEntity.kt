package uk.fernando.bluetoothtalk.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.util.*

@Entity(tableName = ProfileEntity.NAME)
data class ProfileEntity(
    @PrimaryKey
    var id: String,

    val name: String = "",
    val image: String? = null

) : Serializable {

    companion object {
        const val NAME = "profile"
    }
}
