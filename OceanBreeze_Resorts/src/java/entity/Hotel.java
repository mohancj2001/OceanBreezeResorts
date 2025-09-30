package entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 *
 * @author mohan
 */

@Entity
@Table(name ="hotel")
public class Hotel implements Serializable{
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @ManyToOne
    @JoinColumn(name = "status_id")
    private Status status;
    
    @ManyToOne
    @JoinColumn(name = "locations_id")
    private Locations locations;
    
    @ManyToOne
    @JoinColumn(name = "admin_id")
    private Admin admin;

    public Hotel(int id, Status status, Locations locations, Admin admin) {
        this.id = id;
        this.status = status;
        this.locations = locations;
        this.admin = admin;
    }

    
    public Hotel() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Locations getLocations() {
        return locations;
    }

    public void setLocations(Locations locations) {
        this.locations = locations;
    }

    public Admin getAdmin() {
        return admin;
    }

    public void setAdmin(Admin admin) {
        this.admin = admin;
    }
    
    

    
}
