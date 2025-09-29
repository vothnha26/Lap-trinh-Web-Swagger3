let currentProductId = null;

$(document).ready(function() {
    loadCategories();
    loadProducts();
    
    // Xử lý submit form
    $('#productForm').on('submit', function(e) {
        e.preventDefault();
        saveProduct();
    });
    
    // Xử lý xác nhận xóa
    $('#confirmDelete').on('click', function() {
        if (currentProductId) {
            deleteProduct(currentProductId);
        }
    });
});

// Load danh sách categories cho dropdown
function loadCategories() {
    $.ajax({
        url: '/api/category',
        method: 'GET',
        success: function(response) {
            if (response.status) {
                displayCategoriesDropdown(response.body);
            }
        },
        error: function() {
            showAlert('Lỗi khi tải danh sách categories', 'danger');
        }
    });
}

// Hiển thị categories trong dropdown
function displayCategoriesDropdown(categories) {
    let html = '<option value="">Chọn Category</option>';
    categories.forEach(function(category) {
        html += `<option value="${category.categoryId}">${category.categoryName}</option>`;
    });
    $('#categoryId').html(html);
}

// Load danh sách products
function loadProducts() {
    $.ajax({
        url: '/api/product',
        method: 'GET',
        success: function(response) {
            if (response.status) {
                displayProducts(response.body);
            } else {
                showAlert('Lỗi: ' + response.message, 'danger');
            }
        },
        error: function() {
            showAlert('Lỗi khi tải danh sách products', 'danger');
        }
    });
}

// Hiển thị danh sách products
function displayProducts(products) {
    let html = '';
    products.forEach(function(product) {
        const statusText = product.status === 1 ? 'Hoạt động' : 'Không hoạt động';
        const statusClass = product.status === 1 ? 'success' : 'danger';
        
        html += `
            <tr>
                <td>${product.productId}</td>
                <td>${product.productName}</td>
                <td>${product.category ? product.category.categoryName : 'N/A'}</td>
                <td>${product.quantity}</td>
                <td>${formatCurrency(product.unitPrice)}</td>
                <td>${product.discount}%</td>
                <td><span class="badge bg-${statusClass}">${statusText}</span></td>
                <td>
                    ${product.images ? 
                        `<img src="/uploads/${product.images}" alt="Product" style="width: 50px; height: 50px; object-fit: cover;">` : 
                        '<span class="text-muted">Không có</span>'
                    }
                </td>
                <td>
                    <button class="btn btn-sm btn-warning me-2" onclick="editProduct(${product.productId})">
                        <i class="fas fa-edit"></i> Sửa
                    </button>
                    <button class="btn btn-sm btn-danger" onclick="confirmDeleteProduct(${product.productId})">
                        <i class="fas fa-trash"></i> Xóa
                    </button>
                </td>
            </tr>
        `;
    });
    $('#productTableBody').html(html);
}

// Lưu product
function saveProduct() {
    const formData = new FormData();
    formData.append('productName', $('#productName').val());
    formData.append('quantity', $('#quantity').val());
    formData.append('unitPrice', $('#unitPrice').val());
    formData.append('description', $('#description').val());
    formData.append('discount', $('#discount').val() || 0);
    formData.append('status', $('#status').val());
    formData.append('categoryId', $('#categoryId').val());
    
    const imagesFile = $('#images')[0].files[0];
    if (imagesFile) {
        formData.append('images', imagesFile);
    }
    
    let url = '/api/product';
    let method = 'POST';
    
    if (currentProductId) {
        url = `/api/product/${currentProductId}`;
        method = 'PUT';
    }
    
    $.ajax({
        url: url,
        method: method,
        data: formData,
        processData: false,
        contentType: false,
        success: function(response) {
            if (response.status) {
                showAlert(response.message, 'success');
                resetForm();
                loadProducts();
            } else {
                showAlert('Lỗi: ' + response.message, 'danger');
            }
        },
        error: function() {
            showAlert('Lỗi khi lưu product', 'danger');
        }
    });
}

// Sửa product
function editProduct(id) {
    $.ajax({
        url: `/api/product/${id}`,
        method: 'GET',
        success: function(response) {
            if (response.status) {
                const product = response.body;
                $('#productId').val(product.productId);
                $('#productName').val(product.productName);
                $('#quantity').val(product.quantity);
                $('#unitPrice').val(product.unitPrice);
                $('#description').val(product.description);
                $('#discount').val(product.discount);
                $('#status').val(product.status);
                $('#categoryId').val(product.category ? product.category.categoryId : '');
                $('#formTitle').text('Sửa Product');
                $('#submitBtn').html('<i class="fas fa-save"></i> Cập nhật');
                currentProductId = id;
            } else {
                showAlert('Lỗi: ' + response.message, 'danger');
            }
        },
        error: function() {
            showAlert('Lỗi khi tải thông tin product', 'danger');
        }
    });
}

// Xác nhận xóa product
function confirmDeleteProduct(id) {
    currentProductId = id;
    $('#deleteModal').modal('show');
}

// Xóa product
function deleteProduct(id) {
    $.ajax({
        url: `/api/product/${id}`,
        method: 'DELETE',
        success: function(response) {
            if (response.status) {
                showAlert(response.message, 'success');
                loadProducts();
            } else {
                showAlert('Lỗi: ' + response.message, 'danger');
            }
            $('#deleteModal').modal('hide');
        },
        error: function() {
            showAlert('Lỗi khi xóa product', 'danger');
            $('#deleteModal').modal('hide');
        }
    });
}

// Reset form
function resetForm() {
    $('#productForm')[0].reset();
    $('#productId').val('');
    $('#formTitle').text('Thêm Product mới');
    $('#submitBtn').html('<i class="fas fa-save"></i> Lưu');
    currentProductId = null;
}

// Format tiền tệ
function formatCurrency(amount) {
    return new Intl.NumberFormat('vi-VN', {
        style: 'currency',
        currency: 'VND'
    }).format(amount);
}

// Hiển thị thông báo
function showAlert(message, type) {
    const alertHtml = `
        <div class="alert alert-${type} alert-dismissible fade show" role="alert">
            ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    `;
    $('.container').prepend(alertHtml);
    
    // Tự động ẩn sau 3 giây
    setTimeout(function() {
        $('.alert').fadeOut();
    }, 3000);
}
