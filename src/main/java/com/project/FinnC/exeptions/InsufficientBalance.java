package com.project.FinnC.exeptions;

public class InsufficientBalance extends RuntimeException {
    public InsufficientBalance(){
        super("Saldo insuficiente");
    }
    public InsufficientBalance(String message){
        super(message);
    }
}
