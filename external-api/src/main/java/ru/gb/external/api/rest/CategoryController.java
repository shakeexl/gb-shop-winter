package ru.gb.external.api.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.gb.api.category.api.CategoryGateway;
import ru.gb.api.category.dto.CategoryDto;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/category")
public class CategoryController {

//    private final CategoryGateway categoryGateway;
//
//    @GetMapping
//    public List<CategoryDto> getCategoryList() {
//        return categoryGateway.getCategoryList();
//    }
//
//    @GetMapping("/{categoryId}")
//    public ResponseEntity<?> getCategory(@PathVariable("categoryId") Long id) {
//        return categoryGateway.getCategory(id);
//    }

//    @PostMapping
//    public ResponseEntity<?> handlePost(@Validated @RequestBody CategoryDto categoryDto) {
//        categoryService.save(categoryDto);
//        return new ResponseEntity<>(HttpStatus.CREATED);
//    }
//
//    @PutMapping("/{categoryId}")
//    public ResponseEntity<?> handleUpdate(@PathVariable("categoryId") Long id,
//                                          @Validated @RequestBody CategoryDto categoryDto) {
//        categoryDto.setCategoryId(id);
//        categoryService.save(categoryDto);
//        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//    }
//
//    @DeleteMapping("/{categoryId}")
//    @ResponseStatus(HttpStatus.NO_CONTENT)
//    public void deleteById(@PathVariable("categoryId") Long id) {
//        categoryService.deleteById(id);
//    }
}
