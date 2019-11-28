package project.database.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlayerModel {
    @Column(name = "name")
    private String name;

    @Column(name = "score")
    private Integer score;
}
