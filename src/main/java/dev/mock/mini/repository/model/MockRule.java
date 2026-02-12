package dev.mock.mini.repository.model;

import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"id", "created"})
public class MockRule {

    private String id;
    private String method;
    private String path;
    private String headers;
    private String body;
    private Integer statusCode;
    private Integer delay;
    private Timestamp created;
}
