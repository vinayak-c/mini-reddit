package com.vinayak.minireddit.mapper;

import com.vinayak.minireddit.dto.CommentsDto;
import com.vinayak.minireddit.model.Comment;
import com.vinayak.minireddit.model.Post;
import com.vinayak.minireddit.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "text", source = "commentsDto.text")
    @Mapping(target = "createdDate", expression = "java(java.time.Instant.now())")
    @Mapping(target = "post", source = "post")
    Comment map(CommentsDto commentsDto, Post post, User user);

    @Mapping(target = "postId", expression = "java(comment.getPost().getPostId())")
    @Mapping(target = "userName", expression = "java(comment.getUser().getUserName())")
    CommentsDto mapToDto(Comment comment);
}
