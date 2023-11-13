package com.example.milkchat2.user

import com.example.milkchat2.user.model.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.oauth2.core.oidc.OidcIdToken
import org.springframework.security.oauth2.core.oidc.OidcUserInfo
import org.springframework.security.oauth2.core.oidc.user.OidcUser

class CustomOidcUser(
    val user: User,
    val originalOidcUser: OidcUser,
): OidcUser by originalOidcUser {
    override fun getName(): String {
        return user.nickname!!
    }

    override fun getAttributes(): MutableMap<String, Any> {
        return attributes
    }

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return authorities
    }

    override fun getClaims(): MutableMap<String, Any> {
        TODO("Not yet implemented")
    }

    override fun getUserInfo(): OidcUserInfo {
        return userInfo
    }

    override fun getIdToken(): OidcIdToken {
        return idToken
    }
}
