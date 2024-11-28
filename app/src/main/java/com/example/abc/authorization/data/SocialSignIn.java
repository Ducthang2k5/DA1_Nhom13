package com.example.abc.authorization.data;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;

public class SocialSignIn {
    BeginSignInRequest googleSignInRequest;

    public void signInGoogle() {
        googleSignInRequest = BeginSignInRequest.builder()
                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        .setServerClientId("167817210856-r8u744ks2cbf4tb295h5mmk97vvvuoj8.apps.googleusercontent.com")
                        .setFilterByAuthorizedAccounts(true)
                        .build())
                .build();
    }
}
