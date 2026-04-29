from flask import Blueprint, request, jsonify
from database import get_connection

appointments_bp = Blueprint('appointments', __name__)

DOCTORS = {
    "Cardiología": ["Dr. Pérez", "Dra. Gómez"],
    "Dermatología": ["Dr. Ruiz", "Dra. Torres"],
    "Pediatría": ["Dr. López", "Dra. Martínez"]
}

# =========================
# CREAR CITA
# =========================
@appointments_bp.route('/appointments', methods=['POST'])
def create_appointment():
    data = request.json

    if not data or not data.get('user') or not data.get('doctor') or not data.get('specialty') or not data.get('date'):
        return jsonify({"error": "Faltan datos"}), 400

    conn = get_connection()
    cursor = conn.cursor()

    try:
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

    # Convertir a JSON bonito
    data = []
    for r in rows:
        data.append({
            "id": r[0],
            "user": r[1],
            "doctor": r[2],
            "specialty": r[3],
            "date": r[4],
            "status": r[5]
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
# ELIMINAR (LOGICO)
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
# ADMIN - VER TODAS
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
            "id": r[0],
            "user": r[1],
            "doctor": r[2],
            "specialty": r[3],
            "date": r[4],
            "status": r[5]
        })

    return jsonify(data)


# =========================
# EDITAR CITA
# =========================
@appointments_bp.route('/appointments/<int:id>', methods=['PUT'])
def update_appointment(id):
    data = request.json

    if not data.get('doctor') or not data.get('specialty') or not data.get('date'):
        return jsonify({"error": "Faltan datos"}), 400

    conn = get_connection()
    cursor = conn.cursor()

    cursor.execute("""
    UPDATE appointments
    SET doctor=?, specialty=?, date=?
    WHERE id=?
    """, (data['doctor'], data['specialty'], data['date'], id))

    conn.commit()
    conn.close()

    return jsonify({"message": "Cita actualizada"})
