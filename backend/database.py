import sqlite3
import os

# Ruta absoluta del archivo
BASE_DIR = os.path.dirname(os.path.abspath(__file__))
DB_PATH = os.path.join(BASE_DIR, 'database.db')


def get_connection():
    conn = sqlite3.connect(DB_PATH)
    conn.row_factory = sqlite3.Row  # 🔥 clave
    return conn


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
        role TEXT DEFAULT 'user'
    )
    """)

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
        status TEXT DEFAULT 'ACTIVA'
    )
    """)

    conn.commit()
    conn.close()


if __name__ == "__main__":
    init_db()