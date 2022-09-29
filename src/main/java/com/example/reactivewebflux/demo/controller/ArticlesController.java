package com.example.reactivewebflux.demo.controller;

import com.example.reactivewebflux.demo.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/article")
/**
 * Controller to get the Articles after Successful OAUTH2 Authentication.
 */
public class ArticlesController {

    @Autowired
    ArticleService customerService;

    @GetMapping("/getArticles")
    public Mono<String> getArticles() {
        return customerService.getArticles();
    }

}
