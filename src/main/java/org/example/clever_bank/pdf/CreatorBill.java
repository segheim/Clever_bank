package org.example.clever_bank.pdf;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.example.clever_bank.util.ConfigurationManager;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class CreatorBill {

    public void createBill(String transactionId, String type, String bankSender, String bankRecipient, String bankAccountSender, String bankAccountRecipient, BigDecimal amount) throws IOException, URISyntaxException {
        try(PDDocument document = new PDDocument()) {

            URL resource = getClass().getClassLoader().getResource(ConfigurationManager.getProperty("pdf.template"));
            File file = new File(resource.toURI());


            PDDocument doc = new PDDocument().load(file);
            PDAcroForm acroForm = doc.getDocumentCatalog().getAcroForm();
            if (acroForm == null) {
                throw new IllegalArgumentException("acroForm is nor found");
            }
            acroForm.getField(ConfigurationManager.getProperty("pdf.bill_id")).setValue(transactionId);
            acroForm.getField(ConfigurationManager.getProperty("pdf.date")).setValue(LocalDate.now().toString());
            acroForm.getField(ConfigurationManager.getProperty("pdf.time")).setValue(LocalDateTime.now().toString());
            acroForm.getField(ConfigurationManager.getProperty("pdf.type")).setValue(type);
            acroForm.getField(ConfigurationManager.getProperty("pdf.bank_sender")).setValue(bankSender);
            acroForm.getField(ConfigurationManager.getProperty("pdf.bank_recipient")).setValue(bankRecipient);
            acroForm.getField(ConfigurationManager.getProperty("pdf.ba_sender")).setValue(bankAccountSender);
            acroForm.getField(ConfigurationManager.getProperty("pdf.ba_recipient")).setValue(bankAccountRecipient);
            acroForm.getField(ConfigurationManager.getProperty("pdf.amount")).setValue(amount.toString());

            doc.save(String.format(ConfigurationManager.getProperty("pdf.path.save"), transactionId));
        }
    }

    public static CreatorBill getInstance() {
        return Holder.INSTANCE;
    }

    private static class Holder {
        public static final CreatorBill INSTANCE = new CreatorBill();
    }
}
