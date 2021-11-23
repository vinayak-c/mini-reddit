package com.vinayak.minireddit.service;

import com.vinayak.minireddit.dto.CommentsDto;
import com.vinayak.minireddit.exceptions.PostNotFoundException;
import com.vinayak.minireddit.mapper.CommentMapper;
import com.vinayak.minireddit.model.Comment;
import com.vinayak.minireddit.model.NotificationEmail;
import com.vinayak.minireddit.model.Post;
import com.vinayak.minireddit.model.User;
import com.vinayak.minireddit.repository.CommentRepository;
import com.vinayak.minireddit.repository.PostRepository;
import com.vinayak.minireddit.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class CommentService {

    private final CommentMapper commentMapper;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final AuthService authService;
    private final MailContentBuilder mailContentBuilder;
    private final MailService mailService;

    public void createComment(CommentsDto commentsDto) {
        Post post = postRepository.findById(commentsDto.getPostId())
                .orElseThrow(() -> new PostNotFoundException(commentsDto.getPostId().toString()));
        Comment comment = commentMapper.map(commentsDto, post, authService.getCurrentUser());
        commentRepository.save(comment);

        String message = mailContentBuilder.build(comment.getUser().getUserName() + " posted a comment on your post.");
        sendCommentNotification(message, comment.getUser());
    }

    public List<CommentsDto> getCommentByPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId.toString()));
        return commentRepository.findByPost(post)
                .stream()
                .map(commentMapper::mapToDto)
                .collect(Collectors.toList());
    }

    public List<CommentsDto> getCommentsByUser(String userName) {
        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new UsernameNotFoundException(userName));
        return commentRepository.findAllByUser(user)
                .stream()
                .map(commentMapper::mapToDto)
                .collect(Collectors.toList());
    }

    private void sendCommentNotification(String message, User user) {
        mailService.sendEmail(new NotificationEmail(user.getUserName() + " Commented on your post", user.getEmail(), message));
    }
}
