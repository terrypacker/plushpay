package com.payyourself.userManagement.role;

// Generated Apr 24, 2010 1:54:05 PM by Hibernate Tools 3.2.5.Beta

/**
 * Role generated by hbm2java
 */
public class Role implements java.io.Serializable {

	private String username;
	private String role;

	public Role() {
	}

	public Role(String username, String role) {
		this.username = username;
		this.role = role;
	}

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getRole() {
		return this.role;
	}

	public void setRole(String role) {
		this.role = role;
	}

}
