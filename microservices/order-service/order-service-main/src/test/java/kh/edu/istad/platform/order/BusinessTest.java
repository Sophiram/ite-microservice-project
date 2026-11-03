package kh.edu.istad.platform.order;

import kh.edu.istad.platform.order.persistence.repository.BusinessReactiveRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class BusinessTest {
    @Autowired
    private BusinessReactiveRepository businessReactiveRepository;
    @Test
    void testFindBusiness(){
        businessReactiveRepository.findAll();
    }
}
