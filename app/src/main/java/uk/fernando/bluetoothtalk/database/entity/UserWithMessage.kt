package uk.fernando.bluetoothtalk.database.entity

import androidx.room.Embedded
import androidx.room.Relation
import java.io.Serializable

data class UserWithMessage (

    @Embedded val user: UserEntity,

    @Relation(parentColumn = "id", entityColumn = "user_id", entity = MessageEntity::class)
    val messageList: List<MessageEntity>
) : Serializable