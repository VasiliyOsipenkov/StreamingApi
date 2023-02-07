package ru.avalon.javapp.devj130.streamingapi;

import ru.avalon.javapp.devj130.streamingapi.Models.Planes;
import ru.avalon.javapp.devj130.streamingapi.Models.Routes;

import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.function.IntFunction;

public class CsvSupport {
    private static final char SEP = ',';
    
    public static String[][] readCsv(File file) throws IOException {
        List<String[]> res = new ArrayList<>();
        try(BufferedReader br = new BufferedReader(new FileReader(file))) {
            String s;
            int colCnt = -1;
            while( ( s = br.readLine() ) != null ) {
                String[] row = parseLine(s);
                if(colCnt == -1)
                    colCnt = row.length;
                else if(colCnt != row.length)
                    throw new FileFormatException("Rows contain different number of values.");
                res.add(row);
            }
        }
        return res.toArray(new String[0][]);
    }

    public static <T> T[] readCsv(File file, Function<String[], T> convertor,
                                  IntFunction<T[]> arrayBuilder) throws IOException {
        String[][] data = readCsv(file);
        T[] res = arrayBuilder.apply(data.length);
        for (int i = 0; i < res.length; i++) {
            res[i] = convertor.apply(data[i]);
        }
        return res;
    }

    private static String[] parseLine(String s) {
        List<String> res = new ArrayList<>();
        int p = 0;
        while(p < s.length()) {
            int st = p;
            String v;
            if(s.charAt(p) == '"') {
                p++;
                p = s.indexOf('"', p);
                while(p < (s.length() - 1) && s.charAt(p + 1) == '"') {
                    p += 2;
                    p = s.indexOf('"', p);
                }
                v = s.substring(st + 1, p).replace("\"\"", "\"");
                p += 2;
            } else {
                p = s.indexOf(SEP, p);
                if(p == -1)
                    p = s.length();
                v = s.substring(st, p);
                if(v.length() == 0)
                    v = null;
                p++;
            }
            res.add(v);
        }
        return res.toArray(new String[0]);
    }

    public static void writeCsv(File target, String[] colHdrs, Object[][] data) throws IOException {
        try(FileWriter out = new FileWriter(target)) {
            for (int i = 0; i < colHdrs.length - 1; i++) {
                out.write(cellHandler(colHdrs[i]));
                out.write(",");
            }
            out.write(cellHandler(colHdrs[colHdrs.length - 1]));
            out.write("\n");

            for (int i = 0; i < data.length; i++) {
                Object[] row = data[i];
                for (int j = 0; j < row.length - 1; j++) {
                    out.write(cellHandler((String) row[j]));
                    out.write(",");
                }
                out.write(cellHandler((String) row[row.length - 1]));
                if (i < data.length - 1)
                    out.write("\n");
            }
        }
    }

    public static void writeCsv(File target, String[][] rows) throws IOException {
        Map<Integer, Integer> cellLengthCount = new HashMap<>();
        String[] firstRow = rows[0];
        for (int i = 0; i < firstRow.length; i++) {
            cellLengthCount.put(i, firstRow[i].length());
        }
        for (int i = 0; i < rows.length; i++) {
            String[] row = rows[i];
            for (int j = 0; j < row.length; j++) {
                if (row[j].length() != cellLengthCount.get(j))
                    throw new FileFormatException("Line " + row[j] + ": length mismatch");
            }
        }
        try (FileWriter out = new FileWriter(target)) {
            for (int i = 0; i < rows.length; i++) {
                String[] row = rows[i];
                for (int j = 0; j < row.length - 1; j++) {
                    out.write(cellHandler(row[j]));
                    out.write(",");
                }
                out.write(cellHandler(row[row.length - 1]));
                if (i < rows.length - 1)
                    out.write("\n");
            }
        }
    }

    public static void writeCsv(File target, List<String[]> rows) throws IOException {
        writeCsv(target, (String[][]) rows.toArray());
    }

    public static <T> void writeCsv(File target, T[] items, Function<T[], String[][]> q) throws IOException {
        writeCsv(target, q.apply(items));
    }

    public static <T> void writeCsv(File target, List<T> items, Function<T[], String[][]> q) throws IOException {
        writeCsv(target, q.apply((T[]) items.toArray()));
    }

    public static String[][] planesSupport(Planes[] items) {
        String[][] output = new String[items.length][3];
        for (int i = 0; i < items.length; i++) {
            output[i][0] = items[i].getRegistrationNumber();
            output[i][1] = items[i].getType();
            output[i][2] = items[i].getCommissioningDate().toString();
        }
        return output;
    }

    public static String[][] routesSupport(Routes[] items) {
        String[][] output = new String[items.length][3];
        for (int i = 0; i < items.length; i++) {
            output[i][0] = items[i].getFlightDate().toString();
            output[i][1] = items[i].getRegistrationNumber();
            output[i][2] = items[i].getDepartureAirport();
            output[i][4] = items[i].getArrivalAirport();
        }
        return output;
    }

    private static String cellHandler(String s) {
        if (s == null)
            return "";
        if (s.contains("\"")) {
            s = s.replace("\"", "\"\"");
            s = "\"" + s + "\"";
        }
        if (s.matches("^([0-9]*\\,[0-9]+)$"))
            s = "\"" + s + "\"";
        return s;
    }

    public static Class[] columnClassDetect(String[][] rowData) {
        Class[] columnClass = new Class[rowData[0].length];
        for (int i = 0; i < columnClass.length; i++)
            columnClass[i] = String.class;

        for (int i = 0; i < rowData.length; i++) {
            String[] row = rowData[i];
            for (int j = 0; j < row.length; j++) {
                if (row[j] == null)
                    j++;
                if (row[j].matches("^[0-9]+$") && !columnClass[j].equals(BigDecimal.class) && !columnClass[j].equals(String.class) && i > 0 ||
                        row[j].matches("^[0-9]+$") && i == 0) {
                    columnClass[j] = Integer.class;
                }
                if (row[j].matches("^([0-9]*\\,[0-9]+)$") && i == 0 ||
                        row[j].matches("^([0-9]*\\,[0-9]+)$") && i > 0 && !columnClass[j].equals(String.class)) {
                    columnClass[j] = BigDecimal.class;
                }
                if (row[j].matches("^(false|true)$") && i == 0 ||
                        row[j].matches("^(false|true)$") && i > 0 && !columnClass[j].equals(String.class)) {
                    columnClass[j] = Boolean.class;
                }
                if (row[j].matches("^\\d{4}-\\d{2}-\\d{2}$") && i == 0 ||
                        row[j].matches("^\\d{4}-\\d{2}-\\d{2}$") && i > 0 && !columnClass[j].equals(String.class)) {
                    columnClass[j] = LocalDate.class;
                }
            }
        }
        return columnClass;
    }

}
