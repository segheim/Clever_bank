package org.example.clever_bank;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.clever_bank.exception.ValidationException;
import org.example.clever_bank.view.Menu;


public class Main {
    public static void main(String[] args) throws ValidationException {

        final Logger logger = LogManager.getLogger(Main.class);

//        String str = "2023-08-29 22:45";
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
//        LocalDateTime dateTime = LocalDateTime.parse(str, formatter);
//
//        ConnectionPool connectionPool = ConnectionPool.lockingPool();
//        connectionPool.init();
//        Scanner scanner = new Scanner(System.in);
//        AuthMenu authMenu = new AuthMenu(scanner, new AccountServiceImpl(new AccountDaoImpl(connectionPool, logger)));
//        BankAccountServiceImpl bankAccountService = new BankAccountServiceImpl(
//                new BankAccountDaoImpl(connectionPool, logger), new TransactionDaoImpl(connectionPool, logger),
//                new AccountDaoImpl(connectionPool, logger), new BankDaoImpl(connectionPool, logger), new PaperWorkerPdf());
//        TransactionServiceImpl transactionService = new TransactionServiceImpl(new TransactionDaoImpl(connectionPool, logger), new BankAccountDaoImpl(connectionPool, logger),
//                new AccountDaoImpl(connectionPool, logger), new PaperWorkerPdf());
//        bankAccountService.replenishmentAccount(1l, BigDecimal.valueOf(1000));
//        transactionService.createStatementOfAccount(5l, dateTime, LocalDateTime.now());

        Menu menu = BeanRegistration.registrationMenu();
        menu.getMenu();
//        List<Transaction> tra = TransactionServiceImpl.getInstance().createStatementOfAccount(5l, dateTime, LocalDateTime.now());
//        tra.stream().forEach(System.out::println);
//        BankAccountServiceImpl.getInstance().transferMoney(5l, 3l,"lo", BigDecimal.valueOf(500));
//        System.out.println(BankAccountServiceImpl.getInstance().add(BankAccount.builder()
//                        .banks(List.of(Bank.builder().id(1l).build()))
//                        .account(Account.builder().id(5l).build())
//                        .balance(BigDecimal.ONE)
//
//                .build()));
//        List<BankAccount> bankAccounts = BankAccountDaoImpl.getInstance().readAll();
//        bankAccounts.stream().forEach(System.out::println);

//
//        Menu menu = new Menu();
//        menu.getMenu();
//        System.out.println(BankActivityMenu.getInstance().replenishmentAccount(1L));
//
//        System.out.println(BankAccountServiceImpl.getInstance().transferMoney(1l, 1l, "no", BigDecimal.valueOf(20)));
//        System.out.println(TransactionDaoImpl.getInstance().read(2L));
//        try {
//            CreatorBill.getInstance   ().createBill("1", "type", "bank1", "bank2", "ba1", "ba2", BigDecimal.TEN);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        } catch (URISyntaxException e) {
//            throw new RuntimeException(e);
//        }
//        System.out.println(BankAccountServiceImpl.getInstance().replenishmentAccount(1L, BigDecimal.valueOf(300)));

//        System.out.println(BankAccountServiceImpl.getInstance().withdrawal(1L, BigDecimal.valueOf(300)));
    }

}