package by.nortin.restjwtproject.mapper;

import by.nortin.restjwtproject.domain.BaseDomain;
import by.nortin.restjwtproject.dto.BaseDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BaseMapper {

    BaseDto convertToDto(BaseDomain baseDomain);
}
