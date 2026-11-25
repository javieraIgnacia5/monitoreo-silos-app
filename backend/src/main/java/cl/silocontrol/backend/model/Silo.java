
package cl.silocontrol.backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "silos")
public class Silo {

    @Id
    private String id;

    private String name;
    private String grain;
    private String farm;
    private int capacityTon;
    private double currentTon;
    private String updatedAt;

    private String locationAddress;
    private Double latitude;
    private Double longitude;

    public Silo() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGrain() {
        return grain;
    }

    public void setGrain(String grain) {
        this.grain = grain;
    }

    public String getFarm() {
        return farm;
    }

    public void setFarm(String farm) {
        this.farm = farm;
    }

    public int getCapacityTon() {
        return capacityTon;
    }

    public void setCapacityTon(int capacityTon) {
        this.capacityTon = capacityTon;
    }

    public double getCurrentTon() {
        return currentTon;
    }

    public void setCurrentTon(double currentTon) {
        this.currentTon = currentTon;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getLocationAddress() {
        return locationAddress;
    }

    public void setLocationAddress(String locationAddress) {
        this.locationAddress = locationAddress;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}
