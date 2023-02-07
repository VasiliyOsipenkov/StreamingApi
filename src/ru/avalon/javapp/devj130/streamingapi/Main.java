package ru.avalon.javapp.devj130.streamingapi;

import ru.avalon.javapp.devj130.streamingapi.Models.Planes;
import ru.avalon.javapp.devj130.streamingapi.Models.Routes;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {
    /*
4. Выведите регистрационные номера самолётов типа «B737», не бывавших в аэропорту
Мурманска («MMK»).
5. Выведите самую раннюю дату ввода самолёта в эксплуатацию.
6. Выведите все данные о самолёте, введённым в эксплуатацию последним.
7. Сохраните в файл «rep_same.txt» все данные о рейсах, у которых аэропорт отправления и
аэропорт назначения совпадают.
*/
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
            ArrayList<String> registeredFlights = new ArrayList<>();
            for (int i = 0; i < routes.length; i++) {
                registeredFlights.add(routes[i].getRegistrationNumber());
            }
            Planes[] outNoFlights = Arrays.stream(planes)
                    .filter(g ->!registeredFlights.contains(g.getRegistrationNumber()))
                    .toArray(Planes[] :: new);
            CsvSupport.writeCsv(new File("rep_gr.txt"), outNoFlights, CsvSupport::planesSupport);
            //3
            Arrays.stream(routes)
                    .filter(g -> g.getArrivalAirport().equals("LED") || g.getDepartureAirport().equals("LED"))
                    .map(g -> g.getRegistrationNumber())
                    .distinct()
                    .forEach(x -> System.out.println(x));
            System.out.println();
            //4. Выведите регистрационные номера самолётов типа «B737», не бывавших в аэропорту Мурманска («MMK»).
            Arrays.stream(planes)
                    .filter(g -> g.getType().equals("B737"))
                    .map(g -> g.getRegistrationNumber())
                    .filter(Arrays.stream(routes)
                            .map(h ->h.getRegistrationNumber().equals()))
                    .forEach(x -> System.out.println(x));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}