package org.example.clever_bank.service.text;

import org.example.clever_bank.entity.Transaction;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * The interface paper work
 */
public interface PaperWorker {

    /**
     * Create bill
     *
     * @param transactionId - transaction id
     * @param type - type of transaction
     * @param bankSender - bank sender
     * @param bankRecipient - bank recipient
     * @param bankAccountSenderId - id bank account sender
     * @param bankAccountRecipientId - id bank account recipient
     * @param amount - amount of money
     * @param dateCreate - date of create transaction
     * @throws IOException
     * @throws URISyntaxException
     */
    void createBill(Long transactionId, String type, String bankSender, String bankRecipient, Long bankAccountSenderId,
                    Long bankAccountRecipientId, BigDecimal amount, LocalDateTime dateCreate) throws IOException, URISyntaxException;

    /**
     * Create statement of customer account
     *
     * @param transaction - List transactions
     * @param periodFrom - start date
     * @param periodTo - finish date
     * @param fileType - file type
     * @return statement of account text
     * @throws IOException
     * @throws URISyntaxException
     */
    String createStatement(List<Transaction> transaction, LocalDateTime periodFrom, LocalDateTime periodTo, String fileType) throws IOException, URISyntaxException;

}
