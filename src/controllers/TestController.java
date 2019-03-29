package controllers;

import model.Korisnik;

import javax.ejb.EJB;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/test")
public class TestController {
    /*
    @EJB
    TestI test;*/

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    // Moze i
    //@Produces("application/json")
    public Korisnik greeting(){
        Korisnik k = new Korisnik("Pera");
        return k;
    }

}
