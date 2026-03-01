package com.project.FinnC.container;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContainerPeriodRepository extends JpaRepository<ContainerPeriod, Long> {

}
