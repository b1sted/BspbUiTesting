properties([githubProjectProperty(displayName: '', projectUrlStr: 'https://github.com/b1sted/BspbUiTesting/'), pipelineTriggers([GenericTrigger(causeString: 'Generic Cause', genericVariables: [[defaultValue: '', key: 'ref', regexpFilter: '', value: '$.ref']], regexpFilterExpression: 'refs/heads/main', regexpFilterText: '$ref', token: 'bspbuitesting', tokenCredentialId: '')])])

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
}
