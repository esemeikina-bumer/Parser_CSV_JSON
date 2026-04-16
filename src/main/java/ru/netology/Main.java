package ru.netology;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.ColumnPositionMappingStrategy;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // 1. Определяем маппинг колонок
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};

        // 2. Имя CSV файла
        String fileName = "data.csv";

        // 3. Парсим CSV в список сотрудников
        List<Employee> list = parseCSV(columnMapping, fileName);

        // 4. Преобразуем список в JSON строку
        String json = listToJson(list);

        // 5. Записываем JSON в файл
        writeString(json, "data.json");

        System.out.println("Готово! Файл data.json создан.");
    }

    public static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        try (Reader reader = new FileReader(fileName, StandardCharsets.UTF_8)) {
            // Создаём стратегию маппинга колонок
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);

            // Создаём парсер CSV
            CsvToBean<Employee> csvToBean = new CsvToBeanBuilder<Employee>(reader)
                    .withMappingStrategy(strategy)
                    .withSkipLines(0)     // не пропускаем строки
                    .withSeparator(',')   // разделитель запятая
                    .build();

            // Парсим и возвращаем список
            return csvToBean.parse();

        } catch (IOException e) {
            System.err.println("Ошибка при чтении файла: " + e.getMessage());
            return List.of(); // возвращаем пустой список в случае ошибки
        }
    }

    public static String listToJson(List<Employee> list) {
        // Создаём Gson с красивым форматированием
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()  // для красивого вывода с отступами
                .create();

        // Преобразуем список в JSON
        String json = gson.toJson(list);
        return json;
    }

    public static void writeString(String json, String fileName) {
        try (FileWriter writer = new FileWriter(fileName, StandardCharsets.UTF_8)) {
            writer.write(json);
            System.out.println("JSON успешно записан в файл: " + fileName);
        } catch (IOException e) {
            System.err.println("Ошибка при записи в файл: " + e.getMessage());
        }
    }
}


