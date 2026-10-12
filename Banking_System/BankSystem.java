package Banking_System;
import java.lang.classfile.attribute.SourceDebugExtensionAttribute;
import java.util.*;
import java.util.Date;
import java.util.UUID;


//Address (value object)

class Address{
    private String street;
    private String city;
    private String state;
    private String zipCode;

    public Address(String street,String city,String state,String zipCode){
        this.street=street;
        this.city=city;
        this.state=state;
        this.zipCode=zipCode;
    }
}

//Person (abstract base class)

abstract class Person{
    private String id;
    private String name;
    private  Address address;
    private String phone;
    private  String email;

    public Person(String id, String name, Address address, String phone, String email) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.email = email;
    }

    public String getId(){
        return id;
    }
    public String getName(){
        return name;
    }
    public String getContactInfo(){
        return "Phone number: "+phone+"\n"+
                "Email: "+email;
    }
}

enum AccountStatus{
    ACTIVE,
    CLOSED,
    FROZEN
}

//Customer extends Person

class Customer extends Person{
    private  List<Account> accounts;
    private boolean kycVerified;

    public Customer(String id,
                    String name,
                    Address address,
                    String phone,
                    String email,
                    boolean kycVerified){
        super(id, name, address, phone, email);
        this.accounts=new ArrayList<>();
        this.kycVerified=kycVerified;
    }

    public void addAccount(Account account){
        accounts.add(account);
    }

    public void removeAccount(String accountId) {

        for (int i = 0; i < accounts.size(); i++) {

            if (accounts.get(i).getAccountNumber().equals(accountId)) {
                accounts.remove(i);
                break;
            }
        }
    }

    public List<Account> getAccounts(){
        List<Account> copyAccounts=new ArrayList<>(accounts);
        return copyAccounts;
    }

    public double getTotalBalance(){
        double totalBalance=0;
        for(Account account: accounts){
            totalBalance+=account.getBalance();
        }
        return totalBalance;
    }
}

//Employee extends Person

enum Role{
    TELLER,
    MANAGER,
    ADMIN
}

enum AccountType{
    SAVINGS,
    CURRENT,
    FIXED_DEPOSIT
}

class Employee extends Person{
    private String employeeId;
    private Role role;

    public  Employee(String id, String name, Address address, String phone, String email,String employeeId,Role role){
        super(id, name, address, phone, email);
        this.employeeId=employeeId;
        this.role=role;
    }
    public Account openAccount(Customer customer,AccountType type){
      Account newAccount = AccountFactory.createAccount(customer,type);
      if(!Bank.getInstance().registerAccount(newAccount)){
          return null;
      }
      customer.addAccount(newAccount);
      return newAccount;
    }

    public void closeAccount(String accountId){
        boolean closeStatus=Bank.getInstance().closeAccount(accountId);
       if(closeStatus){
           System.out.println("Account is closed now");
       }else{
           System.out.println("Invalid account ID");
       }
    }

    public void approveLoan(String loanId){}
}

//Account (abstract class)
abstract class Account{
    protected String accountNumber;
    private double balance;
    protected  Customer owner;
    private AccountStatus status;
    private List<Transaction> transactions;
    protected Date createdDate;


    public Account(String accountNumber,double balance,Customer owner,AccountStatus status,Date createdDate){
        this.accountNumber=accountNumber;
        this.balance=balance;
        this.owner=owner;
        this.status=status;
        this.createdDate=createdDate;
        this.transactions=new ArrayList<>();
    }
    public void setAccountStatus(AccountStatus status){
        this.status=status;
    }
    public AccountStatus getStatus() {
        return status;
    }
    public boolean deposit(double amount){
        if(amount>0) {
            balance+=amount;
            return true;
        }
        return false;
    }

    public abstract boolean withdraw(double amount);

    public double getBalance(){
        return balance;
    }
    protected void deductBalance(double amount){
        balance-=amount;
    }

    public List<Transaction> getStatement(){
        List<Transaction> copy=new ArrayList<>(transactions);
        return copy;
    }


    protected  void addTransaction(Transaction txn){
        transactions.add(txn);
    }

    public abstract double calculateInterest();
    public String getAccountNumber(){
        return accountNumber;
    }
}
//SavingsAccount extends Account

class SavingsAccount extends Account{
    private double interestRate;
    private double minBalance;

    public SavingsAccount(String accountNumber,
                          double balance,
                          Customer owner,
                          AccountStatus status,
                          Date createdDate,
                          double interestRate,
                          double minBalance) {

        super(accountNumber, balance, owner, status, createdDate);

        this.interestRate = interestRate;
        this.minBalance = minBalance;
    }
    @Override
    public boolean withdraw(double amount){
        if(amount>0 && getBalance()-amount>=minBalance ){
            deductBalance(amount);
            return true;
        }
        return false;
    }
    @Override
    public double calculateInterest(){
        return getBalance()*interestRate;
    }
}

//CurrentAccount extends Account

class CurrentAccount extends Account{
    private double overdraftLimt;

    public  CurrentAccount(String accountNumber,
                           double balance,
                           Customer owner,
                           AccountStatus status,
                           Date createdDate,
                           double overdraftLimt){

        super(accountNumber, balance, owner, status, createdDate);
        this.overdraftLimt=overdraftLimt;
    }
    @Override
    public boolean withdraw(double amount){
        if(amount>0 && getBalance()-amount>=(-overdraftLimt)){
            deductBalance(amount);
            return true;
        }
        return false;
    }
    @Override
    public double calculateInterest(){
        System.out.println("No interest");
        return 0;
    }
}

//FixedDepositAccount extends Account

class FixedDepositAccount extends Account {

    private Date maturityDate;
    private double interestRate;
    private int tenureMonths;

    public FixedDepositAccount(
            String accountNumber,
            double balance,
            Customer owner,
            AccountStatus status,
            Date createdDate,
            Date maturityDate,
            double interestRate,
            int tenureMonths) {

        super(accountNumber, balance, owner, status, createdDate);

        this.maturityDate = maturityDate;
        this.interestRate = interestRate;
        this.tenureMonths = tenureMonths;
    }

    @Override
    public boolean withdraw(double amount) {

        Date today = new Date();

        if (today.before(maturityDate)) {
            throw new RuntimeException("FD is not matured yet");
        }

        if (amount > 0) {
            deductBalance(amount);
            return true;
        }
        return false;
    }

    @Override
    public double calculateInterest() {

        double principal = getBalance();

        // Months → years
        double timeInYears = tenureMonths / 12.0;

        // Assuming annual compounding
        double finalAmount =
                principal * Math.pow(1 + interestRate, timeInYears);

        return finalAmount - principal;
    }
}
//Transaction
enum TransactionStatus{
    SUCCESS,
    FAILED,
    PENDING
}

abstract class Transaction {
    private String transactionId;
    private double amount;
    private Date timestamp;
    private TransactionStatus status;

    public Transaction(String transactionId, double amount, Date timestamp) {
        this.transactionId = transactionId;
        this.amount = amount;
        this.timestamp = timestamp;
        this.status = TransactionStatus.PENDING;
    }

    protected void setStatus(TransactionStatus status){
        this.status=status;
    }

    public abstract boolean execute();

    public double getAmount(){
        return amount;
    }

    public String getDetails() {
        return "Transction ID: "+transactionId+"\n"+
                "Amount: "+amount+"\n"+
                "Timestamp: "+timestamp+"\n"+
                "Status: "+status;
    }
}

//4.2 Concrete transactions

class DepositTransaction extends Transaction{
    private Account targetAccount;

    public DepositTransaction(String transactionId,
                              double amount,
                              Date timestamp,
                              Account targetAccount){
        super(transactionId, amount, timestamp);
        this.targetAccount=targetAccount;
    }
    @Override
    public boolean execute(){
        if(targetAccount.deposit(getAmount())){
          setStatus(TransactionStatus.SUCCESS);
          targetAccount.addTransaction(this);
           return true;
        }else{
            setStatus(TransactionStatus.FAILED);
            targetAccount.addTransaction(this);
        }
        return false;
    }
}

class WithdrawTransaction extends Transaction{
    private Account sourceAccount;

    public  WithdrawTransaction(String transactionId, double amount, Date timestamp,Account sourceAccount){
        super(transactionId, amount, timestamp);
        this.sourceAccount=sourceAccount;
    }
    @Override
    public boolean execute(){
        if(sourceAccount.withdraw(getAmount())){
            setStatus(TransactionStatus.SUCCESS);
            sourceAccount.addTransaction(this);
            return true;
        }else{
            setStatus(TransactionStatus.FAILED);
            sourceAccount.addTransaction(this);
        }
        return false;
    }
}

class  TransferTransaction extends Transaction{
    private  Account sourceAccount;
    private  Account targetAccount;

    public TransferTransaction(String transactionId, double amount, Date timestamp,Account sourceAccount,Account targetAccount){
        super(transactionId, amount, timestamp);
        this.sourceAccount=sourceAccount;
        this.targetAccount=targetAccount;
    }
    @Override
    public boolean execute(){
        if(sourceAccount.withdraw(getAmount())){
            if(targetAccount.deposit(getAmount())){
                setStatus(TransactionStatus.SUCCESS);
                targetAccount.addTransaction(this);
                sourceAccount.addTransaction(this);
                return true;
            }else{
                sourceAccount.deposit(getAmount());
                setStatus(TransactionStatus.FAILED);
                sourceAccount.addTransaction(this);
                targetAccount.addTransaction(this);
            }
        }else{
            setStatus(TransactionStatus.FAILED);
            sourceAccount.addTransaction(this);
        }
        return false;
    }
}

//5. Supporting Design Patterns


//5.2 AccountFactory — Factory Pattern

class AccountFactory {

    public static Account createAccount(Customer customer,AccountType type ) {

        String accountNumber = UUID.randomUUID().toString();
        double openingBalance = 0.0;
        AccountStatus status = AccountStatus.ACTIVE;
        Date createdDate = new Date();

        switch (type) {

            case SAVINGS:
                return new SavingsAccount(
                        accountNumber,
                        openingBalance,
                        customer,
                        status,
                        createdDate,
                        0.04,   // Example interest rate: 4%
                        1000.0  // Example minimum balance
                );

            case CURRENT:
                return new CurrentAccount(
                        accountNumber,
                        openingBalance,
                        customer,
                        status,
                        createdDate,
                        5000.0  // Example overdraft limit
                );

            case FIXED_DEPOSIT:
                int tenureMonths = 12;
                double interestRate = 0.07; // Example: 7%

                Date maturityDate = new Date(
                        System.currentTimeMillis()
                                + tenureMonths * 30L * 24 * 60 * 60 * 1000
                );

                return new FixedDepositAccount(
                        accountNumber,
                        openingBalance,
                        customer,
                        status,
                        createdDate,
                        maturityDate,
                        interestRate,
                        tenureMonths
                );

            default:
                throw new IllegalArgumentException("Invalid account type");
        }
    }
}

//5.1 Bank — Singleton

class Bank{
    private static Bank instance;
    private  Map<String, Customer> customers;
    private Map<String, Account> accounts;

    private Bank(){
        customers = new HashMap<>();
        accounts = new HashMap<>();
        System.out.println("Bank is created");
    }

    public static  Bank getInstance(){
        if(instance==null){
            instance= new Bank();
        }
        return instance;
    }

    public boolean registerCustomer(Customer c){
        if(customers.containsKey(c.getId())){
            System.out.println("User ID already exist");
            return false;
        }else {
            customers.put(c.getId(), c);
            return true;
        }
    }

    public boolean registerAccount(Account a){
        if(accounts.containsKey(a.getAccountNumber())){
            System.out.println("Account number already exist");
            return false;
        }else {
            accounts.put(a.getAccountNumber(),a);
            return true;
        }
    }

    public boolean closeAccount(String accountId){
        Account closeAccount;
        if(accounts.containsKey(accountId)){
            closeAccount=findAccount(accountId);
            if((closeAccount.getStatus()!=AccountStatus.CLOSED) && closeAccount.getBalance()==0){
                closeAccount.setAccountStatus(AccountStatus.CLOSED);
                return true;
            }else{
                return false;
            }
        }
        return false;
    }


    public Account findAccount(String accountNumber){
        return accounts.get(accountNumber);
    }
    public  boolean transferMoney(String fromAccountNumber,String toAccountNumber,double amount){
        Account sourseAccount;
        Account targetAccount;
        String transactionId= UUID.randomUUID().toString();
        Date timestamp= new Date();
        if(findAccount(fromAccountNumber)!=null){
             sourseAccount= findAccount(fromAccountNumber);
        }else {
            System.out.println("Invaild Sourse Account number");
            return false;
        }
        if(findAccount(toAccountNumber)!=null){
            targetAccount= findAccount(toAccountNumber);
        }else {
            System.out.println("Invaild Target Account number");
            return false;
        }
        TransferTransaction t=new TransferTransaction(transactionId,amount,timestamp,sourseAccount,targetAccount);
        return t.execute();
    }

}

public class BankSystem {
    static void main() {

    }
}