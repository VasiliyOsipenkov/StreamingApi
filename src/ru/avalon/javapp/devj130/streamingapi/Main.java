package ru.avalon.javapp.devj130.streamingapi;

import ru.avalon.javapp.devj130.streamingapi.Models.Planes;
import ru.avalon.javapp.devj130.streamingapi.Models.Routes;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) {
        try {
            Planes[] planes = CsvSupport.readCsv(new File("planes-planes.csv"),
                    a -> new Planes(a[0], a[1], LocalDate.parse(a[2])),
                    n -> new Planes[n]);
            Routes[] routes = CsvSupport.readCsv(new File("planes-routes.csv"),
                    a -> new Routes(LocalDate.parse(a[0]), a[1], a[2], a[3]),
                    n -> new Routes[n]);

            //1
            Planes[] importPlanes = Arrays.stream(planes)
                    .filter(g -> !g.getType().equals("SU95"))
                    .toArray(Planes[] :: new);
            CsvSupport.writeCsv(new File("rep_fp.txt"), importPlanes, CsvSupport::planesSupport);
            //2
            List<String> registeredFlights = Arrays.stream(routes)
                    .map(g -> g.getRegistrationNumber())
                    .toList();
            List<Planes> outNoFlights = Arrays.stream(planes)
                    .filter(g -> !registeredFlights.contains(g.getRegistrationNumber()))
                    .toList();
            CsvSupport.writeCsv(new File("rep_gr.txt"), outNoFlights, CsvSupport::planesSupport);
            //3
            Arrays.stream(routes)
                    .filter(g -> g.getArrivalAirport().equals("LED") || g.getDepartureAirport().equals("LED"))
                    .map(g -> g.getRegistrationNumber())
                    .distinct()
                    .forEach(x -> System.out.println(x));
            System.out.println();
            //4
            Planes[] b737Type = Arrays.stream(planes)
                    .filter(g->g.getType().equals("B737"))
                    .toArray(Planes[]::new);
            List<String> noVisitMMK = Arrays.stream(routes)
                    .filter(g -> !(g.getArrivalAirport().equals("MMK") || g.getDepartureAirport().equals("MMK")))
                    .map(h -> h.getRegistrationNumber())
                    .toList();
            Arrays.stream(b737Type)
                    .filter(g -> noVisitMMK.contains(g.getRegistrationNumber()))
                    .map(j -> j.getRegistrationNumber())
                    .forEach(System.out::println);
            outNoFlights.stream()//не летавший самолет тоже не посещал MMK
                    .filter(g -> g.getType().equals("B737"))
                    .map(g -> g.getRegistrationNumber())
                    .forEach(System.out::println);
            System.out.println();
            //5
            LocalDate earlDateCommission = Arrays.stream(planes)
                    .map(g -> g.getCommissioningDate())
                    .min((d1, d2) ->d1.compareTo(d2) )
                    .get();
            System.out.println(earlDateCommission);
            System.out.println();
            //6
            Planes latestPlane = Arrays.stream(planes)
                    .max((d1, d2) -> d1.getRegistrationNumber().compareTo(d2.getRegistrationNumber()))
                    .get();
            System.out.println(latestPlane);
            System.out.println();
            //7
            Routes[] overlappingRoutes = Arrays.stream(routes)
                    .filter(g -> g.getArrivalAirport().equals(g.getDepartureAirport()))
                    .toArray(Routes[]::new);
            CsvSupport.writeCsv(new File("rep_same.txt"), overlappingRoutes, CsvSupport::routesSupport);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}