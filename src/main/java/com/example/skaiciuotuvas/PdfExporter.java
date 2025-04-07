package com.example.skaiciuotuvas;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class PdfExporter {


    private static final String FONT_PATH = "fonts/arial.ttf"; // Path to your TTF font file
    private static Font lithuanianFont;
    private static Font lithuanianFontBold;

    static {
        try {

            BaseFont baseFont = BaseFont.createFont(
                    "c:/windows/fonts/arial.ttf", // Common Windows path
                    BaseFont.IDENTITY_H,
                    BaseFont.EMBEDDED
            );
            lithuanianFont = new Font(baseFont, 10);
            lithuanianFontBold = new Font(baseFont, 12, Font.BOLD);
        } catch (Exception e) {

            lithuanianFont = new Font(Font.FontFamily.HELVETICA, 10);
            lithuanianFontBold = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
        }
    }

    public static void exportToPdf(TableView<PaymentEntry> table, File file) throws IOException, DocumentException {
        Document document = new Document(PageSize.A4.rotate());
        PdfWriter.getInstance(document, new FileOutputStream(file));
        document.open();

        // Add title with Lithuanian font
        Paragraph title = new Paragraph("Paskolos grąžinimo grafikas", lithuanianFontBold);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);


        // Create table
        PdfPTable pdfTable = new PdfPTable(5);
        pdfTable.setWidthPercentage(100);
        pdfTable.setSpacingBefore(10f);
        pdfTable.setSpacingAfter(10f);

        float[] columnWidths = {1f, 2f, 2f, 2f, 2f};
        pdfTable.setWidths(columnWidths);

        // Add table headers with Lithuanian characters
        addTableHeader(pdfTable, "Mėnuo");
        addTableHeader(pdfTable, "Mokėjimas (€)");
        addTableHeader(pdfTable, "Pagrindinė suma (€)");
        addTableHeader(pdfTable, "Palūkanos (€)");
        addTableHeader(pdfTable, "Likutis (€)");

        // Add table rows
        ObservableList<PaymentEntry> items = table.getItems();
        for (PaymentEntry item : items) {
            addTableCell(pdfTable, String.valueOf(item.getMonth()));
            addTableCell(pdfTable, item.getPayment());
            addTableCell(pdfTable, item.getPrincipal());
            addTableCell(pdfTable, item.getInterest());
            addTableCell(pdfTable, item.getBalance());
        }

        document.add(pdfTable);
        document.close();
    }

    private static void addTableHeader(PdfPTable table, String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, lithuanianFontBold));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBackgroundColor(new BaseColor(51, 122, 183));
        cell.setPadding(5);
        table.addCell(cell);
    }

    private static void addTableCell(PdfPTable table, String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, lithuanianFont));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(5);
        table.addCell(cell);
    }
}