#!/bin/bash
# setup.sh - Quick setup script


export M2_HOME=E:\apache-maven-3.9.12-bin
export PATH=$M2_HOME/bin:$PATH
export JAVA_HOME=E:\jdk-25.0.1.8-hotspot 

#echo "Setting up Medical Quiz Flask Frontend..."

# Create project structure
#mkdir -p medical-quiz-frontend/templates
#cd medical-quiz-frontend

# Create requirements.txt
#cat > requirements.txt << 'EOF'
#Flask==3.0.0
#requests==2.31.0
#python-dotenv==1.0.0
#EOF

# Create .env
#cat > .env << 'EOF'
#SECRET_KEY=your-secret-key-change-this-in-production
#JAVA_API_URL=http://localhost:8080/api
#EOF

#echo "✓ Created requirements.txt and .env"
#echo ""
#echo "Next steps:"
#echo "1. Copy app.py into this directory"
#echo "2. Copy all HTML templates into the templates/ folder"
#echo "3. Run: pip install -r requirements.txt"
#echo "4. Run: python app.py"
#echo ""
#echo "Make sure your Java backend is running on port 8080!"