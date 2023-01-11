package com.mycompany.ipv4manager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mycompany.ipv4manager.model.Post;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer>{

}
