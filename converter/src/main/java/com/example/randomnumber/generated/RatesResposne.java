package com.example.randomnumber.generated;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import com.example.randomnumber.generated.*;

import java.math.BigDecimal;
import java.util.Map;

@Getter
@Setter
@Accessors(chain = true)
public class RatesResposne {

  private Currency base;
  private Map<String, BigDecimal> rates;
}