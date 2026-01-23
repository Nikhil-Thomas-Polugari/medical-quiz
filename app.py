# app.py - Simplified Flask Server for Medical Quiz
from flask import Flask, render_template, send_from_directory
import os

app = Flask(__name__)

# Configuration
JAVA_API_URL = os.environ.get('JAVA_API_URL', 'http://localhost:8080/api')
FLASK_HOST = os.environ.get('FLASK_HOST', '0.0.0.0')
FLASK_PORT = int(os.environ.get('FLASK_PORT', '5000'))

@app.route('/')
def index():
    """Serve the main quiz application"""
    return render_template('index.html', api_url=JAVA_API_URL)

@app.route('/qr')
def qr_page():
    """Serve the QR code page (Java backend generates the actual QR)"""
    return render_template('qr_code.html', api_url=JAVA_API_URL)

@app.route('/static/<path:filename>')
def serve_static(filename):
    """Serve static files"""
    return send_from_directory('static', filename)

@app.errorhandler(404)
def not_found(e):
    return "<h1>404 - Page Not Found</h1>", 404

@app.errorhandler(500)
def internal_error(e):
    return "<h1>500 - Internal Server Error</h1>", 500

if __name__ == '__main__':
    print(f"""
    ╔═══════════════════════════════════════════╗
    ║     Medical Quiz Application Started      ║
    ╠═══════════════════════════════════════════╣
    ║  Flask Server: http://{FLASK_HOST}:{FLASK_PORT}         ║
    ║  QR Code:      http://{FLASK_HOST}:{FLASK_PORT}/qr      ║
    ║  Java API:     {JAVA_API_URL}              ║
    ╚═══════════════════════════════════════════╝
    """)
    
    app.run(debug=True, host=FLASK_HOST, port=FLASK_PORT)