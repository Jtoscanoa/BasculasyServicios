package com.puropoo.proyectobys;

public class Request {
    private int id;
    private String serviceType;
    private String serviceDate;
    private String serviceTime;
    private String serviceAddress;
    private String clientCedula;

    // Constructor
    public Request(int id, String serviceType, String serviceDate, String serviceTime, String serviceAddress) {
        this.id = id;
        this.serviceType = serviceType;
        this.serviceDate = serviceDate;
        this.serviceTime = serviceTime;
        this.serviceAddress = serviceAddress;
    }

    // Constructor with clientCedula
    public Request(int id, String serviceType, String serviceDate, String serviceTime, String serviceAddress, String clientCedula) {
        this.id = id;
        this.serviceType = serviceType;
        this.serviceDate = serviceDate;
        this.serviceTime = serviceTime;
        this.serviceAddress = serviceAddress;
        this.clientCedula = clientCedula;
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

    public String getClientCedula() {
        return clientCedula;  // Return the actual clientCedula field
    }

    public String getServiceAddress() {
        return serviceAddress;  // Añadido para la dirección
    }

}
