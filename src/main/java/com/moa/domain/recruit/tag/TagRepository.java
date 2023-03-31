package com.moa.domain.recruit.tag;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface TagRepository extends JpaRepository<Tag, Long> {
    @Query("select t.name from Tag t where t.name in :names")
    List<String> findExistName(@Param(value = "names") Collection<String> tags);

    @Query("select t.id from Tag t where t.name in :names")
    List<Long> findIdByName(@Param(value = "names") Collection<String> tags);

    @Query("select t from Tag t where t.name in :names")
    List<Tag> findAllByName(@Param(value = "names") Collection<String> tags);
}
