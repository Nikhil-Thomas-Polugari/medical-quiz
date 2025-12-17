# app.py - Flask Frontend for Medical Quiz
from flask import Flask, render_template, request, redirect, url_for, session, flash, jsonify
import requests
from functools import wraps
import os

app = Flask(__name__)
app.secret_key = os.environ.get('SECRET_KEY', 'your-secret-key-change-in-production')

# Java Backend Configuration
JAVA_API_URL = os.environ.get('JAVA_API_URL', 'http://localhost:8080/api')

# =============================================================================
# Helper Functions
# =============================================================================

def login_required(f):
    """Decorator to require login for routes"""
    @wraps(f)
    def decorated_function(*args, **kwargs):
        if 'token' not in session:
            flash('Please log in to access this page.', 'warning')
            return redirect(url_for('login'))
        return f(*args, **kwargs)
    return decorated_function

def get_headers():
    """Get authorization headers with JWT token"""
    token = session.get('token')
    if token:
        return {
            'Authorization': f'Bearer {token}',
            'Content-Type': 'application/json'
        }
    return {'Content-Type': 'application/json'}

# =============================================================================
# Routes
# =============================================================================

@app.route('/')
def index():
    """Home page"""
    if 'token' in session:
        return redirect(url_for('dashboard'))
    return render_template('index.html')

@app.route('/register', methods=['GET', 'POST'])
def register():
    """User registration"""
    if request.method == 'POST':
        data = {
            'username': request.form['username'],
            'email': request.form['email'],
            'password': request.form['password']
        }
        
        try:
            response = requests.post(
                f'{JAVA_API_URL}/auth/register',
                json=data,
                headers={'Content-Type': 'application/json'}
            )
            
            if response.status_code == 200:
                result = response.json()
                session['token'] = result['token']
                session['username'] = result['username']
                flash('Registration successful!', 'success')
                return redirect(url_for('dashboard'))
            else:
                error_msg = response.json() if response.headers.get('content-type') == 'application/json' else response.text
                flash(f'Registration failed: {error_msg}', 'danger')
        except requests.exceptions.RequestException as e:
            flash(f'Connection error: {str(e)}', 'danger')
    
    return render_template('register.html')

@app.route('/login', methods=['GET', 'POST'])
def login():
    """User login"""
    if request.method == 'POST':
        data = {
            'username': request.form['username'],
            'password': request.form['password']
        }
        
        try:
            response = requests.post(
                f'{JAVA_API_URL}/auth/login',
                json=data,
                headers={'Content-Type': 'application/json'}
            )
            
            if response.status_code == 200:
                result = response.json()
                session['token'] = result['token']
                session['username'] = result['username']
                flash('Login successful!', 'success')
                return redirect(url_for('dashboard'))
            else:
                flash('Invalid username or password', 'danger')
        except requests.exceptions.RequestException as e:
            flash(f'Connection error: {str(e)}', 'danger')
    
    return render_template('login.html')

@app.route('/logout')
def logout():
    """User logout"""
    session.clear()
    flash('You have been logged out.', 'info')
    return redirect(url_for('index'))

@app.route('/dashboard')
@login_required
def dashboard():
    """User dashboard"""
    try:
        # Get user score
        response = requests.get(
            f'{JAVA_API_URL}/questions/score',
            headers=get_headers()
        )
        
        if response.status_code == 200:
            score_data = response.json()
            return render_template('dashboard.html', 
                                 username=session.get('username'),
                                 score=score_data)
        else:
            flash('Failed to load score data', 'warning')
            return render_template('dashboard.html', 
                                 username=session.get('username'),
                                 score=None)
    except requests.exceptions.RequestException as e:
        flash(f'Connection error: {str(e)}', 'danger')
        return render_template('dashboard.html', 
                             username=session.get('username'),
                             score=None)

@app.route('/quiz')
@login_required
def quiz():
    """Quiz page - get random question"""
    try:
        response = requests.get(
            f'{JAVA_API_URL}/questions/random',
            headers=get_headers()
        )
        
        if response.status_code == 200:
            question_data = response.json()
            
            # Check if quiz is completed
            if isinstance(question_data, str):
                flash(question_data, 'info')
                return redirect(url_for('results'))
            
            return render_template('quiz.html', question=question_data)
        else:
            flash('Failed to load question', 'danger')
            return redirect(url_for('dashboard'))
    except requests.exceptions.RequestException as e:
        flash(f'Connection error: {str(e)}', 'danger')
        return redirect(url_for('dashboard'))

@app.route('/submit-answer', methods=['POST'])
@login_required
def submit_answer():
    """Submit answer to question"""
    data = {
        'questionId': request.form['questionId'],
        'answer': request.form['answer']
    }
    
    try:
        response = requests.post(
            f'{JAVA_API_URL}/questions/answer',
            json=data,
            headers=get_headers()
        )
        
        if response.status_code == 200:
            result = response.json()
            return render_template('answer_result.html', result=result)
        else:
            flash('Failed to submit answer', 'danger')
            return redirect(url_for('quiz'))
    except requests.exceptions.RequestException as e:
        flash(f'Connection error: {str(e)}', 'danger')
        return redirect(url_for('quiz'))

@app.route('/results')
@login_required
def results():
    """Final results page"""
    try:
        response = requests.get(
            f'{JAVA_API_URL}/questions/score',
            headers=get_headers()
        )
        
        if response.status_code == 200:
            score_data = response.json()
            return render_template('results.html', score=score_data)
        else:
            flash('Failed to load results', 'danger')
            return redirect(url_for('dashboard'))
    except requests.exceptions.RequestException as e:
        flash(f'Connection error: {str(e)}', 'danger')
        return redirect(url_for('dashboard'))

# =============================================================================
# API Routes (Optional - for AJAX calls)
# =============================================================================

@app.route('/api/check-connection')
def check_connection():
    """Check if Java backend is accessible"""
    try:
        response = requests.get(f'{JAVA_API_URL}/auth/health', timeout=5)
        return jsonify({'status': 'connected', 'backend_status': response.status_code})
    except:
        return jsonify({'status': 'disconnected'}), 503

# =============================================================================
# Error Handlers
# =============================================================================

@app.errorhandler(404)
def not_found(e):
    return render_template('404.html'), 404

@app.errorhandler(500)
def internal_error(e):
    return render_template('500.html'), 500

# =============================================================================
# Main
# =============================================================================

if __name__ == '__main__':
    # Development server
    app.run(debug=True, host='0.0.0.0', port=5000)