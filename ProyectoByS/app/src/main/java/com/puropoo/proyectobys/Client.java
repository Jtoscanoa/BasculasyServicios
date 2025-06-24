package com.puropoo.proyectobys;

public class Client{
    public final int id;
    public String name, cedula, phone, address, serviceType;

    public Client(int id, String name, String cedula, String phone, String address, String serviceType) {
        this.id = id;
        this.name = name;
        this.cedula = cedula;
        this.phone = phone;
        this.address = address;
        this.serviceType = serviceType;
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getCedula() {
        return cedula;
    }

    public String getAddress() {
        return address;  // Getter para la dirección
    }
}
