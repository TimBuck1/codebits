parameters {
    string(name: 'IMAGE_LABEL', defaultValue: 'latest', description: 'Label of the Docker image to use (e.g., version or tag)')
}


version: '3'
services:
  app:
    image: your-docker-registry/your-app:${IMAGE_LABEL}
    ports:
      - "8080:8080"
    # Add other configuration options as needed


pipeline {
    agent any
    
    parameters {
        string(name: 'IMAGE_LABEL', defaultValue: 'latest', description: 'Label of the Docker image to use (e.g., version or tag)')
    }
    
    stages {
        stage('Deploy') {
            steps {
                script {
                    // Set environment variable for docker-compose
                    env.IMAGE_LABEL = params.IMAGE_LABEL
                    
                    // Bring up containers using docker-compose
                    sh 'docker-compose up -d'
                }
            }
        }
    }
}
