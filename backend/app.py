from flask import Flask
from flask_cors import CORS
from routes.users import users_bp
from routes.appointments import appointments_bp
from database import init_db

app = Flask(__name__)

# CORS configurado
CORS(app, resources={r"/api/*": {"origins": "*"}})

# Inicializar BD automáticamente
init_db()

# Blueprints con prefijo
app.register_blueprint(users_bp, url_prefix="/api")
app.register_blueprint(appointments_bp, url_prefix="/api")

if __name__ == "__main__":
    app.run(debug=True)