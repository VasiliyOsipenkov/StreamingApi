package ru.avalon.javapp.devj130.streamingapi;

import ru.avalon.javapp.devj130.streamingapi.Models.Planes;
import ru.avalon.javapp.devj130.streamingapi.Models.Routes;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {
    /*Методы через параметр принимают имя файла, в который будут сохраняться данные. В случае
ошибки записи (ошибка ввода/вывода и т.п.) должно выбрасываться исключение. При сохранении данных в файл
нужно помнить, что разделитель ячеек и двойная
кавычка в значении ячейки
должны экранироваться — значение должно быть заключено в двойные кавычки, а содержащиеся в значении
двойные кавычки должны быть удвоены.
При помощи имеющихся методов чтения CSV-файла прочитайте файлы с тестовыми данными и
сохраните их в массивы.
Используя методы Streaming API, выведите данные, отвечающие на следующие запросы. Если
требуется, сохраните данные с результатом выполнения запроса в CSV-файл при помощи реализованного вами метода:
2. Сохраните в файл «rep_gr.txt» все данные о самолётах, о рейсах которых нет данных.
3. Выведите регистрационные номера самолётов, бывавших в аэропорту Санкт-Петербурга
(«LED»).
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
            String[][] outputPlanes = new String[importPlanes.length][3];
            for (int i = 0; i < outputPlanes.length; i++) {
                outputPlanes[i][0] = importPlanes[i].getRegistrationNumber();
                outputPlanes[i][1] = importPlanes[i].getType();
                outputPlanes[i][2] = importPlanes[i].getCommissioningDate().toString();
            }
            CsvSupport.writeCsv(new File("rep_fp.txt"), outputPlanes);
            //2. Сохраните в файл «rep_gr.txt» все данные о самолётах, о рейсах которых нет данных.
            String[] registeredFlights = new String[routes.length];
            for (int i = 0; i < routes.length; i++) {
                registeredFlights[i] = routes[i].getRegistrationNumber();
            }
            Planes[] outNoFlights = Arrays.stream(planes)
                    .filter(g ->g.getRegistrationNumber())
                    .toArray(Planes[] :: new);
            /* Sring[] outNoFlights = Arrays.stream(routes)
                    .collect(Collectors.toList().toString());*/
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}