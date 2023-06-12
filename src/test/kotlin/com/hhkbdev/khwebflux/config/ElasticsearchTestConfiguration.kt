package com.hhkbdev.khwebflux.config

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchTemplate
import org.springframework.data.elasticsearch.core.RefreshPolicy
import javax.annotation.PostConstruct

@Configuration
class ElasticsearchTestConfiguration {
    @Autowired
    private lateinit var template: ReactiveElasticsearchTemplate

    @PostConstruct
    fun configureTemplate() {
        this.template.setRefreshPolicy(RefreshPolicy.IMMEDIATE)
    }
}
