package com.ecommerce.library.repository;

import com.ecommerce.library.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category,Long> {

    @Query(value = "select * from categories where is_activated = true", nativeQuery = true)
    List<Category> findAllByActivatedTrue();
}
