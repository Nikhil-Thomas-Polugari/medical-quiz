# app.py - Simplified Flask Server for Medical Quiz
from flask import Flask, render_template, send_from_directory
import qrcode
from io import BytesIO
import base64
import os

app = Flask(__name__)

# Configuration
JAVA_API_URL = os.environ.get('JAVA_API_URL', 'http://localhost:8080/api')
FLASK_HOST = os.environ.get('FLASK_HOST', '0.0.0.0')
FLASK_PORT = int(os.environ.get('FLASK_PORT', '5000'))

# =============================================================================
# Routes
# =============================================================================

@app.route('/')
def index():
    """Serve the main quiz application"""
    return render_template('index.html', api_url=JAVA_API_URL)

@app.route('/qr')
def generate_qr():
    """Generate QR code for the quiz URL"""
    # Get the server URL
    server_url = f"http://{FLASK_HOST}:{FLASK_PORT}"
    
    # Generate QR code
    qr = qrcode.QRCode(
        version=1,
        error_correction=qrcode.constants.ERROR_CORRECT_L,
        box_size=10,
        border=4,
    )
    qr.add_data(server_url)
    qr.make(fit=True)
    
    # Create image
    img = qr.make_image(fill_color="black", back_color="white")
    
    # Convert to base64 for display
    buffered = BytesIO()
    img.save(buffered, format="PNG")
    img_str = base64.b64encode(buffered.getvalue()).decode()
    
    return render_template('qr_code.html', 
                         qr_image=img_str, 
                         quiz_url=server_url)

@app.route('/static/<path:filename>')
def serve_static(filename):
    """Serve static files"""
    return send_from_directory('static', filename)

# =============================================================================
# Error Handlers
# =============================================================================

@app.errorhandler(404)
def not_found(e):
    return "<h1>404 - Page Not Found</h1>", 404

@app.errorhandler(500)
def internal_error(e):
    return "<h1>500 - Internal Server Error</h1>", 500

# =============================================================================
# Main
# =============================================================================

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
    
    # Development server
    app.run(debug=True, host=FLASK_HOST, port=FLASK_PORT)