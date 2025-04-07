package com.example.skaiciuotuvas;

import javafx.collections.ObservableList;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

public class GraphSection {
    private LineChart<Number, Number> paymentChart;
    private XYChart.Series<Number, Number> paymentSeries;
    private XYChart.Series<Number, Number> principalSeries;
    private XYChart.Series<Number, Number> interestSeries;

    public GraphSection() {
        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("Mėnuo");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Mokėjimas (€)");

        paymentChart = new LineChart<>(xAxis, yAxis);
        paymentChart.setTitle("Mokėjimai per laiką");
        paymentChart.setAnimated(true);
        paymentChart.setLegendVisible(true);

        paymentSeries = new XYChart.Series<>();
        paymentSeries.setName("Bendra įmoka");

        principalSeries = new XYChart.Series<>();
        principalSeries.setName("Pagrindinė suma");

        interestSeries = new XYChart.Series<>();
        interestSeries.setName("Palūkanos");

        paymentChart.getData().addAll(paymentSeries, principalSeries, interestSeries);
        paymentChart.setPrefHeight(300);
        paymentChart.setStyle("-fx-background-color: white; -fx-border-color: #ddd; -fx-border-radius: 5;");
    }

    public void updateChartFromData(ObservableList<PaymentEntry> paymentData, double initialAmount) {
        paymentSeries.getData().clear();
        principalSeries.getData().clear();
        interestSeries.getData().clear();

        for (PaymentEntry entry : paymentData) {
            int month = entry.getMonth();
            double payment = Double.parseDouble(entry.getPayment().replace(",", "."));
            double principal = Double.parseDouble(entry.getPrincipal().replace(",", "."));
            double interest = Double.parseDouble(entry.getInterest().replace(",", "."));

            paymentSeries.getData().add(new XYChart.Data<>(month, payment));
            principalSeries.getData().add(new XYChart.Data<>(month, principal));
            interestSeries.getData().add(new XYChart.Data<>(month, interest));
        }
    }

    public LineChart<Number, Number> getChart() {
        return paymentChart;
    }
}