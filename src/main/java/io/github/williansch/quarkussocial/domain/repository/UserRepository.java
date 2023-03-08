package io.github.williansch.quarkussocial.domain.repository;

import javax.enterprise.context.ApplicationScoped;

import io.github.williansch.quarkussocial.domain.model.User;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class UserRepository implements PanacheRepository<User> {
    
}
