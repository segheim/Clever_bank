package org.example.clever_bank.service.text.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.clever_bank.entity.Loggable;
import org.example.clever_bank.entity.Transaction;
import org.example.clever_bank.service.text.PaperWorker;
import org.example.clever_bank.util.ConfigurationManager;
import org.example.clever_bank.util.Constant;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PaperWorkerPdf implements PaperWorker {

    private static final Logger logger = LogManager.getLogger(PaperWorkerPdf.class);

    private static final DateTimeFormatter formatterDate = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private static final DateTimeFormatter formatterDateWIthPoints = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final DateTimeFormatter formatterTime = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final DateTimeFormatter formatterTimeFormation = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH.mm");
    private static final DateTimeFormatter formatterSave = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss");


    @Override
    @Loggable
    public void createBill(Long transactionId, String type, String bankSender, String bankRecipient, Long bankAccountSenderId,
                           Long bankAccountRecipientId, BigDecimal amount, LocalDateTime dateCreate){
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
                |---------------------------------------|
                """, transactionId, dateCreate.format(formatterDate), dateCreate.format(formatterTime), type, bankSender,
                bankRecipient, bankAccountSenderId, bankAccountRecipientId, amount);

//        URL resource = this.getClass().getClassLoader().getResource("");
        try {
            writeToFile(transactionId, bill, ConfigurationManager.getProperty("path.bill"));
        } catch (IOException e) {
            logger.error("Could not write to file");
        }
    }

    @Override
    @Loggable
    public String createStatement(List<Transaction> transactions, LocalDateTime periodFrom, LocalDateTime periodTo) {
        String statementOfAccount = String.format("""                
                                      Выписка
                                    Clever Bank
                Клиент                    | %-35.35s
                Счет                      | %-35.35s
                Валюта                    | BYN
                Дата открытия             | %-35.35s
                Период                    | %-10.10s - %-10.10s
                Дата и время формирования | %-35.35s
                Остаток                   | %-35.35s
                      Дата     |         Примечание             |  Сумма
                -----------------------------------------------------
                """, transactions.get(Constant.INDEX).getBankAccountFrom().getAccount().getLogin(),
                    transactions.get(Constant.INDEX).getBankAccountFrom().getId().toString(),
                    transactions.get(Constant.INDEX).getBankAccountFrom().getDateCreate().format(formatterDateWIthPoints),
                    periodFrom.format(formatterDateWIthPoints), periodTo.format(formatterDateWIthPoints),
                    LocalDateTime.now().format(formatterTimeFormation),
                    transactions.get(Constant.INDEX).getBankAccountFrom().getBalance().toString());

        StringBuilder stringBuilderBill = fetchLineTransactionInfo(transactions, statementOfAccount);
        try {
            writeToFile(transactions.get(Constant.INDEX).getBankAccountFrom().getId(),
                    stringBuilderBill.toString(), ConfigurationManager.getProperty("path.statement"));
        } catch (IOException e) {
            logger.error("Could not write to file");
        }
        return stringBuilderBill.toString();
    }

    private void writeToFile(Long transactionId, String bill, String path) throws IOException {
        File file = new File(String.format(path, transactionId, LocalDateTime.now().format(formatterSave)));
        try(FileOutputStream out = new FileOutputStream(file)){
            out.write(bill.getBytes());
        }
    }

    private static StringBuilder fetchLineTransactionInfo(List<Transaction> transactions, String statementOfAccount) {
        StringBuilder stringBuilderBill = new StringBuilder(statementOfAccount);
        for (Transaction transaction : transactions) {
            String transactionLine = String.format(" %-11.11s   | %-30.30s | %-11.11s",
                    transaction.getDateCreate().format(formatterDateWIthPoints), transaction.getType(), transaction.getSum());
            stringBuilderBill.append(transactionLine).append("\n");
        }
        return stringBuilderBill;
    }
}
