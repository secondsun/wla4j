pipeline {
    agent {
        docker {
            image 'arm64v8/maven:adoptopenjdk' 
        }
    }
    stages {
        stage('Build') { 
            steps {
                sh 'mvn clean package' 
            }
        }
    }
}
