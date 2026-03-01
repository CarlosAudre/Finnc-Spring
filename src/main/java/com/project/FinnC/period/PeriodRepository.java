package com.project.FinnC.period;

import com.project.FinnC.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Month;
import java.util.Optional;

@Repository
public interface PeriodRepository extends JpaRepository<Period, Long> {
    Optional<Period> findByUserAndMonthAndYear(User user, Month month, int year);
}
