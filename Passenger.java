
package com.pickme.pickmeapp;

import java.util.ArrayList;
import java.util.List;

public class Passenger extends User {
    private List<Route> routes;
    

    public Passenger(String name, String email) {
        super(name, email);
        this.routes = new ArrayList<>();
       
    }

}