package org.example.clever_bank.service.text.impl;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.example.clever_bank.entity.Transaction;
import org.example.clever_bank.service.text.PaperWorker;
import org.example.clever_bank.util.ConfigurationManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PaperWorkerPdf implements PaperWorker {

    private static final DateTimeFormatter formatterDate = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter formatterTime = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final DateTimeFormatter formatterTimeFormation = DateTimeFormatter.ofPattern("yyyy-MM-dd, HH:mm");
    private static final DateTimeFormatter formatterSave = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss");

    @Override
    public void createBill(Long transactionId, String type, String bankSender, String bankRecipient, Long bankAccountSenderId,
                           Long bankAccountRecipientId, BigDecimal amount, LocalDateTime dateCreate) throws IOException{
        String bill = String.format("""
                -----------------------------------------
                |           Банковский чек              |
                | Чек:                   %15.15s|
                | %-15.10s        %15.15s|
                | Тип транзакции:        %15.15s|
                | Банк отправителя:      %15.15s|             
                | Банк получателя:       %15.15s|             
                | Счет отправителя:      %15.15s|             
                | Счет получателя:       %15.15s|             
                | Сумма:                 %15.15s|             
                |----------------------------------------
                """, transactionId, dateCreate.format(formatterDate), dateCreate.format(formatterTime), type, bankSender,
                bankRecipient, bankAccountSenderId, bankAccountRecipientId, amount);

//        URL resource = this.getClass().getClassLoader().getResource("");
        File file = new File(String.format(ConfigurationManager.getProperty("pdf.path.bill.save"), transactionId));

        try(FileOutputStream out = new FileOutputStream(file)){
            out.write(bill.getBytes());
        }
//
//        PDDocument document = new PDDocument();
//        PDPage page1 = new PDPage(PDRectangle.A4);
//        PDRectangle rect = page1.getMediaBox();
//        document.addPage(page1);
//
//        PDPageContentStream cos = new PDPageContentStream(document, page1);
//        PDType1Font font = new PDType1Font(Standard14Fonts.getMappedFontName("TIMES_ROMAN"));
//
//        cos.beginText();
//        cos.setFont(font, 12);
//        cos.showText(bill);
//        cos.endText();
//        cos.close();
//
//        document.save(String.format(ConfigurationManager.getProperty("pdf.path.bill.save"), transactionId));
//        document.close();
    }

    @Override
    public void createStatement(List<Transaction> transaction, LocalDateTime periodFrom, LocalDateTime periodTo) throws IOException, URISyntaxException {

    }
}
