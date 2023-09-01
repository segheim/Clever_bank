package org.example.clever_bank.service.text;

import org.example.clever_bank.entity.Transaction;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.List;

public interface PaperWorker {

    void createBill(Long transactionId, String type, String bankSender, String bankRecipient, Long bankAccountSenderId,
                    Long bankAccountRecipientId, BigDecimal amount, LocalDateTime dateCreate) throws IOException, URISyntaxException;

    String createStatement(List<Transaction> transaction, LocalDateTime periodFrom, LocalDateTime periodTo) throws IOException, URISyntaxException;

}
