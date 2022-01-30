package ru.gb.web.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.sleuth.annotation.NewSpan;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.gb.api.manufacturer.dto.ManufacturerDto;
import ru.gb.service.ManufacturerService;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/manufacturer")
public class ManufacturerController {

    private final ManufacturerService manufacturerService;

    @NewSpan
    @GetMapping
    public List<ManufacturerDto> getManufacturerList(HttpServletRequest request) {
        log.info("Getting list of manufacturers");
        Enumeration<String> headerNames = request.getHeaderNames();

        if (headerNames != null) {
            StringBuilder headers = new StringBuilder();
            headers.append("Headers from client: \n");
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                headers
                        .append("Header: ")
                        .append(headerName)
                        .append(": ")
                        .append(request.getHeader(headerName))
                        .append("\n");
            }
            log.info(headers.toString());
        }
        return manufacturerService.findAll();
    }

    @NewSpan
    @GetMapping("/{manufacturerId}")
    public ResponseEntity<?> getManufacturer(@PathVariable("manufacturerId") Long id) {
        ManufacturerDto manufacturerDto;
        if (id != null) {
            log.info("Manufacturer search by id {}", id);
            manufacturerDto = manufacturerService.findById(id);
            if (manufacturerDto != null) {
                log.info("Found manufacturer with id {} and name {}", id, manufacturerDto.getName());
                return new ResponseEntity<>(manufacturerDto, HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @NewSpan
    @PostMapping
    public ResponseEntity<?> handlePost(@Validated @RequestBody ManufacturerDto manufacturerDto) {
        log.info("Added a new manufacturer with id {} and name {}", manufacturerDto.getManufacturerId(), manufacturerDto.getName());
        manufacturerService.save(manufacturerDto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @NewSpan
    @PutMapping("/{manufacturerId}")
    public ResponseEntity<?> handleUpdate(@PathVariable("manufacturerId") Long id,
                                          @Validated @RequestBody ManufacturerDto manufacturerDto) {
        manufacturerDto.setManufacturerId(id);
        log.info("Changed manufacturer with id {}", id);
        manufacturerService.save(manufacturerDto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @NewSpan
    @DeleteMapping("/{manufacturerId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable("manufacturerId") Long id) {
        log.info("Deleted manufacturer with id {}", id);
        manufacturerService.deleteById(id);
    }
}
