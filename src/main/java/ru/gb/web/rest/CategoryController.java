package ru.gb.web.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.gb.api.category.dto.CategoryDto;
import ru.gb.service.CategoryService;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/category")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public List<CategoryDto> getCategoryList(HttpServletRequest request) {
        log.info("Getting category list");
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
        return categoryService.findAll();
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<?> getCategory(@PathVariable("categoryId") Long id) {
        CategoryDto categoryDto;
        if (id != null) {
            categoryDto = categoryService.findById(id);
            if (categoryDto != null) {
                return new ResponseEntity<>(categoryDto, HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping
    public ResponseEntity<?> handlePost(@Validated @RequestBody CategoryDto categoryDto) {
        categoryService.save(categoryDto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<?> handleUpdate(@PathVariable("categoryId") Long id,
                                          @Validated @RequestBody CategoryDto categoryDto) {
        categoryDto.setCategoryId(id);
        categoryService.save(categoryDto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{categoryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable("categoryId") Long id) {
        categoryService.deleteById(id);
    }
}
