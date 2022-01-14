package ru.gb.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import ru.gb.dao.ManufacturerDao;
import ru.gb.entity.Manufacturer;
import ru.gb.web.dto.ManufacturerDto;
import ru.gb.web.dto.mapper.ManufacturerMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class ManufacturerServiceTest {

    @Mock
    private ManufacturerDao manufacturerDao;

    @Mock
    private ManufacturerMapper manufacturerMapper;

    @InjectMocks
    ManufacturerService manufacturerService;


    @Test
    public void findAllManufacturersTest() {
        // given
        List<Manufacturer> manufacturers = new ArrayList<>(){{
            add(new Manufacturer(1L, "Apple", new HashSet<>(), 1, "user1", LocalDateTime.now(), "user1", LocalDateTime.now()));
            add(new Manufacturer(2L, "Microsoft", new HashSet<>(), 1, "user1", LocalDateTime.now(), "user1", LocalDateTime.now()));
        }};

        given(manufacturerDao.findAll()).willReturn(manufacturers);

        // when
        List<ManufacturerDto> manufacturerDtos = manufacturerService.findAll();

        // then
        then(manufacturerDao).should().findAll();
        assertEquals(manufacturers.size(), manufacturerDtos.size());
    }

    @Test
    public void saveManufacturer() {
        // given
        Manufacturer manufacturerFromDao = new Manufacturer(1L, "Apple", new HashSet<>(), 1, "user1", LocalDateTime.now(), "user1", LocalDateTime.now());

        ManufacturerDto manufacturerDto = new ManufacturerDto(null, "Apple");

        given(manufacturerDao.save(any(Manufacturer.class))).willReturn(manufacturerFromDao);
        given(manufacturerMapper.toManufacturer(any())).will(new ToManufacturer());
        given(manufacturerMapper.toManufacturerDto(any())).will(new ToManufacturerDto());

        // when

        ManufacturerDto returnedManufactureDto = manufacturerService.save(manufacturerDto);

        // then
        then(manufacturerDao).should().save(any(Manufacturer.class));
        assertEquals(1L, returnedManufactureDto.getManufacturerId());

    }
}

class ToManufacturer implements Answer<Manufacturer> {

    @Override
    public Manufacturer answer(InvocationOnMock invocation) throws Throwable {

        ManufacturerDto manufacturerDto = (ManufacturerDto) invocation.getArgument(0);
        if (manufacturerDto == null) {
            return null;
        }

        Manufacturer.ManufacturerBuilder manufacturer = Manufacturer.builder();

        manufacturer.id(manufacturerDto.getManufacturerId());
        manufacturer.name(manufacturerDto.getName());

        return manufacturer.build();
    }
}
class ToManufacturerDto implements Answer<ManufacturerDto> {

    @Override
    public ManufacturerDto answer(InvocationOnMock invocation) throws Throwable {
        Manufacturer manufacturer = (Manufacturer) invocation.getArgument(0);

        if ( manufacturer == null ) {
            return null;
        }

        ManufacturerDto.ManufacturerDtoBuilder manufacturerDto = ManufacturerDto.builder();

        manufacturerDto.manufacturerId( manufacturer.getId() );
        manufacturerDto.name( manufacturer.getName() );

        return manufacturerDto.build();
    }
}