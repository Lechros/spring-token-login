package com.lechros.springtokenlogin.provider;

public interface ClientSecretGenerator {

    String getRegistrationId();

    String generate();
}
