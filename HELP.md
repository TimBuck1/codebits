 script {
                echo "Cleaning up Docker environment..."
                sh 'docker-compose down --remove-orphans --volumes'
                sh 'docker network prune -f'
            }
