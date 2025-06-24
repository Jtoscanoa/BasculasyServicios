package com.puropoo.proyectobys;

public class Request {
    private int id;
    private String serviceType;
    private String serviceDate;
    private String serviceTime;

    // Constructor
    public Request(int id, String serviceType, String serviceDate, String serviceTime) {
        this.id = id;
        this.serviceType = serviceType;
        this.serviceDate = serviceDate;
        this.serviceTime = serviceTime;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getServiceType() {
        return serviceType;
    }

    public String getServiceDate() {
        return serviceDate;
    }

    public String getServiceTime() {
        return serviceTime;
    }
}
