pipeline {
    agent any

    tools {
        jdk 'Java21'
    }

    environment {
        GITHUB_TOKEN = credentials('github-credential')
        REPO_OWNER   = 'Herramientas-Group'
        REPO_NAME    = 'Acuamont'
    }

    stages {
        stage('1. Inicializar Estado') {
            when { changeset "**" }
            steps {
                bat '''
                    curl.exe -s -X POST -H "Authorization: token %GITHUB_TOKEN_PSW%" -H "Accept: application/vnd.github.v3+json" https://api.github.com/repos/%REPO_OWNER%/%REPO_NAME%/statuses/%GIT_COMMIT% -d "{\\"state\\": \\"pending\\", \\"target_url\\": \\"%BUILD_URL%\\", \\"description\\": \\"Jenkins esta ejecutando las pruebas...\\", \\"context\\": \\"CI - Jenkins\\"}"
                '''
            }
        }

        stage('2. Backend: Dependencias') {
            when { anyOf { changeset "Backend/**"; changeset "pom.xml"; changeset "Jenkinsfile" } }
            steps {
                dir('Backend') {
                    bat 'mvnw.cmd dependency:resolve -B'
                }
            }
        }

        stage('3. Backend: Compilar') {
            when { anyOf { changeset "Backend/**"; changeset "pom.xml"; changeset "Jenkinsfile" } }
            steps {
                dir('Backend') {
                    bat 'mvnw.cmd compile -B'
                }
            }
        }

        stage('4. Backend: Ejecutar Tests') {
            when { anyOf { changeset "Backend/**"; changeset "pom.xml"; changeset "Jenkinsfile" } }
            steps {
                catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {
                    dir('Backend') {
                        bat 'mvnw.cmd test -B'
                    }
                }
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: 'Backend/target/surefire-reports/*.xml'
                }
            }
        }

        stage('5. Frontend: Dependencias') {
            when { anyOf { changeset "Frontend/**"; changeset "package.json"; changeset "Jenkinsfile" } }
            steps {
                dir('Frontend') {
                    bat 'npm install'
                }
            }
        }

        stage('6. Frontend: Ejecutar Tests') {
            when { anyOf { changeset "Frontend/**"; changeset "package.json"; changeset "Jenkinsfile" } }
            steps {
                catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {
                    dir('Frontend') {
                        bat 'npm run test'
                    }
                }
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: 'Frontend/test-results/junit.xml'
                }
            }
        }

    }

    post {
        success {
            bat '''
                curl.exe -s -X POST -H "Authorization: token %GITHUB_TOKEN_PSW%" -H "Accept: application/vnd.github.v3+json" https://api.github.com/repos/%REPO_OWNER%/%REPO_NAME%/statuses/%GIT_COMMIT% -d "{\\"state\\": \\"success\\", \\"target_url\\": \\"%BUILD_URL%\\", \\"description\\": \\"Tests exitosos\\", \\"context\\": \\"CI - Jenkins\\"}"
            '''
            script {
                if (env.CHANGE_ID) {
                    def comment = """### CI Exitoso
- **Estado:** PASÓ
- **Rama:** ${env.BRANCH_NAME}

[Ver build en Jenkins](${env.BUILD_URL})"""

                    def payload = groovy.json.JsonOutput.toJson([body: comment])
                    writeFile file: 'ci-comment.json', text: payload

                    bat '''
                        curl.exe -s -X POST -H "Authorization: token %GITHUB_TOKEN_PSW%" -H "Accept: application/vnd.github.v3+json" https://api.github.com/repos/%REPO_OWNER%/%REPO_NAME%/issues/%CHANGE_ID%/comments -d @ci-comment.json
                    '''
                    bat 'if exist ci-comment.json del ci-comment.json'
                }
            }
        }

        unstable {
            bat '''
                curl.exe -s -X POST -H "Authorization: token %GITHUB_TOKEN_PSW%" -H "Accept: application/vnd.github.v3+json" https://api.github.com/repos/%REPO_OWNER%/%REPO_NAME%/statuses/%GIT_COMMIT% -d "{\\"state\\": \\"failure\\", \\"target_url\\": \\"%BUILD_URL%\\", \\"description\\": \\"Tests fallaron\\", \\"context\\": \\"CI - Jenkins\\"}"
            '''
            script {
                if (env.CHANGE_ID) {
                    def comment = """### Fallo en CI
- **Estado:** FALLÓ
- **Rama:** ${env.BRANCH_NAME}

[Ver logs en Jenkins](${env.BUILD_URL}console)"""

                    def payload = groovy.json.JsonOutput.toJson([body: comment])
                    writeFile file: 'ci-comment.json', text: payload

                    bat '''
                        curl.exe -s -X POST -H "Authorization: token %GITHUB_TOKEN_PSW%" -H "Accept: application/vnd.github.v3+json" https://api.github.com/repos/%REPO_OWNER%/%REPO_NAME%/issues/%CHANGE_ID%/comments -d @ci-comment.json
                    '''
                    bat 'if exist ci-comment.json del ci-comment.json'
                }
            }
        }

        failure {
            bat '''
                curl.exe -s -X POST -H "Authorization: token %GITHUB_TOKEN_PSW%" -H "Accept: application/vnd.github.v3+json" https://api.github.com/repos/%REPO_OWNER%/%REPO_NAME%/statuses/%GIT_COMMIT% -d "{\\"state\\": \\"error\\", \\"target_url\\": \\"%BUILD_URL%\\", \\"description\\": \\"Error en la ejecucion del pipeline\\", \\"context\\": \\"CI - Jenkins\\"}"
            '''
        }
    }
}
