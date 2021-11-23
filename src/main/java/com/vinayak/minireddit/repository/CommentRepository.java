package com.vinayak.minireddit.repository;

import com.vinayak.minireddit.model.Comment;
import com.vinayak.minireddit.model.Post;
import com.vinayak.minireddit.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment,Long> {

    List<Comment> findByPost(Post post);

    List<Comment> findAllByUser(User user);
}
