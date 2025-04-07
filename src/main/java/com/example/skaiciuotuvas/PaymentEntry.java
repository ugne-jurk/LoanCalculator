package com.example.skaiciuotuvas;

public class PaymentEntry {
    private final int month;
    private final String payment;
    private final String principal;
    private final String interest;
    private final String balance;

    public PaymentEntry(int month, String payment, String principal, String interest, String balance) {
        this.month = month;
        this.payment = payment;
        this.principal = principal;
        this.interest = interest;
        this.balance = balance;
    }

    public int getMonth() {
        return month;
    }

    public String getPayment() {
        return payment;
    }

    public String getPrincipal() {
        return principal;
    }

    public String getInterest() {
        return interest;
    }

    public String getBalance() {
        return balance;
    }
}