properties([
    githubProjectProperty(
        displayName: '',
        projectUrlStr: 'https://github.com/b1sted/BspbUiTesting/'
    ),

    pipelineTriggers([
        GenericTrigger(
            causeString: 'Generic Cause',
            genericVariables: [[defaultValue: '', key: 'ref', regexpFilter: '', value: '$.ref']],
            regexpFilterExpression: 'refs/heads/main',
            regexpFilterText: '$ref',
            token: 'bspbuitesting',
            tokenCredentialId: ''
        )
    ])
])

pipeline {
    agent {
        docker {
            image 'selenium/standalone-chrome:149.0'
            args '--shm-size="128MiB" -e HOME=/tmp'
        }
    }

    environment {
        GRADLE_USER_HOME = "${WORKSPACE}/.gradle"
    }

    tools {
        allure 'allure-cli'
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main',
                    url: 'https://github.com/b1sted/BspbUiTesting'
            }
        }

        stage('Set Starter Status') {
            steps {
                setGitHubStatus('PENDING', 'Запуск Selenium тестов в headless-режиме...')
            }
        }

        stage('Test') {
            steps {
                catchError(buildResult: 'FAILURE', stageResult: 'FAILURE') {
                    sh 'chmod +x gradlew'
                    sh './gradlew clean test'
                }

                allure includeProperties: false, results: [[path: 'build/allure-results']]
            }
        }
    }

    post {
        success {
            setGitHubStatus('SUCCESS', 'Все UI-тесты пройдены успешно.')
        }

        failure {
            setGitHubStatus('FAILURE', 'Один или несколько Selenium тестов завершились ошибкой.')
        }
    }
}

def setGitHubStatus(String state, String message) {
    step([
        $class: 'GitHubCommitStatusSetter',
        reposSource: [
            $class: 'ManuallyEnteredRepositorySource',
            url: 'https://github.com/b1sted/BspbUiTesting'
        ],
        contextSource: [
            $class: 'ManuallyEnteredCommitContextSource',
            context: 'Jenkins CI'
        ],
        statusResultSource: [
            $class: 'ConditionalStatusResultSource',
            results: [[$class: 'AnyBuildResult', state: state, message: message]]
        ]
    ])
}