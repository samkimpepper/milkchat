package com.example.milkchat2.auth

import com.example.milkchat2.user.model.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.core.userdetails.UserDetails

class UserDetailsImpl(val user: User) : UserDetails {
    var enabled: Boolean = true

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> =
            AuthorityUtils.createAuthorityList("ROLE_USER")

    override fun getPassword() = user.password

    override fun getUsername() = user.email

    override fun isAccountNonExpired() = true

    override fun isAccountNonLocked() = true

    override fun isCredentialsNonExpired() = true

    override fun isEnabled(): Boolean = enabled
}