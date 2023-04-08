package com.moa.domain.user;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Optional<User> findByRefreshToken(String refreshToken);

    @EntityGraph(attributePaths = {"recruitmentInterests", "recruitmentInterests.recruitment",
            "recruitmentInterests.recruitment.tags", "recruitmentInterests.recruitment.tags.tag"})
    Optional<User> findRecruitmentInterestById(Long userId);
}
