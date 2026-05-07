from flask import Blueprint, request, jsonify
from database import get_connection
from datetime import datetime, timedelta
try:
    from zoneinfo import ZoneInfo
except Exception:  # pragma: no cover
    ZoneInfo = None

appointments_bp = Blueprint('appointments', __name__)

DOCTORS = {
    "Cardiología": ["Dr. Pérez", "Dra. Gómez"],
    "Dermatología": ["Dr. Ruiz", "Dra. Torres"],
    "Pediatría": ["Dr. López", "Dra. Martínez"]
}


# =========================
# FECHA ACTUAL LOCAL
# =========================
def now_local():
    """Render puede usar UTC; para este proyecto se compara con hora Colombia."""
    if ZoneInfo:
        return datetime.now(ZoneInfo("America/Bogota")).replace(tzinfo=None)
    return datetime.now()


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
        return datetime.fromisoformat(str(date_text).replace("T", " "))
    except Exception:
        return None


# =========================
# REGLA: SOLO HASTA 1 HORA ANTES
# =========================
def can_user_modify(date_text):
    appointment_date = parse_date(date_text)
    if not appointment_date:
        return False
    return now_local() < (appointment_date - timedelta(hours=1))


# =========================
# VALIDAR CONFLICTO DE HORARIO
# =========================
def doctor_has_conflict(conn, doctor, new_date, exclude_id=None):
    """
    Bloquea si el mismo doctor tiene otra cita activa
    con menos de 1 hora de diferencia.
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
# SERIALIZAR CITA
# =========================
def appointment_to_dict(row):
    return {
        "id": row["id"],
        "user": row["user"],
        "doctor": row["doctor"],
        "specialty": row["specialty"],
        "date": row["date"],
        "status": row["status"],
        "attendance": row["attendance"] if "attendance" in row.keys() else "PENDIENTE",
        "canModify": can_user_modify(row["date"])
    }


# =========================
# DATOS DEL ACTOR
# =========================
def get_actor_data():
    data = request.get_json(silent=True) or {}
    return {
        "user": data.get("user") or request.args.get("user"),
        "role": data.get("role") or request.args.get("role") or "user",
        "doctorName": data.get("doctorName") or request.args.get("doctorName")
    }


# =========================
# VALIDAR PERMISOS PARA EDITAR/ELIMINAR
# =========================
def validate_manage_permission(appointment, actor):
    role = actor.get("role")
    actor_user = actor.get("user")
    actor_doctor = actor.get("doctorName")

    if role == "admin":
        return None

    if role == "doctor":
        if actor_doctor and appointment["doctor"] == actor_doctor:
            return None
        return "No puedes modificar citas de otro doctor."

    # Usuario normal: solo puede modificar sus propias citas y hasta 1 hora antes
    if not actor_user or appointment["user"] != actor_user:
        return "Solo puedes editar o eliminar las citas que tú agendaste."

    if not can_user_modify(appointment["date"]):
        return "Solo puedes editar o eliminar la cita hasta máximo 1 hora antes de que comience."

    return None


# =========================
# CREAR CITA
# =========================
@appointments_bp.route('/appointments', methods=['POST'])
def create_appointment():
    data = request.json

    if not data or not data.get('user') or not data.get('doctor') or not data.get('specialty') or not data.get('date'):
        return jsonify({"error": "Faltan datos"}), 400

    user_email = data['user'].strip().lower()
    new_date = parse_date(data['date'])

    if not new_date:
        return jsonify({"error": "Formato de fecha inválido"}), 400

    conn = get_connection()
    cursor = conn.cursor()

    try:
        cursor.execute("SELECT fine_pending FROM users WHERE email=?", (user_email,))
        user = cursor.fetchone()

        if not user:
            return jsonify({"error": "Usuario no encontrado"}), 404

        if user["fine_pending"]:
            return jsonify({
                "error": "No puedes agendar nuevas citas porque tienes una multa pendiente de $70.000 por no haber asistido.",
                "finePending": True,
                "fineAmount": 70000
            }), 403

        conflict = doctor_has_conflict(conn, data['doctor'], new_date)

        if conflict:
            return jsonify({
                "error": f"El doctor {data['doctor']} ya tiene una cita cercana a esa hora. Debe existir al menos 1 hora de diferencia."
            }), 409

        cursor.execute("""
        INSERT INTO appointments (user, doctor, specialty, date, status, attendance)
        VALUES (?, ?, ?, ?, 'ACTIVA', 'PENDIENTE')
        """, (user_email, data['doctor'], data['specialty'], data['date']))

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
    user_filter = request.args.get("user")
    conn = get_connection()
    cursor = conn.cursor()

    if user_filter:
        cursor.execute("""
            SELECT * FROM appointments
            WHERE status='ACTIVA' AND user=?
            ORDER BY date ASC
        """, (user_filter.strip().lower(),))
    else:
        cursor.execute("SELECT * FROM appointments WHERE status='ACTIVA' ORDER BY date ASC")

    rows = cursor.fetchall()
    conn.close()

    return jsonify([appointment_to_dict(r) for r in rows])


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
    actor = get_actor_data()
    conn = get_connection()
    cursor = conn.cursor()

    try:
        cursor.execute("SELECT * FROM appointments WHERE id=?", (id,))
        appointment = cursor.fetchone()

        if not appointment:
            return jsonify({"error": "Cita no encontrada"}), 404

        permission_error = validate_manage_permission(appointment, actor)
        if permission_error:
            return jsonify({"error": permission_error}), 403

        cursor.execute("UPDATE appointments SET status='ELIMINADA' WHERE id=?", (id,))

        conn.commit()
        return jsonify({"message": "Cita eliminada"})

    except Exception as e:
        print(e)
        return jsonify({"error": "Error al eliminar cita"}), 500

    finally:
        conn.close()


# =========================
# ADMIN - VER TODAS LAS CITAS
# =========================
@appointments_bp.route('/admin/appointments', methods=['GET'])
def admin_all():
    conn = get_connection()
    cursor = conn.cursor()

    cursor.execute("SELECT * FROM appointments ORDER BY date DESC")
    rows = cursor.fetchall()

    conn.close()

    return jsonify([appointment_to_dict(r) for r in rows])


# =========================
# DOCTOR - VER CITAS ASIGNADAS
# =========================
@appointments_bp.route('/doctor/appointments/<path:doctor_name>', methods=['GET'])
def doctor_appointments(doctor_name):
    conn = get_connection()
    cursor = conn.cursor()

    cursor.execute("""
        SELECT * FROM appointments
        WHERE doctor=? AND status='ACTIVA'
        ORDER BY date ASC
    """, (doctor_name,))

    rows = cursor.fetchall()
    conn.close()

    return jsonify([appointment_to_dict(r) for r in rows])


# =========================
# DOCTOR - MARCAR ASISTENCIA
# =========================
@appointments_bp.route('/doctor/appointments/<int:id>/attendance', methods=['PUT'])
def update_attendance(id):
    data = request.json

    if not data or not data.get("attendance"):
        return jsonify({"error": "Falta el estado de asistencia"}), 400

    attendance = data["attendance"]
    doctor_name = data.get("doctorName")

    if attendance not in ["ASISTIO", "NO_ASISTIO", "PENDIENTE"]:
        return jsonify({"error": "Estado de asistencia inválido"}), 400

    conn = get_connection()
    cursor = conn.cursor()

    try:
        cursor.execute("SELECT * FROM appointments WHERE id=?", (id,))
        appointment = cursor.fetchone()

        if not appointment:
            return jsonify({"error": "Cita no encontrada"}), 404

        if doctor_name and appointment["doctor"] != doctor_name:
            return jsonify({"error": "No puedes marcar asistencia de citas de otro doctor."}), 403

        cursor.execute("UPDATE appointments SET attendance=? WHERE id=?", (attendance, id))

        if attendance == "NO_ASISTIO":
            cursor.execute("UPDATE users SET fine_pending=1 WHERE email=?", (appointment["user"],))

        conn.commit()

        if attendance == "NO_ASISTIO":
            return jsonify({
                "message": "Paciente marcado como NO asistió. Se activó multa de $70.000.",
                "fineAmount": 70000
            })

        return jsonify({"message": "Asistencia actualizada"})

    except Exception as e:
        print(e)
        return jsonify({"error": "Error al actualizar asistencia"}), 500

    finally:
        conn.close()


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

    actor = get_actor_data()
    conn = get_connection()
    cursor = conn.cursor()

    try:
        cursor.execute("SELECT * FROM appointments WHERE id=?", (id,))
        appointment = cursor.fetchone()

        if not appointment:
            return jsonify({"error": "Cita no encontrada"}), 404

        permission_error = validate_manage_permission(appointment, actor)
        if permission_error:
            return jsonify({"error": permission_error}), 403

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

        return jsonify({"message": "Cita actualizada"})

    except Exception as e:
        print(e)
        return jsonify({"error": "Error al actualizar cita"}), 500

    finally:
        conn.close()
