package com.test.bank.resource;

import com.test.bank.model.TransferRequest;
import com.test.bank.model.TransferResponse;
import com.test.bank.service.AdminService;
import com.test.bank.service.TransactionService;
import com.test.bank.tool.config.EnvConfigManager;

import org.jboss.logging.Param;
import org.jooq.Record1;
import org.jooq.Record2;
import org.jooq.Result;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultConfiguration;
import org.jooq.types.UInteger;
import static com.test.bank.db.Tables.USER;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("")
@Produces(MediaType.APPLICATION_JSON)
public class TransactionResource extends BaseResource {

    TransactionService transactionService;
    AdminService adminService;

    DefaultConfiguration jooqConfiguration;

    @Inject
    public TransactionResource(EnvConfigManager envConfigManager,
                               TransactionService transactionService,
                               AdminService adminService) {
        super(envConfigManager);
        this.transactionService = transactionService;
        this.adminService = adminService;
    }


    @POST
    @Path("/transfer")
    public Response transfer(
            @NotNull @HeaderParam("Token") String token,
            @NotNull @Valid TransferRequest transferRequest) {
        boolean auth = adminService.authenticate(token);
        if (!auth) {
            return Response.ok().entity(token).build();
            //return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        TransferResponse transferResponse = transactionService.transfer(transferRequest.getFromUserId(), transferRequest.getToUserId(), transferRequest.getAmount());
        //Result<Record2<UInteger, Integer>> Result = DSL.using(jooqConfiguration).select(USER.ID, USER.WALLET).from(USER).fetch();
        
        //return Response.ok().entity(Result).build();
        return Response.ok(transferResponse).build();
    }

    @POST
    @Path("/get")
    public Response getWallet(@HeaderParam("Userid") int userid) {
        //Record1<Integer> record = DSL.using(jooqConfiguration).select(USER.WALLET).from(USER).where(USER.ID.equal(UInteger.valueOf(userid))).fetchOne();
        //int wallet = record.getValue(record.field1());
        int wallet = transactionService.CheckWallet(userid);
        return Response.ok().entity(wallet).build();
    }


}
