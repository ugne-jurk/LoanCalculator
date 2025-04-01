package com.example.skaiciuotuvas;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.text.DecimalFormat;
import java.time.LocalDate;

public class Loancalculator extends Application {

    private TableView<PaymentEntry> paymentTable = new TableView<>();
    private ObservableList<PaymentEntry> paymentData = FXCollections.observableArrayList();
    private FilteredList<PaymentEntry> filteredData;
    private GraphSection graphSection;
    private LocalDate loanStartDate = LocalDate.now();

    @Override
    public void start(Stage primaryStage) {
        // Main layout with background color
        BorderPane mainLayout = new BorderPane();
        mainLayout.setPadding(new Insets(20));
        mainLayout.setStyle("-fx-background-color: #f5f5f5;");

        graphSection = new GraphSection();

        // Create sections
        VBox inputSection = createInputSection();
        VBox tableSection = createTableSection();

        // Create a container for the table and graph
        VBox rightSection = new VBox(15);
        rightSection.getChildren().addAll(tableSection, graphSection.getChart());
        VBox.setVgrow(tableSection, Priority.ALWAYS);

        // Layout arrangement
        mainLayout.setLeft(inputSection);
        mainLayout.setCenter(rightSection);

        // Create a scene with inline styles
        Scene scene = new Scene(mainLayout, 1200, 800);

        primaryStage.setTitle("Paskolos Skaičiuoklė");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private VBox createInputSection() {
        // Input section with card-like appearance
        VBox inputContent = new VBox(10);
        inputContent.setPadding(new Insets(10));
        inputContent.setStyle("-fx-background-color: white; -fx-border-color: #ddd; -fx-border-radius: 5; -fx-background-radius: 5;");
        inputContent.setMinWidth(350);

        // Section title
        Label titleLabel = new Label("Paskolos informacija");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
        titleLabel.setPadding(new Insets(0, 0, 10, 0));

        // Loan amount field with validation and formatting
        Label loanAmountLabel = new Label("Pageidaujama paskolos suma (€)");
        TextField loanAmountField = new TextField();
        loanAmountField.setPromptText("Pvz.: 10000");
        loanAmountField.setStyle("-fx-padding: 8px; -fx-border-color: #ddd; -fx-border-radius: 3px;");

        // Loan term (years and months) in a horizontal layout
        Label loanTermLabel = new Label("Paskolos terminas");
        HBox termBox = new HBox(10);

        VBox yearsBox = new VBox(5);
        Label yearsLabel = new Label("Metai");
        TextField loanTermFieldYears = new TextField();
        loanTermFieldYears.setPromptText("0");
        loanTermFieldYears.setPrefWidth(80);
        loanTermFieldYears.setStyle("-fx-padding: 8px; -fx-border-color: #ddd; -fx-border-radius: 3px;");
        yearsBox.getChildren().addAll(yearsLabel, loanTermFieldYears);

        VBox monthsBox = new VBox(5);
        Label monthsLabel = new Label("Mėnesiai");
        TextField loanTermFieldMonths = new TextField();
        loanTermFieldMonths.setPromptText("0");
        loanTermFieldMonths.setPrefWidth(80);
        loanTermFieldMonths.setStyle("-fx-padding: 8px; -fx-border-color: #ddd; -fx-border-radius: 3px;");
        monthsBox.getChildren().addAll(monthsLabel, loanTermFieldMonths);

        termBox.getChildren().addAll(yearsBox, monthsBox);

        // Interest rate field
        Label annualInterestLabel = new Label("Metinė palūkanų norma (%)");
        TextField annualInterestField = new TextField();
        annualInterestField.setPromptText("Pvz.: 2.5");
        annualInterestField.setStyle("-fx-padding: 8px; -fx-border-color: #ddd; -fx-border-radius: 3px;");

        // Payment type selection with better styling
        Label paymentTypeLabel = new Label("Grąžinimo grafikas");

        ToggleGroup paymentTypeGroup = new ToggleGroup();
        RadioButton annuity = new RadioButton("Anuitetas (vienoda įmoka)");
        RadioButton linear = new RadioButton("Linijinis (mažėjanti įmoka)");
        annuity.setToggleGroup(paymentTypeGroup);
        annuity.setSelected(true);
        linear.setToggleGroup(paymentTypeGroup);

        VBox paymentTypeBox = new VBox(10);
        paymentTypeBox.getChildren().addAll(annuity, linear);

        // Deferment section
        VBox defermentSection = createDefermentSection();

        // Create a styled button with hover effect
        Button calculateButton = new Button("Skaičiuoti");
        calculateButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10px 15px; -fx-cursor: hand; -fx-border-radius: 4px;");
        calculateButton.setPrefWidth(150);
        calculateButton.setOnMouseEntered(e -> calculateButton.setStyle("-fx-background-color: #0b7dda; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10px 15px; -fx-cursor: hand; -fx-border-radius: 4px;"));
        calculateButton.setOnMouseExited(e -> calculateButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10px 15px; -fx-cursor: hand; -fx-border-radius: 4px;"));

        // Monthly payment result with larger, emphasized text
        Label resultLabel = new Label("Mėnesinė įmoka:");
        TextField resultField = new TextField();
        resultField.setEditable(false);
        resultField.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-background-color: #e8f4f8; -fx-text-fill: #0b7dda; -fx-padding: 8px;");

        // Add everything to the input panel
        Separator separator = new Separator();
        separator.setPadding(new Insets(10, 0, 10, 0));

        inputContent.getChildren().addAll(
                titleLabel,
                loanAmountLabel, loanAmountField,
                loanTermLabel, termBox,
                annualInterestLabel, annualInterestField,
                paymentTypeLabel, paymentTypeBox,
                defermentSection,
                calculateButton,
                separator,
                resultLabel, resultField,
                createDateFilterSection()
        );

        // Setup calculation logic
        calculateButton.setOnAction(e -> {
            try {
                // Input validation
                if (loanAmountField.getText().isEmpty() ||
                        annualInterestField.getText().isEmpty() ||
                        (loanTermFieldYears.getText().isEmpty() && loanTermFieldMonths.getText().isEmpty())) {
                    showAlert("Klaida", "Trūksta duomenų", "Prašome užpildyti visus reikalingus laukus.");
                    return;
                }

                double amount = Double.parseDouble(loanAmountField.getText().replace(",", "."));
                double annualRate = Double.parseDouble(annualInterestField.getText().replace(",", "."));
                double monthlyRate = annualRate / 100 / 12;
                int years = loanTermFieldYears.getText().isEmpty() ? 0 : Integer.parseInt(loanTermFieldYears.getText());
                int months = loanTermFieldMonths.getText().isEmpty() ? 0 : Integer.parseInt(loanTermFieldMonths.getText());
                int totalMonths = (years * 12) + months;

                // Additional validation
                if (amount <= 0 || annualRate <= 0 || totalMonths <= 0) {
                    showAlert("Klaida", "Neteisingos reikšmės", "Įsitikinkite, kad suma, procentai ir terminas yra teigiami skaičiai.");
                    return;
                }

                paymentData.clear();

                if (annuity.isSelected()) {
                    double monthlyPayment = amount * monthlyRate / (1 - Math.pow(1 + monthlyRate, -totalMonths));
                    resultField.setText(String.format("%.2f €", monthlyPayment));
                    generateAnnuitySchedule(amount, monthlyRate, totalMonths, monthlyPayment);

                    // Update chart for annuity payments
                    graphSection.updateChartFromData(paymentData, amount);
                } else {
                    double principalPayment = amount / totalMonths;
                    double firstMonthPayment = principalPayment + (amount * monthlyRate);
                    resultField.setText(String.format("%.2f € - %.2f €", firstMonthPayment, principalPayment + (principalPayment * monthlyRate)));
                    generateLinearSchedule(amount, monthlyRate, totalMonths, principalPayment);

                    // Update chart for linear payments
                    graphSection.updateChartFromData(paymentData, amount);
                }
            } catch (NumberFormatException ex) {
                showAlert("Klaida", "Neteisingas formatas", "Prašome įvesti tinkamus skaičius.");
                paymentData.clear();
            }
        });

        return inputContent;
    }

    private VBox createDefermentSection() {
        VBox defermentSection = new VBox(10);
        defermentSection.setPadding(new Insets(10));
        defermentSection.setStyle("-fx-background-color: #f8f8f8; -fx-border-color: #ddd; -fx-border-radius: 5;");

        Label defermentTitle = new Label("Atidėjimo nustatymai");
        defermentTitle.setFont(Font.font("System", FontWeight.BOLD, 14));

        // Start date picker
        Label startDateLabel = new Label("Paskolos pradžios data:");
        DatePicker startDatePicker = new DatePicker(LocalDate.now());
        startDatePicker.setPrefWidth(200);

        // Deferment controls
        CheckBox defermentCheck = new CheckBox("Taikyti mokėjimų atidėjimą");
        defermentCheck.setSelected(false);

        GridPane defermentGrid = new GridPane();
        defermentGrid.setHgap(10);
        defermentGrid.setVgap(10);
        defermentGrid.setDisable(true);

        Label deferStartLabel = new Label("Atidėjimo pradžia:");
        DatePicker deferStartPicker = new DatePicker(LocalDate.now().plusMonths(1));
        deferStartPicker.setPrefWidth(200);

        Label deferDurationLabel = new Label("Atidėjimo trukmė (mėn.):");
        TextField deferDurationField = new TextField("1");
        deferDurationField.setPrefWidth(50);

        Label deferRateLabel = new Label("Atidėjimo palūkanos (%):");
        TextField deferRateField = new TextField("0");
        deferRateField.setPrefWidth(50);

        defermentGrid.add(deferStartLabel, 0, 0);
        defermentGrid.add(deferStartPicker, 1, 0);
        defermentGrid.add(deferDurationLabel, 0, 1);
        defermentGrid.add(deferDurationField, 1, 1);
        defermentGrid.add(deferRateLabel, 0, 2);
        defermentGrid.add(deferRateField, 1, 2);

        // Enable/disable deferment controls based on checkbox
        defermentCheck.selectedProperty().addListener((obs, oldVal, newVal) -> {
            defermentGrid.setDisable(!newVal);
        });

        // Update loan start date when changed
        startDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> {
            loanStartDate = newVal;
        });

        defermentSection.getChildren().addAll(
                defermentTitle,
                startDateLabel, startDatePicker,
                defermentCheck,
                defermentGrid
        );

        return defermentSection;
    }

    private VBox createDateFilterSection() {
        // Create date filter section with clear styling
        VBox filterSection = new VBox(15);
        filterSection.setPadding(new Insets(15, 0, 0, 0));
        filterSection.setStyle("-fx-background-color: #f8f8f8; -fx-border-color: #ddd; -fx-border-radius: 5; -fx-padding: 15px;");

        Label filterTitle = new Label("Mokėjimų filtravimas pagal datą");
        filterTitle.setFont(Font.font("System", FontWeight.BOLD, 14));

        GridPane dateGrid = new GridPane();
        dateGrid.setHgap(10);
        dateGrid.setVgap(10);

        Label startDateLabel = new Label("Nuo:");
        DatePicker startDatePicker = new DatePicker();
        startDatePicker.setPromptText("Pasirinkite pradžios datą");
        startDatePicker.setPrefWidth(200);

        Label endDateLabel = new Label("Iki:");
        DatePicker endDatePicker = new DatePicker();
        endDatePicker.setPromptText("Pasirinkite pabaigos datą");
        endDatePicker.setPrefWidth(200);

        dateGrid.add(startDateLabel, 0, 0);
        dateGrid.add(startDatePicker, 1, 0);
        dateGrid.add(endDateLabel, 0, 1);
        dateGrid.add(endDatePicker, 1, 1);

        Button filterButton = new Button("Filtruoti");
        filterButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-padding: 8px 15px; -fx-border-radius: 4px;");

        Button resetButton = new Button("Atstatyti");
        resetButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-padding: 8px 15px; -fx-border-radius: 4px;");

        HBox buttonBox = new HBox(10);
        buttonBox.getChildren().addAll(filterButton, resetButton);

        filterSection.getChildren().addAll(filterTitle, dateGrid, buttonBox);

        // Paruošiame filtravimo logiką
        filteredData = new FilteredList<>(paymentData, p -> true);

        filterButton.setOnAction(e -> {
            LocalDate startDate = startDatePicker.getValue();
            LocalDate endDate = endDatePicker.getValue();

            filteredData.setPredicate(payment -> {
                // Jei datos nepasirinktos, rodyti visus mokėjimus
                if (startDate == null && endDate == null) {
                    return true;
                }

                // Apskaičiuojame mokėjimo datą
                LocalDate paymentDate = calculatePaymentDate(loanStartDate, payment.getMonth());

                // Taikome filtravimo sąlygas
                boolean afterStart = startDate == null || paymentDate.compareTo(startDate) >= 0;
                boolean beforeEnd = endDate == null || paymentDate.compareTo(endDate) <= 0;

                return afterStart && beforeEnd;
            });

            // Rūšiuojame filtruotus duomenis
            SortedList<PaymentEntry> sortedData = new SortedList<>(filteredData);
            sortedData.comparatorProperty().bind(paymentTable.comparatorProperty());
            paymentTable.setItems(sortedData);
        });

        resetButton.setOnAction(e -> {
            startDatePicker.setValue(null);
            endDatePicker.setValue(null);
            filteredData.setPredicate(p -> true);
        });

        return filterSection;
    }

    public class GraphSection {
        private LineChart<Number, Number> paymentChart;
        private XYChart.Series<Number, Number> paymentSeries;
        private XYChart.Series<Number, Number> principalSeries;
        private XYChart.Series<Number, Number> interestSeries;

        public GraphSection() {
            // Create X and Y axes
            NumberAxis xAxis = new NumberAxis();
            xAxis.setLabel("Mėnuo");

            NumberAxis yAxis = new NumberAxis();
            yAxis.setLabel("Mokėjimas (€)");

            // Create the LineChart
            paymentChart = new LineChart<>(xAxis, yAxis);
            paymentChart.setTitle("Mokėjimai per laiką");
            paymentChart.setAnimated(true);
            paymentChart.setLegendVisible(true); // Display legend to distinguish series

            // Initialize data series
            paymentSeries = new XYChart.Series<>();
            paymentSeries.setName("Bendra įmoka");

            principalSeries = new XYChart.Series<>();
            principalSeries.setName("Pagrindinė suma");

            interestSeries = new XYChart.Series<>();
            interestSeries.setName("Palūkanos");

            // Add the series to the chart
            paymentChart.getData().addAll(paymentSeries, principalSeries, interestSeries);
            paymentChart.setPrefHeight(300); // Adjust size

            // Apply custom styling
            paymentChart.setStyle("-fx-background-color: white; -fx-border-color: #ddd; -fx-border-radius: 5;");
        }

        // Method to update chart with payment data from payment entries
        public void updateChartFromData(ObservableList<PaymentEntry> paymentData, double initialAmount) {
            paymentSeries.getData().clear(); // Clear old data
            principalSeries.getData().clear();
            interestSeries.getData().clear();

            // Add data points from payment entries
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

        // Get the chart to add it to the main UI
        public LineChart<Number, Number> getChart() {
            return paymentChart;
        }
    }

    private VBox createTableSection() {
        VBox tableContainer = new VBox(15);
        tableContainer.setPadding(new Insets(0, 0, 0, 10));

        // Payment table title
        Label tableTitle = new Label("Paskolos grąžinimo grafikas");
        tableTitle.setFont(Font.font("System", FontWeight.BOLD, 18));

        // Summary pane for showing totals
        HBox summaryBox = new HBox(10);
        summaryBox.setPadding(new Insets(10));
        summaryBox.setStyle("-fx-background-color: white; -fx-border-color: #ddd; -fx-border-radius: 5; -fx-background-radius: 5;");
        summaryBox.setAlignment(Pos.CENTER_LEFT);

        Label totalPaymentLabel = new Label("Bendra suma: 0.00 €");
        Label totalInterestLabel = new Label("Palūkanos: 0.00 €");

        summaryBox.getChildren().addAll(totalPaymentLabel, totalInterestLabel);

        // Setup the payment table with improved styling
        setupPaymentTable();
        paymentTable.setStyle("-fx-font-size: 14px;");
        paymentTable.setPlaceholder(new Label("Nėra duomenų. Užpildykite paskolos duomenis ir spauskite \"Skaičiuoti\"."));

        // Wrap the table in a scroll pane for better visibility
        ScrollPane scrollPane = new ScrollPane(paymentTable);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setPrefViewportHeight(400);
        scrollPane.setStyle("-fx-background: white; -fx-border-color: #ddd;");

        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        tableContainer.getChildren().addAll(tableTitle, summaryBox, scrollPane);

        // Update summary labels when payment data changes
        paymentData.addListener((javafx.collections.ListChangeListener.Change<? extends PaymentEntry> c) -> {
            if (!paymentData.isEmpty()) {
                double totalPayment = 0;
                double totalInterest = 0;

                for (PaymentEntry entry : paymentData) {
                    totalPayment += Double.parseDouble(entry.getPayment().replace(",", "."));
                    totalInterest += Double.parseDouble(entry.getInterest().replace(",", "."));
                }

                totalPaymentLabel.setText(String.format("Bendra suma: %.2f €", totalPayment));
                totalInterestLabel.setText(String.format("Palūkanos: %.2f €", totalInterest));
            } else {
                totalPaymentLabel.setText("Bendra suma: 0.00 €");
                totalInterestLabel.setText("Palūkanos: 0.00 €");
            }
        });

        return tableContainer;
    }

    private void setupPaymentTable() {
        paymentTable.setItems(paymentData);
        paymentTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        paymentTable.setStyle("-fx-border-color: #ddd;");

        // Add column for payment date
        TableColumn<PaymentEntry, Integer> monthCol = new TableColumn<>("Mėnuo");
        monthCol.setCellValueFactory(new PropertyValueFactory<>("month"));
        monthCol.setStyle("-fx-alignment: CENTER;");

        TableColumn<PaymentEntry, String> paymentCol = new TableColumn<>("Mokėjimas (€)");
        paymentCol.setCellValueFactory(new PropertyValueFactory<>("payment"));
        paymentCol.setStyle("-fx-alignment: CENTER-RIGHT;");

        TableColumn<PaymentEntry, String> principalCol = new TableColumn<>("Pagrindinė suma (€)");
        principalCol.setCellValueFactory(new PropertyValueFactory<>("principal"));
        principalCol.setStyle("-fx-alignment: CENTER-RIGHT;");

        TableColumn<PaymentEntry, String> interestCol = new TableColumn<>("Palūkanos (€)");
        interestCol.setCellValueFactory(new PropertyValueFactory<>("interest"));
        interestCol.setStyle("-fx-alignment: CENTER-RIGHT;");

        TableColumn<PaymentEntry, String> balanceCol = new TableColumn<>("Likutis (€)");
        balanceCol.setCellValueFactory(new PropertyValueFactory<>("balance"));
        balanceCol.setStyle("-fx-alignment: CENTER-RIGHT;");

        paymentTable.getColumns().addAll(monthCol, paymentCol, principalCol, interestCol, balanceCol);

        // Add row coloring for better readability
        paymentTable.setRowFactory(tv -> new TableRow<PaymentEntry>() {
            @Override
            protected void updateItem(PaymentEntry item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null) {
                    setStyle("");
                } else if (item.getMonth() % 2 == 0) {
                    setStyle("-fx-background-color: #f8f8f8;");
                } else {
                    setStyle("");
                }
            }
        });
    }

    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private LocalDate calculatePaymentDate(LocalDate startDate, int monthNumber) {
        if (startDate == null) {
            return LocalDate.now().plusMonths(monthNumber - 1);
        }
        return startDate.plusMonths(monthNumber - 1);
    }

    private void generateAnnuitySchedule(double amount, double monthlyRate, int totalMonths, double monthlyPayment) {
        double balance = amount;
        DecimalFormat df = new DecimalFormat("#.00");

        for (int month = 1; month <= totalMonths; month++) {
            double interest = balance * monthlyRate;
            double principal = monthlyPayment - interest;
            balance -= principal;

            paymentData.add(new PaymentEntry(
                    month,
                    df.format(monthlyPayment),
                    df.format(principal),
                    df.format(interest),
                    df.format(Math.max(0, balance))
            ));
        }
    }

    private void generateLinearSchedule(double amount, double monthlyRate, int totalMonths, double principalPayment) {
        double balance = amount;
        DecimalFormat df = new DecimalFormat("#.00");

        for (int month = 1; month <= totalMonths; month++) {
            double interest = balance * monthlyRate;
            double payment = principalPayment + interest;
            balance -= principalPayment;

            if (month == totalMonths) {
                // Koregavimas dėl apvalinimo paklaidų paskutinį mėnesį
                principalPayment += balance;
                payment += balance;
                balance = 0;
            }

            paymentData.add(new PaymentEntry(
                    month,
                    df.format(payment),
                    df.format(principalPayment),
                    df.format(interest),
                    df.format(Math.max(0, balance))
            ));
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static class PaymentEntry {
        private final int month;
        private final String payment;
        private final String principal;
        private final String interest;
        private final String balance;

        public PaymentEntry(int month, String payment, String principal, String interest, String balance) {
            this.month = month;
            this.payment = payment;
            this.principal = principal;
            this.interest = interest;
            this.balance = balance;
        }

        public int getMonth() {
            return month;
        }

        public String getPayment() {
            return payment;
        }

        public String getPrincipal() {
            return principal;
        }

        public String getInterest() {
            return interest;
        }

        public String getBalance() {
            return balance;
        }
    }
}