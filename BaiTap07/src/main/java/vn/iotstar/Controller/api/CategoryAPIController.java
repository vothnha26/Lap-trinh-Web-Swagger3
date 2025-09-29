package vn.iotstar.Controller.api;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import vn.iotstar.entity.Category;
import vn.iotstar.model.Response;
import vn.iotstar.service.ICategoryService;
import vn.iotstar.service.IStorageService;

@RestController
@RequestMapping(path = "/api/category")
public class CategoryAPIController {
	@Autowired
	private ICategoryService categoryService;
	@Autowired
	IStorageService storageService;
	
	// Lấy tất cả category
	@GetMapping
	public ResponseEntity<?> getAllCategory() {
		return new ResponseEntity<Response>(new Response(true, "Thành công", categoryService.findAll()), HttpStatus.OK);
	}
	
	// Lấy category theo ID
	@GetMapping("/{id}")
	public ResponseEntity<?> getCategory(@PathVariable("id") Long id) {
		Optional<Category> category = categoryService.findById(id);
		if (category.isPresent()) {
			return new ResponseEntity<Response>(new Response(true, "Thành công", category.get()), HttpStatus.OK);
		} else {
			return new ResponseEntity<Response>(new Response(false, "Không tìm thấy category", null), HttpStatus.NOT_FOUND);
		}
	}
	
	// Thêm category mới
	@PostMapping
	public ResponseEntity<?> addCategory(@RequestParam("categoryName") String categoryName,
			@RequestParam(value = "icon", required = false) MultipartFile icon) {
		Optional<Category> optCategory = categoryService.findByCategoryName(categoryName);
		if (optCategory.isPresent()) {
			return new ResponseEntity<Response>(new Response(false, "Category đã tồn tại trong hệ thống", null), HttpStatus.BAD_REQUEST);
		} else {
			Category category = new Category();
			category.setCategoryName(categoryName);
			
			//kiểm tra tồn tại file, lưu file
			if(icon != null && !icon.isEmpty()) {
				UUID uuid = UUID.randomUUID();
				String uuString = uuid.toString();
				//lưu file vào trường Images
				category.setIcon(storageService.getSorageFilename(icon, uuString));
				storageService.store(icon, category.getIcon());
			}
			
			categoryService.save(category);
			return new ResponseEntity<Response>(new Response(true, "Thêm thành công", category), HttpStatus.OK);
		}
	}
	
	// Cập nhật category
	@PutMapping("/{id}")
	public ResponseEntity<?> updateCategory(@PathVariable("id") Long id,
			@RequestParam("categoryName") String categoryName,
			@RequestParam(value = "icon", required = false) MultipartFile icon) {
		Optional<Category> optCategory = categoryService.findById(id);
		if (optCategory.isPresent()) {
			Category category = optCategory.get();
			category.setCategoryName(categoryName);
			
			//kiểm tra tồn tại file, lưu file
			if(icon != null && !icon.isEmpty()) {
				UUID uuid = UUID.randomUUID();
				String uuString = uuid.toString();
				//lưu file vào trường Images
				category.setIcon(storageService.getSorageFilename(icon, uuString));
				storageService.store(icon, category.getIcon());
			}
			
			categoryService.save(category);
			return new ResponseEntity<Response>(new Response(true, "Cập nhật thành công", category), HttpStatus.OK);
		} else {
			return new ResponseEntity<Response>(new Response(false, "Không tìm thấy category", null), HttpStatus.NOT_FOUND);
		}
	}
	
	// Xóa category
	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteCategory(@PathVariable("id") Long id) {
		Optional<Category> optCategory = categoryService.findById(id);
		if (optCategory.isPresent()) {
			categoryService.deleteById(id);
			return new ResponseEntity<Response>(new Response(true, "Xóa thành công", null), HttpStatus.OK);
		} else {
			return new ResponseEntity<Response>(new Response(false, "Không tìm thấy category", null), HttpStatus.NOT_FOUND);
		}
	}
}
