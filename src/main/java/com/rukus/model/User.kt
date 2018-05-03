package com.rukus.model

import org.hibernate.annotations.Proxy
import java.io.Serializable
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "APP_USER")
@Proxy(lazy = false)
class User : Serializable {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int? = null

    @Column(name = "PASSWORD", nullable = false)
    var password: String? = null

    @Column(name = "FIRST_NAME", nullable = false)
    var firstName: String? = null

    @Column(name = "LAST_NAME", nullable = false)
    var lastName: String? = null

    @Column(name = "EMAIL", unique = true, nullable = false)
    var email: String? = null

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "APP_USER_USER_PROFILE", joinColumns = [(JoinColumn(name = "USER_ID"))], inverseJoinColumns = [(JoinColumn(name = "USER_PROFILE_ID"))])
    var userProfiles: Set<UserProfile> = HashSet()

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    var bookings: List<Booking> = ArrayList()

    override fun hashCode(): Int {
        val prime = 31
        var result = 1
        result = prime * result + if (id == null) 0 else id!!.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other)
            return true
        if (other == null)
            return false
        if (other !is User)
            return false
        val oth = other as User?
        if (id == null) {
            if (oth!!.id != null)
                return false
        } else if (id != oth!!.id)
            return false
        return true
    }

    /*
     * DO-NOT-INCLUDE passwords in toString function.
     * It is done here just for convenience purpose.
     */
    override fun toString(): String {
        return ("User [id=" + id + ", password=" + password
                + ", firstName=" + firstName + ", lastName=" + lastName
                + ", email=" + email + "]")
    }

}
