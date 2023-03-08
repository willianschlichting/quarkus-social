package io.github.williansch.quarkussocial.domain.repository;

import javax.enterprise.context.ApplicationScoped;

import io.github.williansch.quarkussocial.domain.model.Post;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class PostRepository implements PanacheRepository<Post> {
    
}
