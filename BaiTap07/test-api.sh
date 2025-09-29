#!/bin/bash

BASE_URL="http://localhost:8080"

echo "=== Testing Category API ==="

# Test GET all categories
echo "1. GET All Categories:"
curl -X GET "$BASE_URL/api/category" -H "Accept: application/json"

echo -e "\n\n2. GET Category by ID:"
curl -X GET "$BASE_URL/api/category/1" -H "Accept: application/json"

echo -e "\n\n3. POST Create Category:"
curl -X POST "$BASE_URL/api/category" \
  -F "categoryName=Test Category" \
  -F "icon=@test-image.jpg"

echo -e "\n\n=== Testing Product API ==="

# Test GET all products
echo "4. GET All Products:"
curl -X GET "$BASE_URL/api/product" -H "Accept: application/json"

echo -e "\n\n5. GET Product by ID:"
curl -X GET "$BASE_URL/api/product/1" -H "Accept: application/json"

echo -e "\n\n6. POST Create Product:"
curl -X POST "$BASE_URL/api/product" \
  -F "productName=Test Product" \
  -F "quantity=5" \
  -F "unitPrice=100000" \
  -F "description=Test Description" \
  -F "discount=10" \
  -F "status=1" \
  -F "categoryId=1" \
  -F "images=@test-image.jpg"

echo -e "\n\n=== Test Complete ==="
