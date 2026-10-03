package Banking_System;
import java.security.PublicKey;
import java.util.*;


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
enum TransactionStatus{
    SUCCESS,
    FAILED,
    PENDING
}

//Transaction

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

    public String getDetails() {
        return "Transction ID: "+transactionId+"\n"+
                "Amount: "+amount+"\n"+
                "Timestamp: "+timestamp+"\n"+
                "Status: "+status;
    }
}

//Account (abstract class)
abstract class Account{
    protected String accountNumber;
    private double balance;
    protected  Customer owner;
    protected AccountStatus status;
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

    public void deposit(double amount){
        if(amount>0) balance+=amount;
    }

    public abstract void withdraw(double amount);

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
    public void withdraw(double amount){
        if(amount>0 && getBalance()-amount>=minBalance ){
            deductBalance(amount);
        }
    }
    public double calculateInterest(){
        return getBalance()*interestRate;
    }
}

//CurrentAccount extends Account
class CurrentAccount extends Account{
    private double overdraftLimit;

    public  CurrentAccount(String accountNumber,
                           double balance,
                           Customer owner,
                           AccountStatus status,
                           Date createdDate,
                           double overdraftLimt){

        super(accountNumber, balance, owner, status, createdDate);
        this.overdraftLimit=overdraftLimt;
    }
    public void withdraw(double amount){
        if(amount>0 && getBalance()-amount>=(-overdraftLimit)){
            deductBalance(amount);
        }
    }
    public double calculateInterest(){
    class CurrentAccount extends Account{
    private double overdraftLimit;

    public  CurrentAccount(String accountNumber,
                           double balance,
                           Customer owner,
                           AccountStatus status,
                           Date createdDate,
                           double overdraftLimt){

        super(accountNumber, balance, owner, status, createdDate);
        this.overdraftLimit=overdraftLimt;
    }
    public void withdraw(double amount){
        if(amount>0 && getBalance()-amount>=(-overdraftLimit)){
            deductBalance(amount);
        }
    }
    public double calculateInterest(){
        return 0;
    }
}    return 0;
    }
}

public class Bank {

}