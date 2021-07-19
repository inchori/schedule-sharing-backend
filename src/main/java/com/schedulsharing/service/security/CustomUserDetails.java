package com.schedulsharing.service.security;

import com.schedulsharing.domain.member.Member;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
public class CustomUserDetails implements UserDetails {
    private Long id;
    private String email;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;
    private Member member;

    public CustomUserDetails(Long id, String email, String password, Collection<? extends GrantedAuthority> authorities, Member member) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
        this.member = member;
    }

    public static CustomUserDetails create(Member member) {
        List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));

        return new CustomUserDetails(
                member.getId(),
                member.getEmail(),
                member.getPassword(),
                authorities,
                member
        );
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
