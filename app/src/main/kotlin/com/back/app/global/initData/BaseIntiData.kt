package com.back.app.global.initData

import com.back.app.domain.service.PostService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.ApplicationRunner
import org.springframework.cache.annotation.Cacheable
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import org.springframework.transaction.annotation.Transactional

@Configuration
class BaseInitData(
    private val postService: PostService
) {
    @Autowired
    @Lazy
    private lateinit var self: BaseInitData

    @Bean
    fun baseInitDataApplicationRunner(): ApplicationRunner {
        return ApplicationRunner {
            self.work1()
        }
    }

    @Transactional
    fun work1() {
        if ( postService.count() > 0 ) return

        postService.write("제목 1", "내용 1")
        postService.write("제목 2", "내용 2")

        self.get1Plus1()
        self.get1Plus1()
        self.get1Plus1()

    }

    @Cacheable("get1Plus1")
    fun get1Plus1(): Int {
        println("get1Plus1 run!")
        return 1 + 1
    }
}
