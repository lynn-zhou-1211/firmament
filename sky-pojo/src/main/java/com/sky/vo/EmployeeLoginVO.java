package com.sky.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "Employee login response data")
public class EmployeeLoginVO implements Serializable {

    @ApiModelProperty("Employee ID")
    private Long id;

    @ApiModelProperty("Username")
    private String userName;

    @ApiModelProperty("Full name")
    private String name;

    @ApiModelProperty("JWT token")
    private String token;

}
