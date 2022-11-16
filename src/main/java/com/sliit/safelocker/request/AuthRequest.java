package com.sliit.safelocker.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthRequest {

    private static final long serialVersionUID = 5926468583005150707L;

    private String userID;
    private String password;



    public AuthRequest(){

    }
    public AuthRequest(String userID, String password){
        this.userID = userID;
        this.password=password;

    }
}
