package com.project.FinnC.exeptions;

public class ContainerPeriodNotFoundException extends RuntimeException {
    public ContainerPeriodNotFoundException(){
        super("Container não encontrado");
    }

    public ContainerPeriodNotFoundException(String message){
        super(message);
    }
}
