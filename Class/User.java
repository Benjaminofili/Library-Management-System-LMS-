/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Class;

import java.sql.Blob;

/**
 *
 * @author benja
 */
public class User {

    private  int id;
    private  String username;
    private String email;
     private String firstName;
    private String lastName;
    private String password;
    private  Blob profileImage;
    private boolean approved;

    
    public User(int id, String username, String email, String password, Blob profileImage) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.profileImage = profileImage;
    }

    public User(int id, String username, Blob profileImage) {
        this.id = id;
        this.username = username;
        this.profileImage = profileImage;
    }

    public User(int id, String email, String firstName, String lastName, boolean approved) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.approved = approved;
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

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }


   

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public Blob getProfileImage() {
        return profileImage;
    }


    
    
 
}


