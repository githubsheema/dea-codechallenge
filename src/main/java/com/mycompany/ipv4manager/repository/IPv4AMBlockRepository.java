package com.mycompany.ipv4manager.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.mycompany.ipv4manager.model.IPv4Block;


/**
 * 
 * @author mathi
 * Repository to manage IPv4Block
 *
 */

@Repository
public interface IPv4AMBlockRepository extends JpaRepository<IPv4Block, Integer>{
	
	@Query ("SELECT b FROM IPv4Block b WHERE (b.block) = ?1")
	public List<IPv4Block> searchBlock(String match);

}