package com.example.cicsgenapp.ui.views;

import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.PageTitle;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.cicsgenapp.service.CustomerService;

/**
 * CustomerEditView is a thin wrapper that routes to CustomerCreateEditView for edit mode.
 *
 * <p>Vaadin doesn't allow @Repeatable on @Route, so we use this separate view class
 * to handle the /customers/:id/edit route while delegating to the shared form.
 */
@Route(value = "/customers/:id/edit", layout = com.example.cicsgenapp.ui.layouts.MainLayout.class)
@PageTitle("Edit Customer - CICS GenApp")
public class CustomerEditView extends CustomerCreateEditView {

    @Autowired
    public CustomerEditView(CustomerService customerService) {
        super(customerService);
    }
}
