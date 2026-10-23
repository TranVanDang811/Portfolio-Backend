package com.portfolioBackend.backend.AboutMe.repository;

import com.portfolioBackend.backend.AboutMe.entity.AboutMe;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AboutMeRepository extends JpaRepository<AboutMe, String> {
}
