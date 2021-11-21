package com.vinayak.minireddit.repository;

import com.vinayak.minireddit.model.Post;
import com.vinayak.minireddit.model.Subreddit;
import com.vinayak.minireddit.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post,Long> {

    List<Post> findAllBySubreddit(Subreddit subreddit);

    List<Post> findByUser(User user);
}
