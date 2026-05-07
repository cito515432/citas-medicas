import sqlite3
import os
from werkzeug.security import generate_password_hash

# Ruta absoluta del archivo
BASE_DIR = os.path.dirname(os.path.abspath(__file__))
DB_PATH = os.path.join(BASE_DIR, 'database.db')

# Cuentas de doctores disponibles.
# Contraseña inicial para todos: 123456
PREDEFINED_DOCTOR_USERS = [
    ("perez@saludpro.com", "Dr. Pérez"),
    ("gomez@saludpro.com", "Dra. Gómez"),
    ("ruiz@saludpro.com", "Dr. Ruiz"),
    ("torres@saludpro.com", "Dra. Torres"),
    ("lopez@saludpro.com", "Dr. López"),
    ("martinez@saludpro.com", "Dra. Martínez"),
]


def get_connection():
    conn = sqlite3.connect(DB_PATH)
    conn.row_factory = sqlite3.Row
    return conn


def column_exists(cursor, table_name, column_name):
    cursor.execute(f"PRAGMA table_info({table_name})")
    columns = [row[1] for row in cursor.fetchall()]
    return column_name in columns


def add_column_if_missing(cursor, table_name, column_name, definition):
    if not column_exists(cursor, table_name, column_name):
        cursor.execute(f"ALTER TABLE {table_name} ADD COLUMN {column_name} {definition}")


def init_db():
    conn = get_connection()
    cursor = conn.cursor()

    # =========================
    # TABLA USUARIOS
    # =========================
    cursor.execute("""
    CREATE TABLE IF NOT EXISTS users (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        email TEXT UNIQUE,
        password TEXT,
        role TEXT DEFAULT 'user',
        doctor_name TEXT,
        fine_pending INTEGER DEFAULT 0
    )
    """)

    # Migraciones para bases de datos ya existentes en Render
    add_column_if_missing(cursor, "users", "role", "TEXT DEFAULT 'user'")
    add_column_if_missing(cursor, "users", "doctor_name", "TEXT")
    add_column_if_missing(cursor, "users", "fine_pending", "INTEGER DEFAULT 0")

    # =========================
    # TABLA CITAS
    # =========================
    cursor.execute("""
    CREATE TABLE IF NOT EXISTS appointments (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        user TEXT,
        doctor TEXT,
        specialty TEXT,
        date TEXT,
        status TEXT DEFAULT 'ACTIVA',
        attendance TEXT DEFAULT 'PENDIENTE'
    )
    """)

    # Migración para bases de datos ya existentes
    add_column_if_missing(cursor, "appointments", "attendance", "TEXT DEFAULT 'PENDIENTE'")

    # Crear cuentas de doctores sin borrar usuarios existentes
    default_password = generate_password_hash("123456")
    for email, doctor_name in PREDEFINED_DOCTOR_USERS:
        cursor.execute("""
            INSERT OR IGNORE INTO users (email, password, role, doctor_name, fine_pending)
            VALUES (?, ?, 'doctor', ?, 0)
        """, (email, default_password, doctor_name))

        # Si la cuenta ya existía, se asegura el rol correcto y el doctor asociado
        cursor.execute("""
            UPDATE users
            SET role='doctor', doctor_name=?
            WHERE email=?
        """, (doctor_name, email))

    conn.commit()
    conn.close()


if __name__ == "__main__":
    init_db()
