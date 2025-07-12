package com.puropoo.proyectobys;

public class TeamMember {
    private int id;
    private int requestId;
    private String name;
    private String role;
    private String phone;
    private int age;
    private double payment;

    public TeamMember(int id, int requestId, String name, String role, String phone, int age, double payment) {
        this.id = id;
        this.requestId = requestId;
        this.name = name;
        this.role = role;
        this.phone = phone;
        this.age = age;
        this.payment = payment;
    }

    public TeamMember(int requestId, String name, String role, String phone, int age, double payment) {
        this(0, requestId, name, role, phone, age, payment);
    }

    public int getId() { return id; }
    public int getRequestId() { return requestId; }
    public String getName() { return name; }
    public String getRole() { return role; }
    public String getPhone() { return phone; }
    public int getAge() { return age; }
    public double getPayment() { return payment; }
}
