package vn.iotstar.Controller.api;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import vn.iotstar.entity.Product;
import vn.iotstar.model.Response;
import vn.iotstar.service.ICategoryService;
import vn.iotstar.service.IProductService;
import vn.iotstar.service.IStorageService;

@RestController
@RequestMapping(path = "/api/product")
public class ProductAPIController {
	@Autowired
	private IProductService productService;
	@Autowired
	private ICategoryService categoryService;
	@Autowired
	IStorageService storageService;
	
	// Lấy tất cả product
	@GetMapping
	public ResponseEntity<?> getAllProduct() {
		return new ResponseEntity<Response>(new Response(true, "Thành công", productService.findAll()), HttpStatus.OK);
	}
	
	// Lấy product theo ID
	@GetMapping("/{id}")
	public ResponseEntity<?> getProduct(@PathVariable("id") Long id) {
		Optional<Product> product = productService.findById(id);
		if (product.isPresent()) {
			return new ResponseEntity<Response>(new Response(true, "Thành công", product.get()), HttpStatus.OK);
		} else {
			return new ResponseEntity<Response>(new Response(false, "Không tìm thấy product", null), HttpStatus.NOT_FOUND);
		}
	}
	
	// Thêm product mới
	@PostMapping
	public ResponseEntity<?> addProduct(@RequestParam("productName") String productName,
			@RequestParam("quantity") int quantity,
			@RequestParam("unitPrice") double unitPrice,
			@RequestParam("description") String description,
			@RequestParam("discount") double discount,
			@RequestParam("status") short status,
			@RequestParam("categoryId") Long categoryId,
			@RequestParam(value = "images", required = false) MultipartFile images) {
		
		Optional<Category> optCategory = categoryService.findById(categoryId);
		if (!optCategory.isPresent()) {
			return new ResponseEntity<Response>(new Response(false, "Category không tồn tại", null), HttpStatus.BAD_REQUEST);
		}
		
		Optional<Product> optProduct = productService.findByProductName(productName);
		if (optProduct.isPresent()) {
			return new ResponseEntity<Response>(new Response(false, "Product đã tồn tại trong hệ thống", null), HttpStatus.BAD_REQUEST);
		}
		
		Product product = new Product();
		product.setProductName(productName);
		product.setQuantity(quantity);
		product.setUnitPrice(unitPrice);
		product.setDescription(description);
		product.setDiscount(discount);
		product.setStatus(status);
		product.setCreateDate(new Date());
		product.setCategory(optCategory.get());
		
		//kiểm tra tồn tại file, lưu file
		if(images != null && !images.isEmpty()) {
			UUID uuid = UUID.randomUUID();
			String uuString = uuid.toString();
			//lưu file vào trường Images
			product.setImages(storageService.getSorageFilename(images, uuString));
			storageService.store(images, product.getImages());
		}
		
		productService.save(product);
		return new ResponseEntity<Response>(new Response(true, "Thêm thành công", product), HttpStatus.OK);
	}
	
	// Cập nhật product
	@PutMapping("/{id}")
	public ResponseEntity<?> updateProduct(@PathVariable("id") Long id,
			@RequestParam("productName") String productName,
			@RequestParam("quantity") int quantity,
			@RequestParam("unitPrice") double unitPrice,
			@RequestParam("description") String description,
			@RequestParam("discount") double discount,
			@RequestParam("status") short status,
			@RequestParam("categoryId") Long categoryId,
			@RequestParam(value = "images", required = false) MultipartFile images) {
		
		Optional<Product> optProduct = productService.findById(id);
		if (!optProduct.isPresent()) {
			return new ResponseEntity<Response>(new Response(false, "Không tìm thấy product", null), HttpStatus.NOT_FOUND);
		}
		
		Optional<Category> optCategory = categoryService.findById(categoryId);
		if (!optCategory.isPresent()) {
			return new ResponseEntity<Response>(new Response(false, "Category không tồn tại", null), HttpStatus.BAD_REQUEST);
		}
		
		Product product = optProduct.get();
		product.setProductName(productName);
		product.setQuantity(quantity);
		product.setUnitPrice(unitPrice);
		product.setDescription(description);
		product.setDiscount(discount);
		product.setStatus(status);
		product.setCategory(optCategory.get());
		
		//kiểm tra tồn tại file, lưu file
		if(images != null && !images.isEmpty()) {
			UUID uuid = UUID.randomUUID();
			String uuString = uuid.toString();
			//lưu file vào trường Images
			product.setImages(storageService.getSorageFilename(images, uuString));
			storageService.store(images, product.getImages());
		}
		
		productService.save(product);
		return new ResponseEntity<Response>(new Response(true, "Cập nhật thành công", product), HttpStatus.OK);
	}
	
	// Xóa product
	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteProduct(@PathVariable("id") Long id) {
		Optional<Product> optProduct = productService.findById(id);
		if (optProduct.isPresent()) {
			productService.deleteById(id);
			return new ResponseEntity<Response>(new Response(true, "Xóa thành công", null), HttpStatus.OK);
		} else {
			return new ResponseEntity<Response>(new Response(false, "Không tìm thấy product", null), HttpStatus.NOT_FOUND);
		}
	}
}
