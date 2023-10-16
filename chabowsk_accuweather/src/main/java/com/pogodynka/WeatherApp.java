package com.pogodynka;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;

public class WeatherApp {

    // definiuje elementy interfejsu
    private JFrame frame;
    private JButton btnSearch, btnCurrentConditions, btn1DayForecast, btn5DaysForecast, btn4HoursForecast,
            btnIndices;
    private JComboBox<String> cityList;
    private JLabel searchLabel;
    // definiuje obiekt klasy WeatherApi aby moc korzystac z funkcjonalnosci
    private WeatherAPI api;
    // tworze hashMape to przechowywania location key dla podanego miasta
    private Map<String, String> cityKeyMap = new HashMap<>();

    public WeatherApp() {
        api = new WeatherAPI("GriiUGF3dEeg4vbdFW0Kyhu1hAygUQBA"); // podaje swoj unikalny klucz API

        // konfiguruje interfejs uzytkownika
        frame = new JFrame("AccuWeather App");
        frame.setSize(450, 550);
        frame.setLayout(new GridLayout(7, 2));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        searchLabel = new JLabel("Wyszukaj miasto:");
        cityList = new JComboBox<String>();
        cityList.setEditable(true);
        btnSearch = new JButton("Szukaj");
        btnSearch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String city = (String) cityList.getSelectedItem();
                cityKeyMap = api.searchCitiesWithRegion(city);
                cityList.removeAllItems();
                for (String cityName : cityKeyMap.keySet()) {
                    cityList.addItem(cityName);
                }
            }
        });

        btnCurrentConditions = createButton("Obecne warunki pogodowe", "currentconditions/v1", 0);
        btn4HoursForecast = createButton("Prognoza za 4 godziny", "forecasts/v1/hourly/12hour", 3);
        btn1DayForecast = createButton("Prognoza na najblizsze 24 godziny", "forecasts/v1/daily/1day", 0);
        btn5DaysForecast = createButton("Prognoza za 5 dni", "forecasts/v1/daily/5day", 4);
        btnIndices = createButton("Czy pogoda sprzyja spacerowi z psem?", "indices/v1/daily/1day", 41);

        JPanel searchPanel = new JPanel();
        searchPanel.add(searchLabel);
        searchPanel.add(cityList);
        searchPanel.add(btnSearch);

        frame.add(searchPanel);
        frame.add(btnCurrentConditions);
        frame.add(btnIndices);
        frame.add(btn4HoursForecast);
        frame.add(btn1DayForecast);
        frame.add(btn5DaysForecast);

        frame.setVisible(true);
    }

    // metoda tworzaca przycisk oraz przypisuje funkcjonalnosc do odpowiedniego
    // przycisku ze wzgledu na jego unikalna wartosc endpoint
    private JButton createButton(String title, final String endpoint, final int index) {
        JButton button = new JButton(title);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedCity = (String) cityList.getSelectedItem();
                if (selectedCity != null) {
                    String locationKey = cityKeyMap.get(selectedCity);
                    String info = api.fetchWeatherData(endpoint, locationKey);
                    String result;
                    Map<String, String> map = api.extractWeatherDetails(info, index, endpoint);

                    if (endpoint == "currentconditions/v1") {
                        result = "Dla miasta " + selectedCity + " obecna temperatura wynosi: "
                                + map.get("Temperature") +
                                ", przy czym odczuwalna temperatura wynosi: " + map.get("RealFeelTemperature") + ".\n" +
                                "Warunki pogodowe to: " + map.get("WeatherText") + ".\n" +
                                "Ciśnienie atmosferyczne wynosi: " + map.get("Pressure") + ".\n";
                    } else if (endpoint == "forecasts/v1/hourly/12hour") {
                        result = "Dla miasta " + selectedCity + " temperatura za 4h będzie wynosić: "
                                + map.get("Temperature") +
                                ", \nprzy czym odczuwalna temperatura bedzie wynosić: " + map.get("RealFeelTemperature")
                                + ".\n" +
                                "Warunki pogodowe to: " + map.get("IconPhrase") + ".\n";
                    } else if (endpoint == "forecasts/v1/daily/1day") {
                        result = "Prognoza na najbliższe 24h dla miasta " + selectedCity + ": \n" + map.get("Headline")
                                + "\n" +
                                "Przy czym temperatura będzie w zakresie od " + map.get("MinimumTemperature") + " do "
                                + map.get("MaximumTemperature") + ".\n";
                    } else if (endpoint == "forecasts/v1/daily/5day") {
                        result = "Temperatura za 5 dni dla miasta " + selectedCity
                                + ": \nbędzie wahała się w przedziale od " +
                                map.get("MinimumTemperature") + " do " + map.get("MaximumTemperature") + ".\n" +
                                "Odczuwalna temperatura w zakresie od " + map.get("minRealFeel") + " do "
                                + map.get("maxRealFeel") + ". \n";
                    } else if (endpoint == "indices/v1/daily/1day") {
                        result = "Czy pogoda sprzyja spacerowi z psem?\n" + map.get("Text");
                    } else {
                        result = "Proszę wybrać jedna z opcji.";
                    }
                    // wynik prognozy wyswietlany w nowym oknie komunikatem
                    JOptionPane.showMessageDialog(frame, result);
                } else {
                    JOptionPane.showMessageDialog(frame, "Proszę wybrać miasto z listy.");
                }
            }
        });
        return button;
    }

    public static void main(String[] args) {
        new WeatherApp();
    }
}
