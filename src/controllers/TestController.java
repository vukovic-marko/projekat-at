package controllers;

import javax.ejb.EJB;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import jdk.nashorn.internal.objects.annotations.Getter;
import model.TestI;

@Path("/test")
public class TestController {
    /*
    @EJB
    TestI test;*/

    @GET
    @Produces("application/json")
    public String greeting(){
        return "Pera";
    }

}
