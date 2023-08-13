package by.nortin.restjwtproject.mapper;

import by.nortin.restjwtproject.domain.BaseDomain;
import by.nortin.restjwtproject.dto.BaseDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BaseMapperTest {

    @Autowired
    private BaseMapper baseMapper;

    private final BaseDto baseDto;
    private final BaseDomain baseDomain;

    {
        Long id = 1L;
        baseDto = new BaseDto();
        baseDto.setId(id);
        baseDomain = new BaseDomain();
        baseDomain.setId(id);
    }

    @Test
    void convertToDto() {
        Assertions.assertEquals(baseDto, baseMapper.convertToDto(baseDomain));
    }
}
