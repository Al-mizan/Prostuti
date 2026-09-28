#!/bin/bash
set -e

BASE_URL="http://127.0.0.1:5000/api/v1"

echo "=== Testing Registration ==="
REGISTER_RES=$(curl -s -X POST "$BASE_URL/auth/register" \
  -H "Content-Type: application/json" \
  -d '{"name":"Test Student", "email":"student@test.com", "password":"password123"}')
echo "$REGISTER_RES"

echo "=== Testing Login (Student) ==="
LOGIN_RES=$(curl -s -X POST "$BASE_URL/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"email":"student@test.com", "password":"password123"}')
echo "$LOGIN_RES"

TOKEN=$(echo $LOGIN_RES | grep -o '"token":"[^"]*' | grep -o '[^"]*$')

echo "=== Testing /me as Student ==="
curl -s -X GET "$BASE_URL/me" \
  -H "Authorization: Bearer $TOKEN"
echo ""

echo "=== Testing Bootstrap Admin ==="
ADMIN_RES=$(curl -s -X POST "$BASE_URL/auth/bootstrap-admin" \
  -H "Content-Type: application/json" \
  -H "X-Bootstrap-Token: bootstraptoken123" \
  -d '{"name":"Admin User", "email":"admin@test.com", "password":"adminpassword"}')
echo "$ADMIN_RES"

ADMIN_TOKEN=$(echo $ADMIN_RES | grep -o '"token":"[^"]*' | grep -o '[^"]*$')

echo "=== Testing /me as Admin ==="
curl -s -X GET "$BASE_URL/me" \
  -H "Authorization: Bearer $ADMIN_TOKEN"
echo ""
