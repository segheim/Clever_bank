package org.example.clever_bank.service.text;

import org.example.clever_bank.entity.Account;
import org.example.clever_bank.entity.Bank;
import org.example.clever_bank.entity.BankAccount;
import org.example.clever_bank.entity.Transaction;
import org.example.clever_bank.service.text.impl.PaperWorkerPdf;
import org.example.clever_bank.util.ConfigurationManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class PaperWorkerPdfTest {

    private static final DateTimeFormatter formatterSave = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss");

    @InjectMocks
    private PaperWorkerPdf paperWorker;

    private BigDecimal amount;
    private LocalDateTime dateCreate;

    @BeforeEach
    public void init() {
        amount = BigDecimal.TEN;
        dateCreate = LocalDateTime.now();
    }

    @Test
    public void test_createBill() {
        Long transactionId = 1L;
        String type = "Transfer";
        String bankSender = "clever_bank";
        String bankRecipient = "best_bank";
        Long bankAccountSenderId = transactionId;
        Long bankAccountRecipientId = 2L;

        String path = "check/bill%s_%s.txt";

        try (MockedStatic<ConfigurationManager> manager = Mockito.mockStatic(ConfigurationManager.class)) {
            manager.when(() -> ConfigurationManager.getProperty("path.bill")).thenReturn(path);
        }

        paperWorker.createBill(transactionId, type, bankSender, bankRecipient, bankAccountSenderId,
                bankAccountRecipientId, amount, dateCreate);

        String filePath = String.format(path, transactionId, LocalDateTime.now().format(formatterSave));
        assertTrue(new File(filePath).length() != 0);
    }

    @Test
    public void test_createStatement() {
        Long id = 1l;
        String login = "Semenovich";
        String password = "semen";
        Long secondId = 2L;
        String secondLogin = "Groot";
        String secondPassword = "grooty";

        LocalDateTime dateCreateBankAccountFrom = LocalDateTime.of(2022, 06, 20, 15, 30);
        LocalDateTime dateCreateBankAccountTo = LocalDateTime.of(2023, 01, 20, 15, 30);

        LocalDateTime periodFrom = LocalDateTime.of(2023, 04, 20, 15, 30);
        LocalDateTime periodTo = LocalDateTime.of(2023, 06, 20, 15, 30);

        LocalDateTime dateCreateTransaction = LocalDateTime.of(2023, 05, 20, 15, 30);

        String path = "statement/statement%s_%s.txt";

        Account accountFrom = Account.builder()
                .id(id)
                .login(login)
                .password(password)
                .build();

        Account accountTo = Account.builder()
                .id(secondId)
                .login(secondLogin)
                .password(secondPassword)
                .build();

        Bank cleverBank = Bank.builder()
                .id(id)
                .name("clever_bank")
                .build();

        Bank bestBank = Bank.builder()
                .id(secondId)
                .name("best_bank")
                .build();

        BankAccount bankAccountFrom = BankAccount.builder()
                .id(id)
                .account(accountFrom)
                .banks(List.of(cleverBank))
                .balance(amount)
                .dateCreate(dateCreateBankAccountFrom)
                .build();

        BankAccount bankAccountTo = BankAccount.builder()
                .id(secondId)
                .account(accountTo)
                .banks(List.of(bestBank))
                .balance(amount)
                .dateCreate(dateCreateBankAccountTo)
                .build();

        Transaction transaction = Transaction.builder()
                .bankAccountFrom(bankAccountFrom)
                .bankAccountTo(bankAccountTo)
                .sum(amount)
                .type("Transfer")
                .dateCreate(dateCreateTransaction)
                .build();

        try (MockedStatic<ConfigurationManager> manager = Mockito.mockStatic(ConfigurationManager.class)) {
            manager.when(() -> ConfigurationManager.getProperty("path.statement.txt")).thenReturn(path);
        }

        List<Transaction> transactionList = List.of(transaction);
        paperWorker.createStatement(transactionList, periodFrom, periodTo, "txt");

        String filePath = String.format(path, id, LocalDateTime.now().format(formatterSave));
        assertTrue(new File(filePath).length() != 0);
    }


}
