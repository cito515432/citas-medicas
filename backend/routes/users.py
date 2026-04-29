from flask import Blueprint, request, jsonify
from database import get_connection
from utils.security import hash_password, verify_password
import sqlite3
import re

users_bp = Blueprint('users', __name__)

# =========================
# VALIDACIÓN EMAIL SIMPLE
# =========================
def is_valid_email(email):
    return re.match(r"[^@]+@[^@]+\.[^@]+", email)


# =========================
# REGISTRO DE USUARIO
# =========================
@users_bp.route('/register', methods=['POST'])
def register():
    data = request.json

    if not data or not data.get('email') or not data.get('password'):
        return jsonify({"error": "Faltan datos"}), 400

    if not is_valid_email(data['email']):
        return jsonify({"error": "Email inválido"}), 400

    conn = get_connection()
    cursor = conn.cursor()

    try:
        # Rol automático
        role = "admin" if data['email'] == "admin@test.com" else "user"

        cursor.execute(
            "INSERT INTO users (email, password, role) VALUES (?, ?, ?)",
            (data['email'], hash_password(data['password']), role)
        )

        conn.commit()

        return jsonify({
            "message": "Usuario registrado",
            "role": role
        })

    except sqlite3.IntegrityError:
        return jsonify({"error": "El usuario ya existe"}), 400

    except Exception as e:
        print("ERROR:", e)
        return jsonify({"error": "Error en el servidor"}), 500

    finally:
        conn.close()


# =========================
# LOGIN
# =========================
@users_bp.route('/login', methods=['POST'])
def login():
    data = request.json

    if not data or not data.get('email') or not data.get('password'):
        return jsonify({"error": "Faltan datos"}), 400

    conn = get_connection()
    cursor = conn.cursor()

    cursor.execute(
        "SELECT password, role FROM users WHERE email=?",
        (data['email'],)
    )

    user = cursor.fetchone()
    conn.close()

    if user and verify_password(data['password'], user["password"]):
        return jsonify({
            "message": "Login exitoso",
            "email": data['email'],
            "role": user["role"]
        })

    return jsonify({"error": "Credenciales incorrectas"}), 401