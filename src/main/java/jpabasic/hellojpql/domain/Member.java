package jpabasic.hellojpql.domain;


import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Setter
@ToString
@NamedQuery(name = "Member.findByTeam",
        query = "select m from Member m where m.team = :team")
public class Member {
    @Id
    @GeneratedValue
    @Column(name = "member_id", nullable = false)
    private Long id;
    private String name;
    private Integer age;

    @Enumerated(EnumType.STRING)
    private MemberType memberType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    @OneToMany
    @JoinColumn(name = "member_id")
    private List<Order> orders;


    @Builder
    public Member(String name, Integer age) {
        this.name = name;
        this.age = age;
    }

    public void entryTeam(Team team) {
        this.team = team;
        team.getMembers().add(this);
    }


}
