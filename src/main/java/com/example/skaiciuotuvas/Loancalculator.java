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
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Loancalculator extends Application {

    private TableView<PaymentEntry> paymentTable = new TableView<>();
    private ObservableList<PaymentEntry> paymentData = FXCollections.observableArrayList();
    private FilteredList<PaymentEntry> filteredData;
    private GraphSection graphSection;
    private LocalDate loanStartDate = LocalDate.now();

    private TextField deferDurationField = new TextField("0");
    private TextField deferRateField = new TextField("0");
    private LocalDate defermentStartDate = LocalDate.now().plusMonths(1);

    @Override
    public void start(Stage primaryStage) {
        BorderPane mainLayout = new BorderPane();
        mainLayout.setPadding(new Insets(20));
        mainLayout.setStyle("-fx-background-color: #f5f5f5;");

        graphSection = new GraphSection();

        VBox inputSection = createInputSection();
        VBox tableSection = createTableSection();

        VBox rightSection = new VBox(15);
        rightSection.getChildren().addAll(graphSection.getChart(), tableSection);
        VBox.setVgrow(tableSection, Priority.ALWAYS);

        mainLayout.setLeft(inputSection);
        mainLayout.setCenter(rightSection);

        Scene scene = new Scene(mainLayout, 1200, 800);
        primaryStage.setTitle("Paskolos Skaičiuoklė");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private VBox createInputSection() {
        VBox inputContent = new VBox(10);
        inputContent.setPadding(new Insets(10));
        inputContent.setStyle("-fx-background-color: white; -fx-border-color: #ddd; -fx-border-radius: 5; -fx-background-radius: 5;");
        inputContent.setMinWidth(350);

        Label titleLabel = new Label("Paskolos informacija");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
        titleLabel.setPadding(new Insets(0, 0, 10, 0));

        Label loanAmountLabel = new Label("Pageidaujama paskolos suma (€)");
        TextField loanAmountField = new TextField();
        loanAmountField.setPromptText("Pvz.: 10000");
        loanAmountField.setStyle("-fx-padding: 8px; -fx-border-color: #ddd; -fx-border-radius: 3px;");

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

        Label annualInterestLabel = new Label("Metinė palūkanų norma (%)");
        TextField annualInterestField = new TextField();
        annualInterestField.setPromptText("Pvz.: 2.5");
        annualInterestField.setStyle("-fx-padding: 8px; -fx-border-color: #ddd; -fx-border-radius: 3px;");

        Label paymentTypeLabel = new Label("Grąžinimo grafikas");
        ToggleGroup paymentTypeGroup = new ToggleGroup();
        RadioButton annuity = new RadioButton("Anuitetas (vienoda įmoka)");
        RadioButton linear = new RadioButton("Linijinis (mažėjanti įmoka)");
        annuity.setToggleGroup(paymentTypeGroup);
        annuity.setSelected(true);
        linear.setToggleGroup(paymentTypeGroup);

        VBox paymentTypeBox = new VBox(10);
        paymentTypeBox.getChildren().addAll(annuity, linear);

        TitledPane defermentSection = createDefermentSection();

        Button calculateButton = new Button("Skaičiuoti");
        calculateButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10px 15px; -fx-cursor: hand; -fx-border-radius: 4px;");
        calculateButton.setPrefWidth(150);
        calculateButton.setOnMouseEntered(e -> calculateButton.setStyle("-fx-background-color: #0b7dda; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10px 15px; -fx-cursor: hand; -fx-border-radius: 4px;"));
        calculateButton.setOnMouseExited(e -> calculateButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10px 15px; -fx-cursor: hand; -fx-border-radius: 4px;"));

        Label resultLabel = new Label("Mėnesinė įmoka:");
        TextField resultField = new TextField();
        resultField.setEditable(false);
        resultField.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-background-color: #e8f4f8; -fx-text-fill: #0b7dda; -fx-padding: 8px;");

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

        calculateButton.setOnAction(e -> {
            try {
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

                if (amount <= 0 || annualRate <= 0 || totalMonths <= 0) {
                    showAlert("Klaida", "Neteisingos reikšmės", "Įsitikinkite, kad suma, procentai ir terminas yra teigiami skaičiai.");
                    return;
                }
                int deferDuration = Integer.parseInt(deferDurationField.getText());
                double deferRate = Double.parseDouble(deferRateField.getText().replace(",", "."));

                if (deferDuration > 0) {
                    totalMonths += deferDuration;
                }

                paymentData.clear();

                if (annuity.isSelected()) {
                    double monthlyPayment = amount * monthlyRate / (1 - Math.pow(1 + monthlyRate, -totalMonths));
                    resultField.setText(String.format("%.2f €", monthlyPayment));
                    generateAnnuitySchedule(amount, monthlyRate, totalMonths, monthlyPayment);
                    graphSection.updateChartFromData(paymentData, amount);
                } else {
                    double principalPayment = amount / totalMonths;
                    double firstMonthPayment = principalPayment + (amount * monthlyRate);
                    resultField.setText(String.format("%.2f € - %.2f €", firstMonthPayment, principalPayment + (principalPayment * monthlyRate)));
                    generateLinearSchedule(amount, monthlyRate, totalMonths, principalPayment);
                    graphSection.updateChartFromData(paymentData, amount);
                }
            } catch (NumberFormatException ex) {
                showAlert("Klaida", "Neteisingas formatas", "Prašome įvesti tinkamus skaičius.");
                paymentData.clear();
            }
        });

        return inputContent;
    }

    private TitledPane createDefermentSection() {
        VBox defermentContent = new VBox(10);
        defermentContent.setPadding(new Insets(10));
        defermentContent.setStyle("-fx-background-color: #f8f8f8; -fx-border-color: #ddd; -fx-border-radius: 5;");

        GridPane defermentGrid = new GridPane();
        defermentGrid.setHgap(10);
        defermentGrid.setVgap(10);

        Label deferStartLabel = new Label("Atidėjimo pradžia:");
        DatePicker deferStartPicker = new DatePicker(LocalDate.now().plusMonths(1));
        deferStartPicker.valueProperty().addListener((obs, oldVal, newVal) -> {
            defermentStartDate = newVal;
        });
        deferStartPicker.setPrefWidth(200);

        Label deferDurationLabel = new Label("Atidėjimo trukmė (mėn.):");
        deferDurationField.setPrefWidth(50);

        Label deferRateLabel = new Label("Atidėjimo palūkanos (%):");
        deferRateField.setPrefWidth(50);

        defermentGrid.add(deferStartLabel, 0, 0);
        defermentGrid.add(deferStartPicker, 1, 0);
        defermentGrid.add(deferDurationLabel, 0, 1);
        defermentGrid.add(deferDurationField, 1, 1);
        defermentGrid.add(deferRateLabel, 0, 2);
        defermentGrid.add(deferRateField, 1, 2);

        defermentContent.getChildren().addAll(defermentGrid);

        TitledPane defermentPane = new TitledPane("Atidėjimo nustatymai", defermentContent);
        defermentPane.setAnimated(true);
        defermentPane.setExpanded(false);
        defermentPane.setStyle("-fx-background-color: #f8f8f8; -fx-border-color: #ddd;");

        return defermentPane;
    }

    private VBox createDateFilterSection() {
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

        filteredData = new FilteredList<>(paymentData, p -> true);

        filterButton.setOnAction(e -> {
            LocalDate startDate = startDatePicker.getValue();
            LocalDate endDate = endDatePicker.getValue();

            filteredData.setPredicate(payment -> {
                if (startDate == null && endDate == null) {
                    return true;
                }

                LocalDate paymentDate = calculatePaymentDate(loanStartDate, payment.getMonth());

                boolean afterStart = startDate == null || paymentDate.compareTo(startDate) >= 0;
                boolean beforeEnd = endDate == null || paymentDate.compareTo(endDate) <= 0;

                return afterStart && beforeEnd;
            });

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

    private VBox createTableSection() {
        VBox tableContainer = new VBox(5);
        tableContainer.setPadding(new Insets(0, 0, 0, 20));

        Label tableTitle = new Label("Paskolos grąžinimo grafikas");
        tableTitle.setFont(Font.font("System", FontWeight.BOLD, 16));

        HBox summaryBox = new HBox(10);

        setupPaymentTable();
        paymentTable.setStyle("-fx-font-size: 12px;");
        paymentTable.setFixedCellSize(25);
        paymentTable.setPrefHeight(10 * 25 + 30);

        ScrollPane scrollPane = new ScrollPane(paymentTable);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setPrefHeight(paymentTable.getPrefHeight() + 2);
        scrollPane.setStyle("-fx-background: white; -fx-border-color: #ddd;");

        tableContainer.getChildren().addAll(tableTitle, summaryBox, scrollPane);

        return tableContainer;
    }

    private void setupPaymentTable() {
        paymentTable.setItems(paymentData);
        paymentTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        paymentTable.setStyle("-fx-border-color: #ddd;");
        paymentTable.setMaxHeight(Region.USE_PREF_SIZE);
        paymentTable.setMinHeight(Region.USE_PREF_SIZE);

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

        int deferDuration = 0;
        double deferRate = 0;
        try {
            deferDuration = Integer.parseInt(deferDurationField.getText());
            deferRate = Double.parseDouble(deferRateField.getText()) / 100 / 12;
        } catch (NumberFormatException e) {
            // Use defaults if parsing fails
        }

        int deferStartMonth = 1;
        if (deferDuration > 0 && defermentStartDate != null) {
            deferStartMonth = (int) loanStartDate.until(defermentStartDate, ChronoUnit.MONTHS) + 1;
            if (deferStartMonth < 1) deferStartMonth = 1;
        }

        for (int month = 1; month <= totalMonths; month++) {
            double interest, principal;

            if (deferDuration > 0 && month >= deferStartMonth && month < deferStartMonth + deferDuration) {
                interest = balance * deferRate;
                principal = 0;
                paymentData.add(new PaymentEntry(
                        month,
                        df.format(0.00),
                        df.format(principal),
                        df.format(interest),
                        df.format(balance)
                ));
                balance += interest;
            } else {
                if (month == deferStartMonth + deferDuration) {
                    int remainingMonths = totalMonths - (deferStartMonth + deferDuration - 1);
                    monthlyPayment = balance * monthlyRate / (1 - Math.pow(1 + monthlyRate, -remainingMonths));
                }

                interest = balance * monthlyRate;
                principal = monthlyPayment - interest;
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
    }

    private void generateLinearSchedule(double amount, double monthlyRate, int totalMonths, double principalPayment) {
        double balance = amount;
        DecimalFormat df = new DecimalFormat("#.00");

        int deferDuration = 0;
        double deferRate = 0;
        try {
            deferDuration = Integer.parseInt(deferDurationField.getText());
            deferRate = Double.parseDouble(deferRateField.getText()) / 100 / 12;
        } catch (NumberFormatException e) {
            // Use defaults if parsing fails
        }

        int deferStartMonth = 1;
        if (deferDuration > 0 && defermentStartDate != null) {
            deferStartMonth = (int) loanStartDate.until(defermentStartDate, ChronoUnit.MONTHS) + 1;
            if (deferStartMonth < 1) deferStartMonth = 1;
        }

        boolean defermentActive = deferDuration > 0;
        int paymentStartMonth = defermentActive ? deferStartMonth + deferDuration : 1;
        int actualPaymentMonths = totalMonths - (defermentActive ? deferDuration : 0);

        for (int month = 1; month <= totalMonths; month++) {
            double interest, payment;
            double currentPrincipal = 0;

            if (defermentActive && month >= deferStartMonth && month < deferStartMonth + deferDuration) {
                interest = balance * deferRate;
                payment = 0;
                currentPrincipal = 0;

                paymentData.add(new PaymentEntry(
                        month,
                        df.format(payment),
                        df.format(currentPrincipal),
                        df.format(interest),
                        df.format(balance)
                ));

                balance += interest;
            } else {
                interest = balance * monthlyRate;

                if (month == paymentStartMonth) {
                    principalPayment = balance / (totalMonths - month + 1);
                }

                currentPrincipal = principalPayment;
                payment = currentPrincipal + interest;
                balance -= currentPrincipal;

                if (month == totalMonths) {
                    currentPrincipal += balance;
                    payment += balance;
                    balance = 0;
                }

                paymentData.add(new PaymentEntry(
                        month,
                        df.format(payment),
                        df.format(currentPrincipal),
                        df.format(interest),
                        df.format(Math.max(0, balance))
                ));
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}