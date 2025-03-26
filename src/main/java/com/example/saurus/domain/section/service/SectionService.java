package com.example.saurus.domain.section.service;

import com.example.saurus.domain.section.dto.request.SectionCreateRequest;
import com.example.saurus.domain.section.dto.request.SectionUpdateRequest;
import com.example.saurus.domain.section.dto.response.SectionResponse;

import java.util.List;

public interface SectionService {

    SectionResponse createSection(Long gameId, SectionCreateRequest request);

    SectionResponse updateSection(Long sectionId, SectionUpdateRequest request);

    void deleteSection(Long sectionId);

    List<SectionResponse> getSectionsByGameId(Long gameId);

    SectionResponse getSection(Long sectionId);
}