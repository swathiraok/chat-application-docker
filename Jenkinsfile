// Jenkinsfile for Chat Application CI/CD

// Define the agent where the pipeline will run.
// 'any' means Jenkins will use any available agent.
agent any

// Define environment variables.
// The DOCKER_USERNAME will be used to tag images.
environment {
    DOCKER_USERNAME = 'swathiraok' // <<< IMPORTANT: Replace with your Docker Hub username
}

// Define stages for the pipeline.
stages {
    stage('Checkout Code') {
        steps {
            // Checkout the Git repository.
            // Jenkins will automatically detect the repository linked to the job.
            git branch: 'main', url: 'https://github.com/swathiraok/chat-application-docker.git' // <<< IMPORTANT: Update with your GitHub repo URL
        }
    }

    stage('Build Backend Docker Image') {
        steps {
            script {
                // Change directory to the backend project.
                dir('backend') {
                    // Build the Docker image.
                    // The image is tagged with the Docker Hub username and 'latest'.
                    sh "docker build -t ${DOCKER_USERNAME}/chat-backend:latest ."
                }
            }
        }
    }

    stage('Push Backend Docker Image') {
        steps {
            // Use the 'withDockerRegistry' step to authenticate with Docker Hub using defined credentials.
            // The credentials ID must match what you configured in Jenkins (e.g., 'dockerhub-credentials').
            withDockerRegistry(credentialsId: 'dockerhub-credentials', url: 'https://index.docker.io/v1/') {
                script {
                    // Push the tagged Docker image to Docker Hub.
                    sh "docker push ${DOCKER_USERNAME}/chat-backend:latest"
                }
            }
        }
    }

    stage('Build Frontend Docker Image') {
        steps {
            script {
                // Change directory to the frontend project.
                dir('frontend/chat-frontend') {
                    // Build the Docker image.
                    sh "docker build -t ${DOCKER_USERNAME}/chat-frontend:latest ."
                }
            }
        }
    }

    stage('Push Frontend Docker Image') {
        steps {
            withDockerRegistry(credentialsId: 'dockerhub-credentials', url: 'https://index.docker.io/v1/') {
                script {
                    // Push the tagged Docker image to Docker Hub.
                    sh "docker push ${DOCKER_USERNAME}/chat-frontend:latest"
                }
            }
        }
    }
}
