package com.project.FinnC.period;

import com.project.FinnC.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.Month;
import java.time.Year;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(
        name = "period",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"month", "year", "user_id"} //It can't exist multiples periods tables with the same month, year and user_id, so i used @UniqueConstraint
        )
)
@Entity
public class Period {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(precision = 10, scale = 2)
    private BigDecimal value;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Month month;

    @Column(nullable = false)
    private int year;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Period(BigDecimal value, Month month, int year, User user) {
        this.value = value;
        this.month = month;
        this.year = year;
        this.user = user;
    }
}
