package com.project.FinnC.exeptions;

public class InvalidPasswordException extends RuntimeException{

    public InvalidPasswordException(){
        super("Senha inválida");
    }
    public InvalidPasswordException(String message){
        super(message);
    }
}
