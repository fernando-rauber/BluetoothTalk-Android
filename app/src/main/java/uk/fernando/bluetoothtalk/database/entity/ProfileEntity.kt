package uk.fernando.bluetoothtalk.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = ProfileEntity.NAME)
data class ProfileEntity(
    @PrimaryKey
    var id: String,

    var name: String = "",
    var photo: String? = null

) : Serializable {

    companion object {
        const val NAME = "profile"
    }
}
