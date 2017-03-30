package com.hellstudios.weedstore.rest.login;

import com.hellstudios.weedstore.Application;
import com.hellstudios.weedstore.api.APIException;
import com.hellstudios.weedstore.api.account.AccountAPI;
import com.hellstudios.weedstore.api.account.AccountBean;
import com.hellstudios.weedstore.api.auth.AuthAPI;
import com.hellstudios.weedstore.rest.ReplyObject;
import com.hellstudios.weedstore.util.FieldValidation;
import org.apache.log4j.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * @author strel
 */
@Path("auth")
@Produces(MediaType.APPLICATION_JSON)
public class AuthResource {
    /**
     * {@code Slf4j} {@link Logger}.
     */
    private static final Logger log = Logger.getLogger(AuthResource.class);

    private AccountAPI accountAPI;
    private AuthAPI authAPI;


    public AuthResource() {
        try {
            accountAPI = Application.getInstance().getAPI(AccountAPI.class);
            authAPI = Application.getInstance().getAPI(AuthAPI.class);
        }
        catch (APIException ex) {
            log.error("Can't initialize Account API", ex);
        }
    }


    @POST
    @Path("login")
    @Consumes(MediaType.APPLICATION_JSON)
    public ReplyObject login(LoginForm loginForm) {
        if (!validateInputData(loginForm)) {
            return ReplyObject.error("Wrong parameters");
        }

        try {
            AccountBean account = accountAPI.getAccountByEmail(loginForm.getEmail());
            String token = authAPI.authenticate(account);

            return ReplyObject.success("token", token);
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return ReplyObject.error(ex.getMessage());
        }
    }

    private boolean validateInputData(LoginForm form) {
        return FieldValidation.validateEmail(form.getEmail())
                && FieldValidation.validatePassword(form.getPassword());
    }
}
