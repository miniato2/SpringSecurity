package com.miniato.security.dto;

import com.miniato.security.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

public class CustomUserDetails implements UserDetails {

    private final User user;

    public CustomUserDetails(User user){
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collection = new ArrayList<>();
        collection.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return user.getRole();
            }
        });
        return collection;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        //계정 만료 여부
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        //계정 잠김 여부
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        //자격 증명 만료여부
        return true;
    }

    @Override
    public boolean isEnabled() {
        //계정 활성화 여부
        return true;
    }
}
