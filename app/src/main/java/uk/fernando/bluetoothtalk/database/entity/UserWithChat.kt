package uk.fernando.bluetoothtalk.database.entity

import androidx.room.Embedded
import androidx.room.Relation
import java.io.Serializable

data class UserWithChat (

    @Embedded val user: UserEntity,

    @Relation(parentColumn = "address", entityColumn = "user_address", entity = MessageEntity::class)
    val chatList: List<MessageEntity>
) : Serializable