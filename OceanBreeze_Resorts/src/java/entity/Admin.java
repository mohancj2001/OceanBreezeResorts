/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 * @author mohan
 */
@Entity
@Table(name ="admin")
public class Admin {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
     @Column(name = "first_name", length = 45, nullable = true)
    private String first_name;
    
    @Column(name = "last_name", length = 45, nullable = true)
    private String last_name;
    
    @Column(name = "password", length = 45, nullable = true)
    private String password;
    
    @Column(name = "email", length = 45, nullable = true)
    private String email;
    
    @Column(name = "verification", length = 10, nullable = true)
    private String verification;
    
    @ManyToOne
    @JoinColumn(name = "status_id")
    private Status status;
    
    @ManyToOne
    @JoinColumn(name = "branch_types_id")
    private Branch_Types branch_Types;

    public Admin() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Branch_Types getBranch_Types() {
        return branch_Types;
    }

    public void setBranch_Types(Branch_Types branch_Types) {
        this.branch_Types = branch_Types;
    }

    public String getVerification() {
        return verification;
    }

    public void setVerification(String verification) {
        this.verification = verification;
    }
    
}
