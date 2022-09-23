#!/usr/bin/env groovy

pipeline {
    agent any
    tools {
        jdk 'jdk17'
        maven 'maven3'
    }

    environment {
        imageTag = 'docker.io/suayb/spring-native-reactive-graphql-example/latest'
        registryCredential = 'dockerhub-registry'
    }

    stages {
        stage('checkout scm') {
            steps {
                checkout scm
            }
        }

        stage('check java') {
            steps {
                sh 'java -version'
            }
        }

        stage('clean') {
            steps {
                sh 'mvn -ntp clean'
            }
        }

        stage('test') {
            steps {
                script {
                    try {
                        sh 'mvn -ntp test'
                    } catch (err) {
                        throw err
                    }
                }
            }
        }

        stage('packaging') {
            steps {
                sh 'mvn -ntp package -DskipTests'
            }
        }

        stage('quality analysis') {
            steps {
                withSonarQubeEnv('sonar') {
                    sh 'mvn -ntp -Psonar initialize sonar:sonar'
                }
            }
        }

        stage("Publish Docker") {
            stage("Build native image") {
                steps {
                    withCredentials([usernamePassword(credentialsId: 'docker-registry', passwordVariable: 'DOCKER_REGISTRY_PWD', usernameVariable: 'DOCKER_REGISTRY_USER')]) {
                        sh 'mvn -ntp -Pprod spring-boot:build-image -DskipTests'
                    }
                }
            }
        }
    }

    post {
        always {
            archiveArtifacts artifacts: '**/target/*.jar', fingerprint: true
        }
    }
}