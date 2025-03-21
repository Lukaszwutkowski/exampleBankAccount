package com.richbank.userfront.domain;

import javax.persistence.*;

@Entity
@Table(name = "sequence_generator")
public class SequenceGenerator {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    private int seqValue;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLastValue() {
        return seqValue;
    }

    public void setLastValue(int lastValue) {
        this.seqValue = lastValue;
    }
}
