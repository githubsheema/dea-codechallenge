package com.mycompany.ipv4manager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mycompany.ipv4manager.model.IPv4Node;

/**
 * 
 * @author mathi
 * Repository to manage IPv4Node
 *
 */

@Repository
public interface IPv4AMNodRepository extends JpaRepository<IPv4Node, Integer>{

}