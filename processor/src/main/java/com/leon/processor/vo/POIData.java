package com.leon.processor.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class POIData implements Serializable {

    private double latitude;

    private double longitude;

    private double radius;

}
