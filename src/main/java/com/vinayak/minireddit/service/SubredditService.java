package com.vinayak.minireddit.service;

import com.vinayak.minireddit.dto.SubredditDto;
import com.vinayak.minireddit.exceptions.SpringRedditException;
import com.vinayak.minireddit.mapper.SubredditMapper;
import com.vinayak.minireddit.model.Subreddit;
import com.vinayak.minireddit.repository.SubredditRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class SubredditService {


    private final SubredditRepository subredditRepository;
    private final SubredditMapper subredditMapper;

    @Transactional
    public SubredditDto save(SubredditDto subredditDto){
        Subreddit save=subredditRepository.save(subredditMapper.mapDtoToSubreddit(subredditDto));
        subredditDto.setId(save.getId());
        return subredditDto;
    }

    @Transactional(readOnly = true)
    public List<SubredditDto> getAll() {
          return subredditRepository.findAll()
                  .stream()
                  .map(subredditMapper::mapSubredditToDto)
                  .collect(Collectors.toList());
    }

    public SubredditDto getSubreddit(Long id) {
        Subreddit subreddit = subredditRepository.findById(id)
                .orElseThrow(() -> new SpringRedditException("No subreddit found with ID - " + id));
        return subredditMapper.mapSubredditToDto(subreddit);
    }

//    private SubredditDto mapToDto(Subreddit subreddit) {
//        return SubredditDto.builder().name(subreddit.getName())
//                .description(subreddit.getDescription())
//                .id(subreddit.getId())
//                .numberOfPosts(subreddit.getPosts().size())
//                .build();
//    }

//    private Subreddit mapSubredditDto(SubredditDto subredditDto) {
//        return Subreddit.builder().name(subredditDto.getName())
//                .description(subredditDto.getDescription())
//                .build();
//    }


}
