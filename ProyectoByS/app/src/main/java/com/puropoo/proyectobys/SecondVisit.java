package com.puropoo.proyectobys;

public class SecondVisit {
    private int id;
    private int serviceRequestId;
    private String serviceType;
    private String visitDate;
    private String visitTime;
    private String clientCedula;

    // Constructor
    public SecondVisit(int id, int serviceRequestId, String serviceType, String visitDate, String visitTime, String clientCedula) {
        this.id = id;
        this.serviceRequestId = serviceRequestId;
        this.serviceType = serviceType;
        this.visitDate = visitDate;
        this.visitTime = visitTime;
        this.clientCedula = clientCedula;
    }

    // Constructor without id for new records
    public SecondVisit(int serviceRequestId, String serviceType, String visitDate, String visitTime, String clientCedula) {
        this.serviceRequestId = serviceRequestId;
        this.serviceType = serviceType;
        this.visitDate = visitDate;
        this.visitTime = visitTime;
        this.clientCedula = clientCedula;
    }

    // Getters
    public int getId() {
        return id;
    }

    public int getServiceRequestId() {
        return serviceRequestId;
    }

    public String getServiceType() {
        return serviceType;
    }

    public String getVisitDate() {
        return visitDate;
    }

    public String getVisitTime() {
        return visitTime;
    }

    public String getClientCedula() {
        return clientCedula;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setServiceRequestId(int serviceRequestId) {
        this.serviceRequestId = serviceRequestId;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public void setVisitDate(String visitDate) {
        this.visitDate = visitDate;
    }

    public void setVisitTime(String visitTime) {
        this.visitTime = visitTime;
    }

    public void setClientCedula(String clientCedula) {
        this.clientCedula = clientCedula;
    }
}