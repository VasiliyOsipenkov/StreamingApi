package ru.avalon.javapp.devj130.streamingapi.Models;

import java.time.LocalDate;

public class Routes {
    private LocalDate flightDate;
    private String registrationNumber;
    private String departureAirport;
    private String arrivalAirport;

    public Routes(LocalDate flightDate, String registrationNumber, String departureAirport, String arrivalAirport) {
        this.flightDate = flightDate;
        this.registrationNumber = registrationNumber;
        this.departureAirport = departureAirport;
        this.arrivalAirport = arrivalAirport;
    }

    public LocalDate getFlightDate() {
        return flightDate;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public String getDepartureAirport() {
        return departureAirport;
    }

    public String getArrivalAirport() {
        return arrivalAirport;
    }

    @Override
    public String toString() {
        return "Routes{" +
                "flightDate=" + flightDate +
                ", registrationNumber='" + registrationNumber + '\'' +
                ", departureAirport='" + departureAirport + '\'' +
                ", arrivalAirport='" + arrivalAirport + '\'' +
                '}';
    }
}
