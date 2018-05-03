package com.rukus.service.rep

import com.rukus.model.UserProfile
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.transaction.annotation.Transactional

@Transactional
interface UserProfileRepository : JpaRepository<UserProfile, Int> {
}