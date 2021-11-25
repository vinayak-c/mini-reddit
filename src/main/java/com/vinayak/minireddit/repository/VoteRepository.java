package com.vinayak.minireddit.repository;

import com.vinayak.minireddit.model.Post;
import com.vinayak.minireddit.model.User;
import com.vinayak.minireddit.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VoteRepository extends JpaRepository<Vote,Long> {
    Optional<Vote> findTopByPostAndUserOrderByVoteIdDesc(Post post, User currentUser);
}
