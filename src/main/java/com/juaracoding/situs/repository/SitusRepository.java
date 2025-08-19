package com.juaracoding.situs.repository;


/*
IntelliJ IDEA 2025.1.2 (Ultimate Edition)
Build #IU-251.26094.121, built on June 3, 2025
@Author lenovo Achmadi Suryo Utomo
Java Developer
Created on 14/08/2025 16:08
@Last Modified 14/08/2025 16:08
Version 1.0
*/

import com.juaracoding.situs.model.Situs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SitusRepository extends JpaRepository<Situs, Long> {

}


