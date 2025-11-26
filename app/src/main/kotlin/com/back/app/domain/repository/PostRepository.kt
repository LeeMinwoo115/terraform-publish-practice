package com.back.app.domain.repository

import com.back.app.domain.entity.Post
import org.springframework.data.jpa.repository.JpaRepository

interface PostRepository : JpaRepository<Post, Long>