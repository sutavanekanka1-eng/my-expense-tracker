package org.example.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.example.entities.UserInfo;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class CustomUserDetails extends UserInfo implements UserDetails {
	private String username;
	private String password;
	
	public Collection<? extends GrantedAuthority> authorities;
	public CustomUserDetails(UserInfo userInfo) {
		this.username = userInfo.getUsername();
		this.password = userInfo.getPassword();
		this.authorities = new ArrayList<>();
	}
	@Override
	public String getPassword() {
		return password;
	}
	@Override
	public String getUsername() {
		return username;
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
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}
	
	
	

}
