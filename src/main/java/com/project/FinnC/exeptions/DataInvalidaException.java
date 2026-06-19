package com.project.FinnC.exeptions;

public class DataInvalidaException extends RuntimeException {
    public DataInvalidaException(){
        super("Data inválida");
    }

    public DataInvalidaException(String message){
        super(message);
    }
}
