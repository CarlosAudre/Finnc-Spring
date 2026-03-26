package com.project.FinnC.exeptions;

public class InsufficientBalanceException extends RuntimeException {
    public InsufficientBalanceException(){
        super("Saldo insuficiente");
    }
    public InsufficientBalanceException(String message){
        super(message);
    }
}
