package com.AptiTekk.Agenda.core.impl;

import com.AptiTekk.Agenda.core.UserService;
import com.AptiTekk.Agenda.core.entity.QUser;
import com.AptiTekk.Agenda.core.entity.User;
import com.AptiTekk.Agenda.core.utilities.Sha256Helper;
import com.querydsl.jpa.impl.JPAQuery;

import javax.ejb.Stateless;

@Stateless
public class UserServiceImpl extends EntityServiceAbstract<User> implements UserService {

    QUser userTable = QUser.user;

    public UserServiceImpl() {
        super(User.class);
    }

    @Override
    public User findByName(String username) {
        if (username == null) {
            return null;
        }

        return new JPAQuery<User>(entityManager).from(userTable).where(userTable.username.eq(username))
                .fetchOne();
    }

    @Override
    public User correctCredentials(String username, String password) {
        if (username == null || password == null) {
            return null;
        }

        byte[] hashedPassword = Sha256Helper.rawToSha(password);
        return new JPAQuery<User>(entityManager).from(userTable)
                .where(userTable.username.eq(username).and(userTable.password.eq(hashedPassword)))
                .fetchOne();
    }

}
