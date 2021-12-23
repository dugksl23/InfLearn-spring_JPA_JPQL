package jpabasic.hellojpql.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MemberDto {

    private long id;
    private Integer age;

    public MemberDto(long id, Integer age) {

        this.id = id;
        this.age = age;
    }
}
