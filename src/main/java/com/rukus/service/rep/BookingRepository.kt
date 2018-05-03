package com.rukus.service.rep

import com.rukus.model.Booking
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.transaction.annotation.Transactional

@Transactional
interface BookingRepository : JpaRepository<Booking, Int> {
    fun findByStatus(status: String): List<Booking>
}