package com.project.FinnC.container;

import com.project.FinnC.period.Period;
import com.project.FinnC.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ContainerPeriodRepository extends JpaRepository<ContainerPeriod, Long> {
    List<ContainerPeriod> findByPeriod(Period period);
    Boolean existsByContainerAndPeriod(Container container, Period period);
    List<ContainerPeriod> findByContainer(Container container);
    Optional<ContainerPeriod> findByContainerAndPeriod(Container container, Period period);
}
