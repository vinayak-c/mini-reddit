package com.vinayak.minireddit.controller;

import com.vinayak.minireddit.dto.CommentsDto;
import com.vinayak.minireddit.service.CommentService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.ResponseEntity.status;

@RestController
@RequestMapping("/api/comments/")
@AllArgsConstructor
public class CommentsController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<Void> createComment(@RequestBody CommentsDto commentsDto) {
        commentService.createComment(commentsDto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("by-postId/{postId}")
    public ResponseEntity<List<CommentsDto>> getAllCommentsForPost(@PathVariable("postId") Long postId) {
        return status(HttpStatus.OK)
                .body(commentService.getCommentByPost(postId));
    }

    @GetMapping("by-user/{userName}")
    public ResponseEntity<List<CommentsDto>> getAllCommentsByUser(@PathVariable("userName") String userName) {
        return status(HttpStatus.OK).body(commentService.getCommentsByUser(userName));
    }

}
