package com.test.bank.service;

import com.test.bank.initializer.DataSourceInitializer;
import com.test.bank.model.AdminUser;
import com.test.bank.tool.PasswordUtils;

import org.jooq.Record1;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultConfiguration;
import org.jooq.types.UInteger;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.security.SecureRandom;
import java.util.Base64;

import static com.test.bank.db.Tables.ADMIN;
import static com.test.bank.db.Tables.TOKEN;

@Singleton
public class AdminService {

    DefaultConfiguration jooqConfiguration;

    @Inject
    public AdminService(DataSourceInitializer dataSourceInitializer) {
        this.jooqConfiguration = dataSourceInitializer.getJooqConfiguration();
    }

    public String login(String account, String password) {
        String token = null;
        AdminUser adminUser = DSL.using(jooqConfiguration).fetchOne(ADMIN, ADMIN.ACCOUNT.eq(account)).into(AdminUser.class);
        
        if (PasswordUtils.verifyUserPassword(password, adminUser.getPassword(), adminUser.getSalt())) {
            token = generateToken();
            DSL.using(jooqConfiguration).insertInto(TOKEN, TOKEN.ADMINID, TOKEN.TOKEN_)
                    .values(UInteger.valueOf(adminUser.getId()), token)
                    .onDuplicateKeyUpdate()
                    .set(TOKEN.TOKEN_, token)
                    .execute();
        }
        Record1<String> record = DSL.using(jooqConfiguration).select(TOKEN.TOKEN_).from(TOKEN).where(TOKEN.TOKEN_.eq(token)).fetchOne();
        String databaseToken = record.getValue(record.field1());
        //String databaseToken = DSL.using(jooqConfiguration).select(TOKEN.TOKEN_).from(TOKEN).where(TOKEN.ADMINID.eq(UInteger.valueOf(adminUser.getId()))).fetch().getValue(0, TOKEN.TOKEN_);
        //String databaseToken = DSL.using(jooqConfiguration).select(TOKEN.TOKEN_).from(TOKEN).where(TOKEN.TOKEN_.eq(token)).fetch().getValue(0, TOKEN.TOKEN_);
        return token+"||"+databaseToken;
    }

    public boolean authenticate(String token) {
        // TODO implement authenticate
        Boolean authStatus = false;
        Record1<String> record = DSL.using(jooqConfiguration).select(TOKEN.TOKEN_).from(TOKEN).where(TOKEN.TOKEN_.equal(token)).fetchOne();
        if(record != null) {
            authStatus = true;
        }
        
        return authStatus;
        
    }

    private String generateToken() {
        SecureRandom random = new SecureRandom();
        byte bytes[] = new byte[20];
        random.nextBytes(bytes);
        Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();
        return encoder.encodeToString(bytes);
    }

}
