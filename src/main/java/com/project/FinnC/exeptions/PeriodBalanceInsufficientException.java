package com.project.FinnC.exeptions;

public class PeriodBalanceInsufficientException extends RuntimeException {
    public PeriodBalanceInsufficientException(){
        super("Saldo insuficiente");
    }
    public PeriodBalanceInsufficientException(String message){
        super(message);
    }
}
