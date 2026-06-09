package com.mba.saasapp.auth.service;

import com.mba.saasapp.auth.Request.LoginRequest;
import com.mba.saasapp.auth.Response.LoginResponse;

public interface AuthenticationService {


     LoginResponse login (final LoginRequest request ) ;
}
