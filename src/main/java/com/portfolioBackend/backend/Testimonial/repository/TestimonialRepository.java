package com.portfolioBackend.backend.Testimonial.repository;

import com.portfolioBackend.backend.Testimonial.entity.Testimonial;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TestimonialRepository extends JpaRepository<Testimonial, String> {
    List<Testimonial> findAllByOrderByDisplayOrderAsc();
}
