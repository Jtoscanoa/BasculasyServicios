package com.puropoo.proyectobys;

public class RemoteSupport {
    private int id;
    private int requestId;
    private String supportDate;
    private String supportTime;
    private String medium;
    private String link;
    private String clientCedula;

    // Constructor
    public RemoteSupport(int id, int requestId, String supportDate, String supportTime, 
                        String medium, String link, String clientCedula) {
        this.id = id;
        this.requestId = requestId;
        this.supportDate = supportDate;
        this.supportTime = supportTime;
        this.medium = medium;
        this.link = link;
        this.clientCedula = clientCedula;
    }

    // Constructor sin ID (para inserciones)
    public RemoteSupport(int requestId, String supportDate, String supportTime, 
                        String medium, String link, String clientCedula) {
        this.requestId = requestId;
        this.supportDate = supportDate;
        this.supportTime = supportTime;
        this.medium = medium;
        this.link = link;
        this.clientCedula = clientCedula;
    }

    // Getters
    public int getId() {
        return id;
    }

    public int getRequestId() {
        return requestId;
    }

    public String getSupportDate() {
        return supportDate;
    }

    public String getSupportTime() {
        return supportTime;
    }

    public String getMedium() {
        return medium;
    }

    public String getLink() {
        return link;
    }

    public String getClientCedula() {
        return clientCedula;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public void setSupportDate(String supportDate) {
        this.supportDate = supportDate;
    }

    public void setSupportTime(String supportTime) {
        this.supportTime = supportTime;
    }

    public void setMedium(String medium) {
        this.medium = medium;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setClientCedula(String clientCedula) {
        this.clientCedula = clientCedula;
    }
}