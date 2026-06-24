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
                bat """
                    curl.exe -s -X POST -H "Authorization: token %GITHUB_TOKEN%" -H "Accept: application/vnd.github.v3+json" https://api.github.com/repos/%REPO_OWNER%/%REPO_NAME%/statuses/%GIT_COMMIT% -d "{\\"state\\": \\"pending\\", \\"target_url\\": \\"%BUILD_URL%\\", \\"description\\": \\"Jenkins esta ejecutando las pruebas...\\", \\"context\\": \\"CI / Jenkins Backend\\"}"
                """
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
            bat """
                curl.exe -s -X POST -H "Authorization: token %GITHUB_TOKEN%" -H "Accept: application/vnd.github.v3+json" https://api.github.com/repos/%REPO_OWNER%/%REPO_NAME%/statuses/%GIT_COMMIT% -d "{\\"state\\": \\"success\\", \\"target_url\\": \\"%BUILD_URL%\\", \\"description\\": \\"Todos los tests pasaron exitosamente\\", \\"context\\": \\"CI / Jenkins Backend\\"}"
            """
            script {
                if (env.CHANGE_ID) {
                    bat """
                        curl.exe -s -X POST -H "Authorization: token %GITHUB_TOKEN%" -H "Accept: application/vnd.github.v3+json" https://api.github.com/repos/%REPO_OWNER%/%REPO_NAME%/issues/%CHANGE_ID%/comments -d "{\\"body\\": \\"### Reporte de Integracion Continua\\\\n- **Estado:** EXITOSO\\\\n- **Rama:** %BRANCH_NAME%\\\\n\\\\nTodas las pruebas unitarias y reglas de arquitectura de ArchUnit pasaron correctamente. El codigo es seguro para ser fusionado.\\"}"
                    """
                    bat """
                        curl.exe -s -X POST -H "Authorization: token %GITHUB_TOKEN%" -H "Accept: application/vnd.github.v3+json" https://api.github.com/repos/%REPO_OWNER%/%REPO_NAME%/pulls/%CHANGE_ID%/reviews -d "{\\"event\\": \\"APPROVE\\", \\"body\\": \\"Todos los tests pasaron correctamente. Codigo listo para merge.\\"}"
                    """
                }
            }
        }

        failure {
            bat """
                curl.exe -s -X POST -H "Authorization: token %GITHUB_TOKEN%" -H "Accept: application/vnd.github.v3+json" https://api.github.com/repos/%REPO_OWNER%/%REPO_NAME%/statuses/%GIT_COMMIT% -d "{\\"state\\": \\"failure\\", \\"target_url\\": \\"%BUILD_URL%\\", \\"description\\": \\"El build o los tests fallaron\\", \\"context\\": \\"CI / Jenkins Backend\\"}"
            """
            script {
                if (env.CHANGE_ID) {
                    bat """
                        curl.exe -s -X POST -H "Authorization: token %GITHUB_TOKEN%" -H "Accept: application/vnd.github.v3+json" https://api.github.com/repos/%REPO_OWNER%/%REPO_NAME%/issues/%CHANGE_ID%/comments -d "{\\"body\\": \\"### Alerta de Fallo en CI!\\\\n- **Estado:** FALLIDO\\\\n- **Rama:** %BRANCH_NAME%\\\\n\\\\nEl pipeline se detuvo porque algunas pruebas fallaron o se rompieron las reglas de arquitectura de capas en el Backend. Por favor, revisa los logs de ejecucion en Jenkins antes de reintentar.\\"}"
                    """
                    bat """
                        curl.exe -s -X POST -H "Authorization: token %GITHUB_TOKEN%" -H "Accept: application/vnd.github.v3+json" https://api.github.com/repos/%REPO_OWNER%/%REPO_NAME%/pulls/%CHANGE_ID%/reviews -d "{\\"event\\": \\"REQUEST_CHANGES\\", \\"body\\": \\"Jenkins solicita cambios. Los tests fallaron. Revisar los logs y corregir antes de fusionar.\\"}"
                    """
                }
            }
        }
    }
}
