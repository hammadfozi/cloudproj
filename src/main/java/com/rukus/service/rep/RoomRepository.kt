package com.rukus.service.rep

import com.rukus.model.Room
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Transactional
interface RoomRepository : JpaRepository<Room, Int> {

    fun findByName(name: String): Optional<Room>

    @Query("SELECT m.* from Room AS m JOIN (SELECT DISTINCT d.* FROM Room AS d LEFT OUTER JOIN Booking AS c ON c.room_id = d.id WHERE (c.room_id IS NULL) ORDER BY d.name) AS t ON m.id = t.id", nativeQuery = true)
    fun findFreeRooms(): List<Room>
}