package com.example.accounts.dto;

import com.example.accounts.config.AccountsConfigEnv;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;

@Data
public class RequestTokenDTO {
    String grant_type = "client_credentials";
    String client_id;
    String client_secret;

    public RequestTokenDTO(String clientId, String clientSecret) {
        this.client_id = clientId;
        this.client_secret = clientSecret;
    }
}
