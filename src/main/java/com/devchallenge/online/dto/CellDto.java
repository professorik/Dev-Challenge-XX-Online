package com.devchallenge.online.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CellDto {
    private String value;
    private String result;

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("{");
        sb.append("\"value\": ").append('\"').append(value).append('\"');
        sb.append(", \"result\": ").append('\"').append(result).append('\"');
        sb.append('}');
        return sb.toString();
    }
}
