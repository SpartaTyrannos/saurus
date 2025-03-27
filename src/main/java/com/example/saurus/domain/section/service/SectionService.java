package com.example.saurus.domain.section.service;

import com.example.saurus.domain.common.dto.AuthUser;
import com.example.saurus.domain.section.dto.request.SectionCreateRequest;
import com.example.saurus.domain.section.dto.request.SectionUpdateRequest;
import com.example.saurus.domain.section.dto.response.SectionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SectionService {

    SectionResponse createSection(AuthUser authUser, Long gameId, SectionCreateRequest request);

    SectionResponse updateSection(AuthUser authUser, Long gameId, Long sectionId, SectionUpdateRequest request);

    void deleteSection(AuthUser authUser, Long gameId, Long sectionId);

    Page<SectionResponse> getSectionsByGameId(Long gameId, Pageable pageable);

    SectionResponse getSection(Long gameId, Long sectionId);
}