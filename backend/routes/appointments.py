from flask import Blueprint, request, jsonify
from database import get_connection
from datetime import datetime, timedelta

appointments_bp = Blueprint('appointments', __name__)

DOCTORS = {
    "Cardiología": ["Dr. Pérez", "Dra. Gómez"],
    "Dermatología": ["Dr. Ruiz", "Dra. Torres"],
    "Pediatría": ["Dr. López", "Dra. Martínez"]
}


# =========================
# CONVERTIR FECHA
# =========================
def parse_date(date_text):
    """
    Acepta formatos como:
    2026-05-01T13:00
    2026-05-01 13:00
    """
    try:
        return datetime.fromisoformat(date_text.replace("T", " "))
    except Exception:
        return None


# =========================
# VALIDAR CONFLICTO DE HORARIO
# =========================
def doctor_has_conflict(conn, doctor, new_date, exclude_id=None):
    """
    Bloquea si el mismo doctor tiene otra cita activa
    con menos de 1 hora de diferencia.
    Ejemplo:
    Si hay cita a la 1:00 PM:
    - 12:30 PM NO se permite
    - 1:00 PM NO se permite
    - 1:30 PM NO se permite
    - 12:00 PM SÍ se permite
    - 2:00 PM SÍ se permite
    """

    cursor = conn.cursor()

    cursor.execute("""
        SELECT * FROM appointments
        WHERE doctor = ?
        AND status = 'ACTIVA'
    """, (doctor,))

    appointments = cursor.fetchall()

    for appointment in appointments:
        if exclude_id and appointment["id"] == exclude_id:
            continue

        existing_date = parse_date(appointment["date"])

        if not existing_date:
            continue

        difference = abs(existing_date - new_date)

        if difference < timedelta(hours=1):
            return appointment

    return None


# =========================
# CREAR CITA
# =========================
@appointments_bp.route('/appointments', methods=['POST'])
def create_appointment():
    data = request.json

    if not data or not data.get('user') or not data.get('doctor') or not data.get('specialty') or not data.get('date'):
        return jsonify({"error": "Faltan datos"}), 400

    new_date = parse_date(data['date'])

    if not new_date:
        return jsonify({"error": "Formato de fecha inválido"}), 400

    conn = get_connection()
    cursor = conn.cursor()

    try:
        conflict = doctor_has_conflict(conn, data['doctor'], new_date)

        if conflict:
            return jsonify({
                "error": f"El doctor {data['doctor']} ya tiene una cita cercana a esa hora. Debe existir al menos 1 hora de diferencia."
            }), 409

        cursor.execute("""
        INSERT INTO appointments (user, doctor, specialty, date, status)
        VALUES (?, ?, ?, ?, 'ACTIVA')
        """, (data['user'], data['doctor'], data['specialty'], data['date']))

        conn.commit()

        return jsonify({"message": "Cita creada"})

    except Exception as e:
        print(e)
        return jsonify({"error": "Error al crear cita"}), 500

    finally:
        conn.close()


# =========================
# VER CITAS ACTIVAS
# =========================
@appointments_bp.route('/appointments', methods=['GET'])
def get_appointments():
    conn = get_connection()
    cursor = conn.cursor()

    cursor.execute("SELECT * FROM appointments WHERE status='ACTIVA'")
    rows = cursor.fetchall()

    conn.close()

    data = []

    for r in rows:
        data.append({
            "id": r["id"],
            "user": r["user"],
            "doctor": r["doctor"],
            "specialty": r["specialty"],
            "date": r["date"],
            "status": r["status"]
        })

    return jsonify(data)


# =========================
# ESPECIALIDADES
# =========================
@appointments_bp.route('/specialties', methods=['GET'])
def get_specialties():
    return jsonify(list(DOCTORS.keys()))


# =========================
# DOCTORES
# =========================
@appointments_bp.route('/doctors/<specialty>', methods=['GET'])
def get_doctors(specialty):
    return jsonify(DOCTORS.get(specialty, []))


# =========================
# ELIMINAR CITA LÓGICA
# =========================
@appointments_bp.route('/appointments/<int:id>', methods=['DELETE'])
def delete_appointment(id):
    conn = get_connection()
    cursor = conn.cursor()

    cursor.execute("UPDATE appointments SET status='ELIMINADA' WHERE id=?", (id,))

    conn.commit()
    conn.close()

    return jsonify({"message": "Cita eliminada"})


# =========================
# ADMIN - VER TODAS LAS CITAS
# =========================
@appointments_bp.route('/admin/appointments', methods=['GET'])
def admin_all():
    conn = get_connection()
    cursor = conn.cursor()

    cursor.execute("SELECT * FROM appointments")
    rows = cursor.fetchall()

    conn.close()

    data = []

    for r in rows:
        data.append({
            "id": r["id"],
            "user": r["user"],
            "doctor": r["doctor"],
            "specialty": r["specialty"],
            "date": r["date"],
            "status": r["status"]
        })

    return jsonify(data)


# =========================
# EDITAR CITA
# =========================
@appointments_bp.route('/appointments/<int:id>', methods=['PUT'])
def update_appointment(id):
    data = request.json

    if not data or not data.get('doctor') or not data.get('specialty') or not data.get('date'):
        return jsonify({"error": "Faltan datos"}), 400

    new_date = parse_date(data['date'])

    if not new_date:
        return jsonify({"error": "Formato de fecha inválido"}), 400

    conn = get_connection()
    cursor = conn.cursor()

    try:
        conflict = doctor_has_conflict(conn, data['doctor'], new_date, exclude_id=id)

        if conflict:
            return jsonify({
                "error": f"El doctor {data['doctor']} ya tiene una cita cercana a esa hora. Debe existir al menos 1 hora de diferencia."
            }), 409

        cursor.execute("""
        UPDATE appointments
        SET doctor=?, specialty=?, date=?
        WHERE id=?
        """, (data['doctor'], data['specialty'], data['date'], id))

        conn.commit()

        if cursor.rowcount == 0:
            return jsonify({"error": "Cita no encontrada"}), 404

        return jsonify({"message": "Cita actualizada"})

    except Exception as e:
        print(e)
        return jsonify({"error": "Error al actualizar cita"}), 500

    finally:
        conn.close()