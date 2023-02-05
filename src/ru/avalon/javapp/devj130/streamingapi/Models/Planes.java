package ru.avalon.javapp.devj130.streamingapi.Models;

import java.time.LocalDate;

public class Planes {
    private String registrationNumber;
    private String type;
    private LocalDate commissioningDate;

    public Planes(String registrationNumber, String type, LocalDate commissioningDate) {
        this.registrationNumber = registrationNumber;
        this.type = type;
        this.commissioningDate = commissioningDate;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public String getType() {
        return type;
    }

    public LocalDate getCommissioningDate() {
        return commissioningDate;
    }

    @Override
    public String toString() {
        return "Planes{" +
                "registrationNumber='" + registrationNumber + '\'' +
                ", type='" + type + '\'' +
                ", commissioningDate=" + commissioningDate +
                '}';
    }
}
