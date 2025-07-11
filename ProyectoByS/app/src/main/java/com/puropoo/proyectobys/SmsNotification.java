package com.puropoo.proyectobys;

public class SmsNotification {
    private int id;
    private int requestId;
    private String recipientType;
    private String phone;
    private String serviceDate;
    private String serviceTime;
    private String serviceType;
    private String message;
    private String scheduledSend;
    private String sentTime;

    public SmsNotification(int id, int requestId, String recipientType, String phone,
                           String serviceDate, String serviceTime, String serviceType,
                           String message, String scheduledSend, String sentTime) {
        this.id = id;
        this.requestId = requestId;
        this.recipientType = recipientType;
        this.phone = phone;
        this.serviceDate = serviceDate;
        this.serviceTime = serviceTime;
        this.serviceType = serviceType;
        this.message = message;
        this.scheduledSend = scheduledSend;
        this.sentTime = sentTime;
    }

    public SmsNotification(int requestId, String recipientType, String phone,
                           String serviceDate, String serviceTime, String serviceType,
                           String message, String scheduledSend) {
        this.requestId = requestId;
        this.recipientType = recipientType;
        this.phone = phone;
        this.serviceDate = serviceDate;
        this.serviceTime = serviceTime;
        this.serviceType = serviceType;
        this.message = message;
        this.scheduledSend = scheduledSend;
    }

    public int getId() { return id; }
    public int getRequestId() { return requestId; }
    public String getRecipientType() { return recipientType; }
    public String getPhone() { return phone; }
    public String getServiceDate() { return serviceDate; }
    public String getServiceTime() { return serviceTime; }
    public String getServiceType() { return serviceType; }
    public String getMessage() { return message; }
    public String getScheduledSend() { return scheduledSend; }
    public String getSentTime() { return sentTime; }

    public void setMessage(String message) { this.message = message; }
    public void setSentTime(String sentTime) { this.sentTime = sentTime; }
}
