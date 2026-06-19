package com.project.FinnC.exeptions;

public class PeriodNotFoundException extends RuntimeException {
    public PeriodNotFoundException(){
        super("Period not found");
    }

    public PeriodNotFoundException(String message){
        super(message);
    }
}
