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
            steps {
                bat '''
                    curl.exe -s -X POST -H "Authorization: token %GITHUB_TOKEN_PSW%" -H "Accept: application/vnd.github.v3+json" https://api.github.com/repos/%REPO_OWNER%/%REPO_NAME%/statuses/%GIT_COMMIT% -d "{\\"state\\": \\"pending\\", \\"target_url\\": \\"%BUILD_URL%\\", \\"description\\": \\"Jenkins esta ejecutando las pruebas...\\", \\"context\\": \\"CI / Jenkins Backend\\"}"
                '''
            }
        }

        stage('2. Preparar Entorno') {
            steps {
                withCredentials([file(credentialsId: 'acuamont-env-file', variable: 'ENV_FILE')]) {
                    script {
                        readFile(ENV_FILE).eachLine { line ->
                            def trimmed = line.trim()
                            if (trimmed && !trimmed.startsWith('#') && trimmed.contains('=')) {
                                def sepIndex = trimmed.indexOf('=')
                                def key = trimmed.substring(0, sepIndex).trim()
                                def value = trimmed.substring(sepIndex + 1).trim()
                                env[key] = value
                            }
                        }
                    }
                }
            }
        }

        stage('3. Instalar Dependencias') {
            steps {
                dir('Backend') {
                    bat 'mvnw.cmd dependency:resolve -B'
                }
            }
        }

        stage('4. Compilar') {
            steps {
                dir('Backend') {
                    bat 'mvnw.cmd compile -B'
                }
            }
        }

        stage('5. Ejecutar Tests') {
            steps {
                dir('Backend') {
                    bat 'mvnw.cmd test -B'
                }
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: 'Backend/target/surefire-reports/*.xml'
                }
            }
        }

        stage('6. Empaquetar') {
            steps {
                dir('Backend') {
                    bat 'mvnw.cmd package -DskipTests -B'
                }
            }
        }

        stage('7. Archivar Artifact') {
            steps {
                archiveArtifacts artifacts: 'Backend/target/*.jar', fingerprint: true
            }
        }
    }

    post {
        success {
            bat '''
                curl.exe -s -X POST -H "Authorization: token %GITHUB_TOKEN_PSW%" -H "Accept: application/vnd.github.v3+json" https://api.github.com/repos/%REPO_OWNER%/%REPO_NAME%/statuses/%GIT_COMMIT% -d "{\\"state\\": \\"success\\", \\"target_url\\": \\"%BUILD_URL%\\", \\"description\\": \\"Tests exitosos\\", \\"context\\": \\"CI / Jenkins Backend\\"}"
            '''
            script {
                if (env.CHANGE_ID) {
                    bat '''
                        curl.exe -s -X POST -H "Authorization: token %GITHUB_TOKEN_PSW%" -H "Accept: application/vnd.github.v3+json" https://api.github.com/repos/%REPO_OWNER%/%REPO_NAME%/issues/%CHANGE_ID%/comments -d "{\\"body\\": \\"### CI Exitoso\\\\n- **Estado:** PASÓ\\\\n- **Rama:** %BRANCH_NAME%\\\\n\\\\nTodas las pruebas pasaron correctamente.\\"}"
                    '''
                }
            }
        }

        failure {
            bat '''
                curl.exe -s -X POST -H "Authorization: token %GITHUB_TOKEN_PSW%" -H "Accept: application/vnd.github.v3+json" https://api.github.com/repos/%REPO_OWNER%/%REPO_NAME%/statuses/%GIT_COMMIT% -d "{\\"state\\": \\"failure\\", \\"target_url\\": \\"%BUILD_URL%\\", \\"description\\": \\"Tests fallaron\\", \\"context\\": \\"CI / Jenkins Backend\\"}"
            '''
            script {
                if (env.CHANGE_ID) {
                    bat '''
                        curl.exe -s -X POST -H "Authorization: token %GITHUB_TOKEN_PSW%" -H "Accept: application/vnd.github.v3+json" https://api.github.com/repos/%REPO_OWNER%/%REPO_NAME%/issues/%CHANGE_ID%/comments -d "{\\"body\\": \\"### Fallo en CI\\\\n- **Estado:** FALLIDO\\\\n- **Rama:** %BRANCH_NAME%\\\\n\\\\nRevisar logs en Jenkins.\\"}"
                    '''
                }
            }
        }
    }
}
