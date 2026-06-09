package com.mba.saasapp.services;

import com.mba.saasapp.entities.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProvisionningService   {

    void provisionTenant(final Tenant tenant) ;

  //  void provisionTenant(final Tenant tenant, String plainPassword);





}
