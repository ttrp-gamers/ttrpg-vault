package com.ttrp.manager.helper.authentication;


import com.ttrp.manager.entity.type.AccountStatus;
import lombok.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public record UserIdentity(
        Long id,
        String email,
        AccountStatus status,
        Collection<? extends GrantedAuthority> authorities
) implements UserDetails {

    private boolean isActiveAccount() {
        return this.status == AccountStatus.ACTIVE;
    }





    @Override
    @NonNull
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    @NonNull
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return isActiveAccount();
    }

    @Override
    public boolean isAccountNonLocked() {
        return isActiveAccount();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return isActiveAccount();
    }

    @Override
    public boolean isEnabled() {
        return isActiveAccount();
    }


}
