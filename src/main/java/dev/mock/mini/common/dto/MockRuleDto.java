package dev.mock.mini.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MockRuleDto {
    private String id;
    private String method;
    private String path;
    private String headers;
    private String body;
    private int statusCode;
    private int delay;
    private Timestamp created;
}
