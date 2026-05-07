from flask import Blueprint, request, jsonify
from database import get_connection, PREDEFINED_DOCTOR_USERS
from utils.security import hash_password, verify_password
import sqlite3
import re

users_bp = Blueprint('users', __name__)
DOCTOR_EMAILS = {email for email, _ in PREDEFINED_DOCTOR_USERS}


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

    email = data['email'].strip().lower()
    password = data['password']

    if not is_valid_email(email):
        return jsonify({"error": "Email inválido"}), 400

    if email in DOCTOR_EMAILS:
        return jsonify({
            "error": "Este correo pertenece a una cuenta de doctor. Inicia sesión con la contraseña asignada."
        }), 400

    conn = get_connection()
    cursor = conn.cursor()

    try:
        # Rol automático: admin solo para el correo definido
        role = "admin" if email == "admin@test.com" else "user"

        cursor.execute("""
            INSERT INTO users (email, password, role, doctor_name, fine_pending)
            VALUES (?, ?, ?, NULL, 0)
        """, (email, hash_password(password), role))

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

    email = data['email'].strip().lower()
    password = data['password']

    conn = get_connection()
    cursor = conn.cursor()

    cursor.execute("""
        SELECT password, role, doctor_name, fine_pending
        FROM users
        WHERE email=?
    """, (email,))

    user = cursor.fetchone()
    conn.close()

    if user and verify_password(password, user["password"]):
        return jsonify({
            "message": "Login exitoso",
            "email": email,
            "role": user["role"],
            "doctorName": user["doctor_name"],
            "finePending": bool(user["fine_pending"])
        })

    return jsonify({"error": "Credenciales incorrectas"}), 401


# =========================
# ESTADO DEL USUARIO / MULTA
# =========================
@users_bp.route('/user-status/<path:email>', methods=['GET'])
def user_status(email):
    conn = get_connection()
    cursor = conn.cursor()

    cursor.execute("""
        SELECT email, role, doctor_name, fine_pending
        FROM users
        WHERE email=?
    """, (email.strip().lower(),))

    user = cursor.fetchone()
    conn.close()

    if not user:
        return jsonify({"error": "Usuario no encontrado"}), 404

    return jsonify({
        "email": user["email"],
        "role": user["role"],
        "doctorName": user["doctor_name"],
        "finePending": bool(user["fine_pending"]),
        "fineAmount": 70000
    })


# =========================
# SIMULAR PAGO DE MULTA
# =========================
@users_bp.route('/pay-fine', methods=['POST'])
def pay_fine():
    data = request.json

    if not data or not data.get('email'):
        return jsonify({"error": "Falta el correo del usuario"}), 400

    email = data['email'].strip().lower()

    conn = get_connection()
    cursor = conn.cursor()

    cursor.execute("UPDATE users SET fine_pending=0 WHERE email=?", (email,))
    conn.commit()

    updated = cursor.rowcount
    conn.close()

    if updated == 0:
        return jsonify({"error": "Usuario no encontrado"}), 404

    return jsonify({
        "message": "Multa pagada. Ya puedes agendar citas nuevamente.",
        "finePending": False
    })
