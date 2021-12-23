package jpabasic.hellojpql.domain;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Member {
    @Id @GeneratedValue
    @Column(name = "member_id", nullable = false)
    private Long id;
    private String name;

    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;

    @OneToMany
    @JoinColumn(name = "member_id")
    private List<Order> orders ;


    @Builder
    public Member(String name) {
        this.name = name;
    }

    public void entryTeam(Team team){
        this.team = team;
        team.getMembers().add(this);
    }


}
