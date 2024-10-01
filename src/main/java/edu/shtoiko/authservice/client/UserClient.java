package edu.shtoiko.authservice.client;

import edu.shtoiko.authservice.model.SecuredUser;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "USERSERVICE")
public interface UserClient {

    @PostMapping("/user/auth/")
    SecuredUser getSecuredUserByEmail(@RequestBody String email);
}
