package org.example.clever_bank.pdf;

import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDTextField;
import org.example.clever_bank.util.ConfigurationManager;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CreatorBill {

    private CreatorBill() {
    }

    public void createBill(Long transactionId, String type, String bankSender, String bankRecipient, Long bankAccountSenderId, Long bankAccountRecipientId, BigDecimal amount, LocalDateTime dateCreate) throws IOException, URISyntaxException {
        URL resource = this.getClass().getClassLoader().getResource(ConfigurationManager.getProperty("pdf.template"));
        File file = new File(resource.toURI());

        PDDocument doc = new PDDocument().load(file);
        PDAcroForm acroForm = doc.getDocumentCatalog().getAcroForm();
        acroForm.setNeedAppearances(true);

        if (acroForm == null) {
            throw new IllegalArgumentException("acroForm is nor found");
        }
        DateTimeFormatter formatterDate = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter formatterTime = DateTimeFormatter.ofPattern("HH:mm:ss");
        acroForm.getField(ConfigurationManager.getProperty("pdf.bill_id")).setValue(transactionId.toString());
        acroForm.getField(ConfigurationManager.getProperty("pdf.date")).setValue(dateCreate.format(formatterDate).toString());
        acroForm.getField(ConfigurationManager.getProperty("pdf.time")).setValue(dateCreate.format(formatterTime).toString());
        acroForm.getField(ConfigurationManager.getProperty("pdf.type")).setValue(type);
        acroForm.getField(ConfigurationManager.getProperty("pdf.bank_sender")).setValue(bankSender);
        acroForm.getField(ConfigurationManager.getProperty("pdf.bank_recipient")).setValue(bankRecipient);
        acroForm.getField(ConfigurationManager.getProperty("pdf.ba_sender")).setValue(bankAccountSenderId.toString());
        acroForm.getField(ConfigurationManager.getProperty("pdf.ba_recipient")).setValue(bankAccountRecipientId.toString());
        acroForm.getField(ConfigurationManager.getProperty("pdf.amount")).setValue(amount.toString());

        doc.save(String.format(ConfigurationManager.getProperty("pdf.path.save"), transactionId));
    }

    public static CreatorBill getInstance() {
        return Holder.INSTANCE;
    }

    private static class Holder {
        public static final CreatorBill INSTANCE = new CreatorBill();
    }
}
