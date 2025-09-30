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
import javax.persistence.Table;

/**
 *
 * @author mohan
 */
@Entity
@Table(name ="branch_types")
public class Branch_Types {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
     @Column(name = "branch_type", length = 45, nullable = true)
    private String branch_type;

    public Branch_Types() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBranch_type() {
        return branch_type;
    }

    public void setBranch_type(String branch_type) {
        this.branch_type = branch_type;
    }
     
     
}
