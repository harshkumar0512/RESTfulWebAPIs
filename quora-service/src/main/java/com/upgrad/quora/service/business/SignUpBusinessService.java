package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.Userdao;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SignUpBusinessService {

    @Autowired
    private Userdao userdao;

    @Autowired
    private PasswordCryptographyProvider passwordCryptographyProvider;

    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity signUp(UserEntity userEntity) throws SignUpRestrictedException {
        if (userdao.getUserByUserName(userEntity.getUserName()) != null) {
            throw new SignUpRestrictedException("SGR-001","Try any other Username, this Username has already been taken");
        }

        if (userdao.getUserByEmail(userEntity.getEmail()) != null) {
            throw new SignUpRestrictedException("SGR-002","This user has already been registered, try with any other emailId");
        }

        String[] encryptedPasswordArray = passwordCryptographyProvider.encrypt(userEntity.getPassword());
        userEntity.setSalt(encryptedPasswordArray[0]);
        userEntity.setPassword(encryptedPasswordArray[1]);

        return userdao.createUser(userEntity);
    }
}
