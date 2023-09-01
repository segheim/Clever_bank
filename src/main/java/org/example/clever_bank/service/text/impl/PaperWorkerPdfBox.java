package org.example.clever_bank.service.text.impl;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.io.RandomAccessReadBufferedFile;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.apache.pdfbox.pdmodel.interactive.form.PDTextField;
import org.example.clever_bank.entity.Transaction;
import org.example.clever_bank.service.text.PaperWorker;
import org.example.clever_bank.util.ConfigurationManager;
import org.example.clever_bank.util.Constant;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class PaperWorkerPdfBox implements PaperWorker {

    public static final String PDF_FORM_NAME_BILL_ID = "bill_id";
    public static final String PDF_FORM_NAME_DATE = "date";
    public static final String PDF_FORM_NAME_TIME = "time";
    public static final String PDF_FORM_NAME_TYPE = "type";
    public static final String PDF_FORM_NAME_BANK_SENDER = "bank_sender";
    public static final String PDF_FORM_NAME_PDF_BANK_RECIPIENT = "pdf.bank_recipient";
    public static final String PDF_FORM_NAME_PDF_BA_SENDER = "pdf.ba_sender";
    public static final String PDF_FORM_NAME_PDF_BA_RECIPIENT = "pdf.ba_recipient";
    public static final String PDF_FORM_NAME_AMOUNT = "amount";

    private static final DateTimeFormatter formatterDate = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter formatterTime = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final DateTimeFormatter formatterTimeFormation = DateTimeFormatter.ofPattern("yyyy-MM-dd, HH:mm");
    private static final DateTimeFormatter formatterSave = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss");

    public PaperWorkerPdfBox() {
    }

    @Override
    public void createBill(Long transactionId, String type, String bankSender, String bankRecipient, Long bankAccountSenderId,
                           Long bankAccountRecipientId, BigDecimal amount, LocalDateTime dateCreate) throws IOException, URISyntaxException {
        URL resource = this.getClass().getClassLoader().getResource(ConfigurationManager.getProperty("path.bill.template"));
        File file = new File(resource.toURI());

        PDDocument doc = Loader.loadPDF(new RandomAccessReadBufferedFile(file));
        PDAcroForm acroForm = doc.getDocumentCatalog().getAcroForm();
        acroForm.setNeedAppearances(true);

        acroForm.getField(PDF_FORM_NAME_BILL_ID).setValue(transactionId.toString());
        acroForm.getField(PDF_FORM_NAME_DATE).setValue(dateCreate.format(formatterDate));
        acroForm.getField(PDF_FORM_NAME_TIME).setValue(dateCreate.format(formatterTime));
        acroForm.getField(PDF_FORM_NAME_TYPE).setValue(type);
        acroForm.getField(PDF_FORM_NAME_BANK_SENDER).setValue(bankSender);
        acroForm.getField(PDF_FORM_NAME_PDF_BANK_RECIPIENT).setValue(bankRecipient);
        acroForm.getField(PDF_FORM_NAME_PDF_BA_SENDER).setValue(bankAccountSenderId.toString());
        acroForm.getField(PDF_FORM_NAME_PDF_BA_RECIPIENT).setValue(bankAccountRecipientId.toString());
        acroForm.getField(PDF_FORM_NAME_AMOUNT).setValue(amount.toString());

        doc.save(String.format(ConfigurationManager.getProperty("path.bill"), transactionId));
    }

    @Override
    public void createStatement(List<Transaction> transactions, LocalDateTime periodFrom, LocalDateTime periodTo) throws IOException, URISyntaxException {
        URL resource = this.getClass().getClassLoader().getResource(ConfigurationManager.getProperty("path.statement.template"));
        File file = new File(resource.toURI());

        PDDocument doc = Loader.loadPDF(new RandomAccessReadBufferedFile(file));
        PDAcroForm acroForm = doc.getDocumentCatalog().getAcroForm();
        acroForm.setNeedAppearances(true);
        PDTextField pdTextField = new PDTextField(acroForm);
        pdTextField.setValue("newsdsd");


        acroForm.getField("account").setValue(transactions.get(Constant.INDEX).getBankAccountFrom().getAccount().getLogin());
        acroForm.getField("id").setValue(transactions.get(Constant.INDEX).getBankAccountFrom().getId().toString());
        acroForm.getField("currency").setValue("BYN");
        acroForm.getField("date_create").setValue(transactions.get(Constant.INDEX).getBankAccountFrom().getDateCreate().format(formatterDate));
        acroForm.getField("period_from").setValue(periodFrom.format(formatterDate));
        acroForm.getField("period_to").setValue(periodTo.format(formatterDate));
        acroForm.getField("statement_date").setValue(LocalDateTime.now().format(formatterTimeFormation));
        acroForm.getField("balance").setValue(transactions.get(Constant.INDEX).getBankAccountFrom().getBalance().toString());

//        PDPage page = doc.getPages().get(0);


        List<PDField> fields = new ArrayList<>();
        fields.add(pdTextField);
        acroForm.setFields(fields);
//        int i = 0;
//        for (Transaction transaction : transactions) {
//            PDDocument docInternal = Loader.loadPDF(new RandomAccessReadBufferedFile(file));
//            PDDocumentCatalog docCatalogInternal = docInternal.getDocumentCatalog();
//            PDAcroForm acroFormInternal = doc.getDocumentCatalog().getAcroForm();
////            PDField field = acroFormInternal.getField("SampleField");
////            field.setValue(value);
//            PDField transactionDateField = acroFormInternal.getField("transaction_date");
//            transactionDateField.setValue(transaction.getDateCreate().format(formatterDate));
//            transactionDateField.setAlternateFieldName("transaction_date" + i++);
//            PDField transactionNameField = acroFormInternal.getField("transaction_name");
//            transactionNameField.setValue(transaction.getDateCreate().format(formatterDate));
//            transactionNameField.setAlternateFieldName("transaction_name" + i++);
//            PDField transactionSumField = acroFormInternal.getField("transaction_sum");
//            transactionSumField.setValue(transaction.getDateCreate().format(formatterDate));
//            transactionSumField.setAlternateFieldName("transaction_sum" + i++);
//            fields.add(transactionDateField);
//            fields.add(transactionNameField);
//            fields.add(transactionSumField);
//            PDPageTree pages = docCatalogInternal.getPages();
//            doc.addPage(pages.get(0));
//        }
//        doc.getDocumentCatalog().setAcroForm(acroForm);
//        acroForm.setFields(fields);

        doc.save(String.format(ConfigurationManager.getProperty("path.statement"), transactions.get(Constant.INDEX).getBankAccountFrom().getId(), LocalDateTime.now().format(formatterSave)));

    }
}
