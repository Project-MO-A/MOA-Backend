package com.moa.domain.recruit.category;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Query("select c.name from Category c where c.name in :names")
    List<String> findExistName(@Param(value = "names") List<String> categories);

    @Query("select c.id from Category c where c.name in :names")
    List<Long> findIdByName(@Param(value = "names") List<String> categories);
}
