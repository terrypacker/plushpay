package com.plushpay.service.user;

import com.plushpay.repository.user.User;
import com.plushpay.repository.user.UserRepository;
import com.plushpay.service.AbstractPlushPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Terry Packer
 */
@Service
public class UserService extends AbstractPlushPayService<User, Integer, UserRepository> {

    @Autowired
    public UserService(UserRepository repository) {
        super(repository);
    }
}
