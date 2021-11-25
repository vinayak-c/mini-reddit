package com.vinayak.minireddit.mapper;

import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.vinayak.minireddit.dto.PostRequest;
import com.vinayak.minireddit.dto.PostResponse;
import com.vinayak.minireddit.model.Post;
import com.vinayak.minireddit.model.Subreddit;
import com.vinayak.minireddit.model.User;
import com.vinayak.minireddit.repository.CommentRepository;
import com.vinayak.minireddit.repository.VoteRepository;
import com.vinayak.minireddit.service.AuthService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class PostMapper {

    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private VoteRepository voteRepository;
    @Autowired
    private AuthService authService;

    @Mapping(target = "createdDate", expression = "java(java.time.Instant.now())")
    @Mapping(target = "description", source = "postRequest.description")
    @Mapping(target = "voteCount", constant = "0")
    public abstract Post map(PostRequest postRequest, Subreddit subreddit, User user);

    @Mapping(target = "id", source = "postId")
    @Mapping(target = "subredditName", source = "subreddit.name")
    @Mapping(target = "userName", source = "user.userName")
    @Mapping(target = "commentCount", expression = "java(commentCount(post))")
    @Mapping(target = "duration", expression = "java(getDuration(post))")
    public abstract PostResponse mapToDto(Post post);

    Integer commentCount(Post post) {
        return commentRepository.findByPost(post).size();
    }

    String getDuration(Post post) {
        return TimeAgo.using(post.getCreatedDate().toEpochMilli());
    }
}
