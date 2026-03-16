package com.project.FinnC.container;

import com.project.FinnC.period.Period;
import com.project.FinnC.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ContainerRepository extends JpaRepository<Container, Long> {
    List<Container> findAllByUser(User user);

    @Query("""
            SELECT c FROM Container c
            WHERE c.user = :user
            AND c.startDate <= :periodDate
            AND (c.endDate >= :periodDate OR c.endDate is NULL)
            """)
    List<Container> findActiveContainers(User user, LocalDate periodDate);
}
