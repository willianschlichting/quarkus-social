package io.github.williansch.quarkussocial.domain.repository;

import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;

import io.github.williansch.quarkussocial.domain.model.Follower;
import io.github.williansch.quarkussocial.domain.model.User;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Parameters;

@ApplicationScoped
public class FollowerRepository implements PanacheRepository<Follower> {

    public boolean followers(User follower, User user) {
        PanacheQuery<Follower> query = find("follower = :follower and user = :user", Parameters.with("follower", follower).and("user", user).map());
        Optional<Follower> result = query.firstResultOptional();
        return result.isPresent();
    }

    public List<Follower> findByUser(Long userId) {
        return find("user.id", userId).list();
    }

    public void deleteByFollowerAndUser(Long followerId, Long userId) {
        long l = delete("follower.id = :followerId and user.id  = :userId", Parameters.with("followerId", followerId).and("userId", userId).map());
        System.out.println(l);
    }
    
}
