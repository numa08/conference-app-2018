package io.github.droidkaigi.confsched2018.data.api.response.mapper

import io.github.droidkaigi.confsched2018.data.api.response.*
import io.github.droidkaigi.confsched2018.data.db.entity.*

fun List<Session>?.toSessionSpeakerJoinEntities(): List<SessionSpeakerJoinEntity> {
    val sessionSpeakerJoinEntity: MutableList<SessionSpeakerJoinEntity> = arrayListOf()
    this!!.forEach { responseSession ->
        responseSession.speakers!!.forEach { speakerId ->
            sessionSpeakerJoinEntity += SessionSpeakerJoinEntity(responseSession.id!!, speakerId!!)
        }
    }
    return sessionSpeakerJoinEntity
}

fun List<Session>?.toSessionEntities(categories: List<Category>?, rooms: List<io.github.droidkaigi.confsched2018.data.api.response.Room>?): List<SessionEntity> =
        this!!.map { responseSession ->
            responseSession.toSessionEntity(categories, rooms)
        }

fun Session.toSessionEntity(categories: List<Category>?, rooms: List<Room>?): SessionEntity {
    val sessionFormt = categories.category(0, categoryItems!![0])
    val language = categories.category(1, categoryItems[1])
    val topic = categories.category(2, categoryItems[2])
    val level = categories.category(3, categoryItems[3])
    return SessionEntity(
            id = id!!,
            title = title!!,
            desc = description!!,
            stime = startsAt!!,
            etime = endsAt!!,
            sessionFormat = sessionFormt.name!!,
            language = language.name!!,
            topic = TopicEntity(topic.id!!, topic.name!!),
            level = LevelEntity(level.id!!, level.name!!),
            room = RoomEntity(roomId!!, rooms.roomName(roomId))
    )
}

fun List<Speaker>?.toSpeakerEntities(): List<SpeakerEntity> =
        this!!.map { responseSpeaker ->
            SpeakerEntity(id = responseSpeaker.id!!,
                    name = responseSpeaker.fullName!!,
                    imageUrl = responseSpeaker.profilePicture.orEmpty(),
                    twitterUrl = responseSpeaker.links
                            ?.firstOrNull { "Twitter" == it?.linkType }
                            ?.url,
                    companyUrl = responseSpeaker.links
                            ?.firstOrNull { "Company_Website" == it?.linkType }
                            ?.url,
                    blogUrl = responseSpeaker.links
                            ?.firstOrNull { "Blog" == it?.linkType }
                            ?.url,
                    githubUrl = responseSpeaker.links
                            ?.firstOrNull { "GitHub" == it?.title || "Github" == it?.title }
                            ?.url
            )
        }

private fun List<Category>?.category(categoryIndex: Int, categoryId: Int?): CategoryItem =
        this!![categoryIndex].items!!.first { it!!.id == categoryId }!!

private fun List<Room>?.roomName(roomId: Int?): String =
        this!!.first { it.id == roomId }.name!!
