package com.project.FinnC.expense;
import com.project.FinnC.container.ContainerPeriod;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(
        name = "expense_container",
        uniqueConstraints = @UniqueConstraint(columnNames = {
                "expense_fk", "container_period_fk"
        })
)
public class ExpenseContainer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private BigDecimal value;

    @ManyToOne
    @JoinColumn(name = "container_period_fk", nullable = false)
    private ContainerPeriod containerPeriod;

    @ManyToOne
    @JoinColumn(name = "expense_fk", nullable = false)
    private Expense expense;

}
