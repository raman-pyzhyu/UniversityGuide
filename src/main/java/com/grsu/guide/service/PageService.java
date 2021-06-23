package com.grsu.guide.service;

import com.grsu.guide.domain.Element;
import com.grsu.guide.domain.Page;
import com.grsu.guide.repository.PageRepository;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

import java.util.List;

@Service
public class PageService {
   private final PageRepository pageRepository;

    public PageService(PageRepository pageRepository) {
        this.pageRepository = pageRepository;
    }

    public Optional<Page> getPage (Long id){
        return pageRepository.findById(id);
    }

    public Iterable<Page> getAllChildPages(Long id) {return  pageRepository.findPagesByParentPageId(id);}

    public Iterable<Page> getAllParentPages(Long id){
        return pageRepository.findPagesByParentPageId(id);
    }

    public void addPage(Page page){

        if(page.getElements()!=null) {
            for (Element newElement : page.getElements()) {
                page.getElements().add(newElement);
            }
            page.setElements(page.getElements());
        }

        pageRepository.save(page);
    }

    public void deletePage(Long id){
        pageRepository.deleteById(getPage(id).get().getId());
    }

    public List<Page> getPagesBySearchRequest(String searchRequest) {
        return pageRepository.findPagesByNamePageContains(searchRequest);
    }

}

