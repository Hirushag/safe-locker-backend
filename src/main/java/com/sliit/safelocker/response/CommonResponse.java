package com.sliit.safelocker.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommonResponse<T>  {
    private T dataBundle;
    private Boolean isSuccess;
    private String errorMessage;
    private String successMessage;


    public CommonResponse(Boolean isSuccess, String errorMessage, T dataBundle) {
        this.isSuccess =isSuccess;
        this.dataBundle = dataBundle;
        this.errorMessage = errorMessage;

    }
}
