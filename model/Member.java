/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

/**
 *
 * @author benja
 */
public class Member {
    
    private int memberId;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String address;
    
    // Default constructor
    public Member() {
    }
    
    // Optional: Parameterized constructor
    public Member(int memberId, String firstName, String lastName, String email, String phone, String address) {
        this.memberId = memberId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.address = address;
    }


    
    // Getters and Setters
    public int getMemberId() {
        return memberId;
    }
    
    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    // Overriding toString() for easy debugging/logging
    @Override
    public String toString() {
        return "Member [memberId=" + memberId + ", firstName=" + firstName 
                + ", lastName=" + lastName + ", email=" + email 
                + ", phone=" + phone + ", address=" + address + "]";
    }
}
