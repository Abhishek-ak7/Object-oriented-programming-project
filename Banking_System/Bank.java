package Banking_System;

import java.util.*;

abstract class Person{
    private String id;
    private String name;
    private String address;
    private String phone;
    private String email;

    public Person(String id,String name,String address,String phone,String email){
        this.id=id;
        this.name=name;
        this.address=address;
        this.phone=phone;
        this.email=email;
    }
    public String getId(){
        return id;
    }
    public String getName(){
        return name;
    }
    public String getContactInfo(){
        return "Phone number: "+phone+"email: "+email;
    }
}

class Customer extends Person{
    private List<Account> accounts;
    private boolean kycVerified;

    public Customer(String id,String name,String address,String phone,String email,List<Account> accounts,boolean kycVerified){
        super(id, name, address, phone, email);
        this.accounts=accounts;
        this.kycVerified=kycVerified;
    }

    public void addAccount(Account account){
        accounts.add(account);
    }
    public void removeAccount(String accountId) {

        for (Account account : accounts) {

            if (account.getAccountNumber().equals(accountId)) {
                accounts.remove(account);
                break;
            }
        }
    }
    public List<Account> getAccounts(){
        return accounts;
    }
    public double getTotalBalance(){
        double totalBalance=0;
        for(Account account : accounts){
            totalBalance+=account.getBalance();
        }
        return totalBalance;
    }
}
abstract class Account{
    protected String accountNumber;
    protected double balance;
    protected Customer owner;
    protected AccountStatus status;
    protected List<Transaction> transactions;
    protected Date createdDate;

    public Account(String accountNumber,double balance,Customer owner){
        this.accountNumber=accountNumber;
        this.balance=balance;
        this.owner=owner;
        this.status=AccountStatus.ACTIVE;
        this.transactions=new ArrayList<>();
        this.createdDate=new Date();
    }

    public String getAccountNumber() {
        return accountNumber;
    }
    public double getBalance(){
        return balance;
    }
    public abstract void withdraw(double amount);
    public abstract double calculateInterest();
}
enum AccountStatus {
    ACTIVE,
    CLOSED,
    FROZEN
}

class SavingsAccount extends Account{
    private double interestRate;
    private double minBalance;

    public SavingsAccount(String accountNumber,double balance,Customer owner,double interstRate,double minBalance){
        super(accountNumber,balance,owner);
        this.interestRate=interstRate;
        this.minBalance=minBalance;
    }
    @Override
    public void withdraw(double amount){
        if(balance-amount>=minBalance){
            balance-=amount;
        }
    }
    @Override
    public double calculateInterest(){
        return balance*interestRate;
    }
}

abstract class Transaction{
    private String transactionId;
    private double amount;
    private Date timestamp;
    private TransactionStatus status;

    public Transaction(String transactionId,double amount){
        this.transactionId=transactionId;
        this.amount=amount;
        this.timestamp = new Date();
        this.status=TransactionStatus.PENDING;
    }
    public abstract boolean execute();
    public String getDetails(){
        return "Transaction ID: " + transactionId
                + ", Amount: " + amount
                + ", Status: " + status;
    }
}
enum TransactionStatus{
    SUCCESS,
    FAILED,
    PENDING
}

public class Bank {
    public static void main(String[] args){

    }
}
