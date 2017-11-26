package com.leon.processor.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AggregateKey implements Serializable {

    private String routeId;

    private String vehicleType;

}
