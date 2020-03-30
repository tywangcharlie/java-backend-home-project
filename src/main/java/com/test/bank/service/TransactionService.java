package com.test.bank.service;

import com.test.bank.initializer.DataSourceInitializer;
import com.test.bank.model.TransferResponse;

import org.jooq.Record1;
import org.jooq.Record2;
import org.jooq.Result;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultConfiguration;
import org.jooq.types.UInteger;

import javax.inject.Inject;
import javax.inject.Singleton;

import static com.test.bank.db.Tables.USER;

@Singleton
public class TransactionService {

    DefaultConfiguration jooqConfiguration;

    @Inject
    public TransactionService(DataSourceInitializer dataSourceInitializer) {
        this.jooqConfiguration = dataSourceInitializer.getJooqConfiguration();
    }

    public TransferResponse transfer(int fromUserId, int toUserId, int amount) {
        // TODO implement transfer

        DSL.using(jooqConfiguration).update(USER).set(USER.WALLET, USER.WALLET.sub(amount)).where(USER.ID.eq(UInteger.valueOf(fromUserId))).execute();
        DSL.using(jooqConfiguration).update(USER).set(USER.WALLET, USER.WALLET.add(amount)).where(USER.ID.eq(UInteger.valueOf(toUserId))).execute();
        //Result<Record2<UInteger, Integer>> Result = DSL.using(jooqConfiguration).select(USER.ID, USER.WALLET).from(USER).fetch();
        
        return null;
    }

    public int CheckWallet(int userid) {
        Record1<Integer> record = DSL.using(jooqConfiguration).select(USER.WALLET).from(USER).where(USER.ID.equal(UInteger.valueOf(userid))).fetchOne();
        int wallet = record.getValue(record.field1());
        return wallet;
    }

}