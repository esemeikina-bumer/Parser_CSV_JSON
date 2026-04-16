package ru.netology;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // ========== ЧАСТЬ 1: CSV → JSON ==========
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";
        List<Employee> listFromCSV = parseCSV(columnMapping, fileName);
        String jsonFromCSV = listToJson(listFromCSV);
        writeString(jsonFromCSV, "data.json");
        System.out.println("CSV → JSON: файл data.json создан");

        // ========== ЧАСТЬ 2: XML → JSON ==========
        List<Employee> listFromXML = parseXML("data.xml");
        String jsonFromXML = listToJson(listFromXML);
        writeString(jsonFromXML, "data2.json");
        System.out.println("XML → JSON: файл data2.json создан");
    }

    // Парсинг CSV
    public static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        try (Reader reader = new FileReader(fileName, StandardCharsets.UTF_8)) {
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);

            CsvToBean<Employee> csvToBean = new CsvToBeanBuilder<Employee>(reader)
                    .withMappingStrategy(strategy)
                    .withSkipLines(0)
                    .withSeparator(',')
                    .build();

            return csvToBean.parse();

        } catch (IOException e) {
            System.err.println("Ошибка при чтении CSV файла: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // Парсинг XML
    public static List<Employee> parseXML(String fileName) {
        List<Employee> employees = new ArrayList<>();

        try {
            // Создаём фабрику и билдер для парсинга XML
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            // Парсим XML файл в Document
            Document document = builder.parse(new File(fileName));

            // Получаем корневой элемент <staff>
            Element root = document.getDocumentElement();

            // Получаем список всех узлов <employee>
            NodeList nodeList = root.getChildNodes();

            // Проходим по всем узлам
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);

                // Проверяем, что узел является элементом и имеет имя "employee"
                if (node.getNodeType() == Node.ELEMENT_NODE && node.getNodeName().equals("employee")) {
                    Element employeeElement = (Element) node;

                    // Извлекаем значения из каждого дочернего элемента
                    long id = Long.parseLong(getTagValue("id", employeeElement));
                    String firstName = getTagValue("firstName", employeeElement);
                    String lastName = getTagValue("lastName", employeeElement);
                    String country = getTagValue("country", employeeElement);
                    int age = Integer.parseInt(getTagValue("age", employeeElement));

                    // Создаём объект Employee и добавляем в список
                    Employee employee = new Employee(id, firstName, lastName, country, age);
                    employees.add(employee);
                }
            }

        } catch (Exception e) {
            System.err.println("Ошибка при парсинге XML файла: " + e.getMessage());
            e.printStackTrace();
        }

        return employees;
    }

    // Вспомогательный метод для получения значения тега XML
    private static String getTagValue(String tagName, Element element) {
        NodeList nodeList = element.getElementsByTagName(tagName).item(0).getChildNodes();
        Node node = nodeList.item(0);
        return node != null ? node.getNodeValue() : "";
    }

    // Преобразование списка в JSON (из предыдущей задачи)
    public static String listToJson(List<Employee> list) {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();

        return gson.toJson(list);
    }

    // Запись JSON в файл (из предыдущей задачи)
    public static void writeString(String json, String fileName) {
        try (FileWriter writer = new FileWriter(fileName, StandardCharsets.UTF_8)) {
            writer.write(json);
            System.out.println("Файл " + fileName + " успешно записан");
        } catch (IOException e) {
            System.err.println("Ошибка при записи в файл " + fileName + ": " + e.getMessage());
        }
    }
}