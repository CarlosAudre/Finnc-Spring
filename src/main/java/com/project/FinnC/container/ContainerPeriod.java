package com.project.FinnC.container;

import com.project.FinnC.period.Period;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(
        name = "container_period",
        uniqueConstraints = @UniqueConstraint( columnNames = {
                "container_fk", "period_fk"
        })
)
public class ContainerPeriod {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalValue;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalSpent;

    @ManyToOne
    @JoinColumn(name = "container_fk")
    Container container;

    @ManyToOne
    @JoinColumn(name = "period_fk")
    Period period;
}
