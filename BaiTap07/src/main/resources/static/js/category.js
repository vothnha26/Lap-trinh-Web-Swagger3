let currentCategoryId = null;

$(document).ready(function() {
    loadCategories();
    
    // Xử lý submit form
    $('#categoryForm').on('submit', function(e) {
        e.preventDefault();
        saveCategory();
    });
    
    // Xử lý xác nhận xóa
    $('#confirmDelete').on('click', function() {
        if (currentCategoryId) {
            deleteCategory(currentCategoryId);
        }
    });
});

// Load danh sách categories
function loadCategories() {
    $.ajax({
        url: '/api/category',
        method: 'GET',
        success: function(response) {
            if (response.status) {
                displayCategories(response.body);
            } else {
                showAlert('Lỗi: ' + response.message, 'danger');
            }
        },
        error: function() {
            showAlert('Lỗi khi tải danh sách categories', 'danger');
        }
    });
}

// Hiển thị danh sách categories
function displayCategories(categories) {
    let html = '';
    categories.forEach(function(category) {
        html += `
            <tr>
                <td>${category.categoryId}</td>
                <td>${category.categoryName}</td>
                <td>
                    ${category.icon ? 
                        `<img src="/uploads/${category.icon}" alt="Icon" style="width: 50px; height: 50px; object-fit: cover;">` : 
                        '<span class="text-muted">Không có</span>'
                    }
                </td>
                <td>
                    <button class="btn btn-sm btn-warning me-2" onclick="editCategory(${category.categoryId})">
                        <i class="fas fa-edit"></i> Sửa
                    </button>
                    <button class="btn btn-sm btn-danger" onclick="confirmDeleteCategory(${category.categoryId})">
                        <i class="fas fa-trash"></i> Xóa
                    </button>
                </td>
            </tr>
        `;
    });
    $('#categoryTableBody').html(html);
}

// Lưu category
function saveCategory() {
    const formData = new FormData();
    formData.append('categoryName', $('#categoryName').val());
    
    const iconFile = $('#icon')[0].files[0];
    if (iconFile) {
        formData.append('icon', iconFile);
    }
    
    let url = '/api/category';
    let method = 'POST';
    
    if (currentCategoryId) {
        url = `/api/category/${currentCategoryId}`;
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
                loadCategories();
            } else {
                showAlert('Lỗi: ' + response.message, 'danger');
            }
        },
        error: function() {
            showAlert('Lỗi khi lưu category', 'danger');
        }
    });
}

// Sửa category
function editCategory(id) {
    $.ajax({
        url: `/api/category/${id}`,
        method: 'GET',
        success: function(response) {
            if (response.status) {
                const category = response.body;
                $('#categoryId').val(category.categoryId);
                $('#categoryName').val(category.categoryName);
                $('#formTitle').text('Sửa Category');
                $('#submitBtn').html('<i class="fas fa-save"></i> Cập nhật');
                currentCategoryId = id;
            } else {
                showAlert('Lỗi: ' + response.message, 'danger');
            }
        },
        error: function() {
            showAlert('Lỗi khi tải thông tin category', 'danger');
        }
    });
}

// Xác nhận xóa category
function confirmDeleteCategory(id) {
    currentCategoryId = id;
    $('#deleteModal').modal('show');
}

// Xóa category
function deleteCategory(id) {
    $.ajax({
        url: `/api/category/${id}`,
        method: 'DELETE',
        success: function(response) {
            if (response.status) {
                showAlert(response.message, 'success');
                loadCategories();
            } else {
                showAlert('Lỗi: ' + response.message, 'danger');
            }
            $('#deleteModal').modal('hide');
        },
        error: function() {
            showAlert('Lỗi khi xóa category', 'danger');
            $('#deleteModal').modal('hide');
        }
    });
}

// Reset form
function resetForm() {
    $('#categoryForm')[0].reset();
    $('#categoryId').val('');
    $('#formTitle').text('Thêm Category mới');
    $('#submitBtn').html('<i class="fas fa-save"></i> Lưu');
    currentCategoryId = null;
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
