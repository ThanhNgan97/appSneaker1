package com.example.sneakershop3.Report;

import com.example.sneakershop3.dbConnection.connectionClass;
import com.itextpdf.kernel.colors.DeviceGray;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class pdfReceiptGenerator {
    public void pdfReceipt(Stage stage, int recId) {
        Connection connection = connectionClass.getConnection();
        List<billData> billDataList = new ArrayList<>();

        try {
            String sqlQuery = "SELECT pd.prod_name, rd.recd_quantity, pd.prod_price, r.rec_totalAmount, r.rec_Id " +
                    "FROM productdetails pd " +
                    "JOIN receiptdetails rd ON pd.prod_barCode = rd.prod_barCode " +
                    "JOIN receipt r ON rd.rec_Id = r.rec_Id " +
                    "WHERE r.rec_Id = ?;";

            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            preparedStatement.setInt(1, recId);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                billData billDate = new billData(
                        resultSet.getString(1),
                        resultSet.getInt(2),
                        resultSet.getDouble(3),
                        resultSet.getDouble(4),
                        resultSet.getInt(5)
                );
                billDataList.add(billDate);
            }

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save PDF");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Save Invoice File", "*.pdf"));
            File file = fileChooser.showSaveDialog(stage);

            if (file == null) {
                System.out.println("File saving was canceled.");
                return;
            }

            // Create PdfWriter and PdfDocument
            PdfWriter pdf = new PdfWriter(file.getAbsolutePath());
            PdfDocument pdfDocument = new PdfDocument(pdf);
            pdfDocument.setDefaultPageSize(new PageSize(400, 650));

            // Create Document
            Document document = new Document(pdfDocument);

            String fontPath = "C:\\Windows\\Fonts\\times.ttf";  // Ensure this path is correct
            PdfFont font = PdfFontFactory.createFont(fontPath, "Identity-H");

            // Add title
            Paragraph title = new Paragraph("SALES RECEIPT").setFont(font)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setBold()
                    .setFontSize(16);
            document.add(title);

            // Add separator
            Paragraph separator = new Paragraph("--------------------------------------------");
            separator.setTextAlignment(TextAlignment.CENTER);
            document.add(separator);

            // Add date and time
            LocalDateTime currentDateTime = LocalDateTime.now();
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

            document.add(new Paragraph("Date: " + currentDateTime.format(dateFormatter)).setFont(font));
            document.add(new Paragraph("Printed at: " + currentDateTime.format(timeFormatter)).setFont(font));

            // Add table
            float[] columnWidths = {1, 5, 1, 2, 2};
            Table table = new Table(UnitValue.createPercentArray(columnWidths));
            table.setWidth(UnitValue.createPercentValue(100));
            table.setFont(font);

            // Add table headers
            table.addHeaderCell(new Cell().add(new Paragraph("No.").setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("Product Name").setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("Quantity").setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("Price").setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("Total").setBold()));

            int stt = 0;
            double totalPrice = 0.0;
            int totalQuantity = 0;

            for (billData e : billDataList) {
                stt++;
                table.addCell(new Cell().add(new Paragraph(String.valueOf(stt))));
                table.addCell(new Cell().add(new Paragraph(e.getProductName())));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(e.getQuantity()))));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(e.getPrice()))));

                double itemTotal = e.getQuantity() * e.getPrice();
                table.addCell(new Cell().add(new Paragraph(String.valueOf(itemTotal))));
                totalPrice += itemTotal;
                totalQuantity += e.getQuantity();
            }

            document.add(table);

            // Add total quantity and total amount
            document.add(new Paragraph("Total Quantity: " + totalQuantity).setFont(font));
            document.add(new Paragraph("Total Payment: " + totalPrice).setFont(font));

            // Add thank you note
            Paragraph thankYou = new Paragraph("Thank you for coming!").setFont(font);
            thankYou.setTextAlignment(TextAlignment.CENTER);
            document.add(thankYou);

            // Close the document
            document.close();
            System.out.println("Receipt generated successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}