package com.logistics.company.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "offices")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = { "employees", "company" })
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Office {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "office_id")
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "address", nullable = false)
    private String address;

    @OneToMany(mappedBy = "office")
    @Builder.Default
    private Set<Employee> employees = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;
}
