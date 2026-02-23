package com.project.FinnC.auth;

import org.springframework.stereotype.Service;

@Service
public class RegisterService {
    public boolean passwordValidation(String password){
        boolean isSpecial = false;
        boolean isLetter = false;
        boolean isDigit = false;
        char[] passwordChar = password.toCharArray();
        if(passwordChar.length < 8){
            return false;
        }
        for(char ch : passwordChar){
            if(Character.isLetter(ch)){
                isLetter = true;
            }
            else if(Character.isDigit(ch)){
                isDigit = true;
            }
            else{
                isSpecial = true;
            }
            if(isLetter && isDigit && isSpecial){
                break;
            }
        }
        return isLetter && isDigit && isSpecial;
    }
}
