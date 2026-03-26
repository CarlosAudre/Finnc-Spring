package com.project.FinnC.container;

import com.project.FinnC.expense.Expense;
import com.project.FinnC.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
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

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContainerColor color = ContainerColor.PURPLE;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "container", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true) //With CascadeType.All, when deleted, containerPeriod is also deleted
    private List<ContainerPeriod> containerPeriods;

    @OneToMany(mappedBy = "container", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Expense> expenses;
}
