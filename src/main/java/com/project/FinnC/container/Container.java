package com.project.FinnC.container;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "container")
public class Container {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    @Column(nullable = false)
    private LocalDate start_date;
    private LocalDate end_date;

    @OneToMany(mappedBy = "container", cascade = CascadeType.ALL)
    private List<ContainerPeriod> containerPeriods;

}
