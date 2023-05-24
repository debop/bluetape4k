package io.bluetape4k.spring.security.provisioning

import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsPasswordService
import org.springframework.security.provisioning.UserDetailsManager

class RedisUserDetailManager: UserDetailsManager, UserDetailsPasswordService {

    override fun loadUserByUsername(username: String?): UserDetails {
        TODO("Not yet implemented")
    }

    override fun createUser(user: UserDetails?) {
        TODO("Not yet implemented")
    }

    override fun updateUser(user: UserDetails?) {
        TODO("Not yet implemented")
    }

    override fun deleteUser(username: String?) {
        TODO("Not yet implemented")
    }

    override fun changePassword(oldPassword: String?, newPassword: String?) {
        TODO("Not yet implemented")
    }

    override fun userExists(username: String?): Boolean {
        TODO("Not yet implemented")
    }

    override fun updatePassword(user: UserDetails?, newPassword: String?): UserDetails {
        TODO("Not yet implemented")
    }
}
