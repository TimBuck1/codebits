pipeline {
    agent any

    environment {
        COMPOSE_PROJECT_NAME = 'my_project'
        JAVA_OPTS = '-Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=9010 -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false'
    }

    stages {
        stage('Checkout') {
            steps {
                git 'https://your-repo-url.git'
            }
        }

        stage('Build and Deploy') {
            steps {
                script {
                    // Clean up any previous containers
                    sh 'docker-compose down'
                    
                    // Build and bring up the application with JMX enabled
                    sh 'docker-compose up --build -d'
                    
                    // Wait for the services to be up and running
                    sleep(time: 30, unit: 'SECONDS')
                }
            }
        }

        stage('Run Performance Tests') {
            steps {
                script {
                    // Run the performance tests
                    sh 'docker-compose run --rm performance-tester'
                }
            }
        }

        stage('Teardown') {
            steps {
                // Bring down the application
                sh 'docker-compose down'
            }
        }
    }

    post {
        always {
            // Archive logs or other artifacts
            archiveArtifacts artifacts: 'logs/**/*.log', allowEmptyArchive: true
            
            // Print performance tester logs
            script {
                sh 'docker-compose logs performance-tester'
            }
        }
    }
}
