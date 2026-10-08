package kh.edu.istad.common.domain.valueobject;

import java.math.BigDecimal;

public record Money(
        BigDecimal amount
) {
   public void isGreaterThanZero(){
       if (!(amount.compareTo(BigDecimal.ZERO)> 0)){
           System.out.println("Money is not greater than zero");
           throw new RuntimeException("Money is not greater than zero");
       }
   }
}
