pipeline {
    agent any

    tools {
        jdk 'Java21'
    }

    environment {
        GITHUB_TOKEN = credentials('github-credential')
        REPO_OWNER   = 'Herramientas-Group'
        REPO_NAME    = 'Acuamont'
        VPS_IP       = credentials('vps-ip')
    }

    stages {
        stage('1. Inicializar Estado') {
            when { changeset "**" }
            steps {
                bat '''
                    curl.exe -s -X POST -H "Authorization: token %GITHUB_TOKEN_PSW%" -H "Accept: application/vnd.github.v3+json" https://api.github.com/repos/%REPO_OWNER%/%REPO_NAME%/statuses/%GIT_COMMIT% -d "{\\"state\\": \\"pending\\", \\"target_url\\": \\"%BUILD_URL%\\", \\"description\\": \\"Jenkins esta ejecutando las pruebas...\\", \\"context\\": \\"CI/CD / Jenkins\\"}"
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

        stage('5. Backend: Empaquetar') {
            when { anyOf { changeset "Backend/**"; changeset "pom.xml"; changeset "Jenkinsfile" } }
            steps {
                dir('Backend') {
                    bat 'mvnw.cmd package -DskipTests -B'
                }
            }
        }

        stage('6. Backend: Archivar') {
            when { anyOf { changeset "Backend/**"; changeset "pom.xml"; changeset "Jenkinsfile" } }
            steps {
                archiveArtifacts artifacts: 'Backend/target/*.jar', fingerprint: true
            }
        }

        stage('7. Frontend: Dependencias') {
            when { anyOf { changeset "Frontend/**"; changeset "package.json"; changeset "Jenkinsfile" } }
            steps {
                dir('Frontend') {
                    bat 'npm install'
                }
            }
        }

        stage('8. Frontend: Ejecutar Tests') {
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

        stage('9. Docker Build Backend') {
            when { branch 'main' }
            steps {
                bat 'docker build -t acuamont/acuamont-backend:%GIT_COMMIT% ./Backend'
            }
        }

        stage('10. Docker Build Frontend') {
            when { branch 'main' }
            steps {
                bat 'docker build -t acuamont/acuamont-frontend:%GIT_COMMIT% ./Frontend'
            }
        }

        stage('11. Docker Push') {
            when { branch 'main' }
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'dockerhub-credentials',
                    usernameVariable: 'DOCKER_USER',
                    passwordVariable: 'DOCKER_PASS'
                )]) {
                    bat 'echo %DOCKER_PASS% | docker login -u %DOCKER_USER% --password-stdin'
                    bat 'docker tag acuamont/acuamont-backend:%GIT_COMMIT% acuamont/acuamont-backend:latest'
                    bat 'docker tag acuamont/acuamont-frontend:%GIT_COMMIT% acuamont/acuamont-frontend:latest'
                    bat 'docker push acuamont/acuamont-backend:latest'
                    bat 'docker push acuamont/acuamont-backend:%GIT_COMMIT%'
                    bat 'docker push acuamont/acuamont-frontend:latest'
                    bat 'docker push acuamont/acuamont-frontend:%GIT_COMMIT%'
                }
            }
        }

        stage('12. Deploy a VPS') {
            when { branch 'main' }
            steps {
                withCredentials([
                    sshUserPrivateKey(
                        credentialsId: 'vps-ssh-key',
                        keyFileVariable: 'SSH_KEY',
                        usernameVariable: 'SSH_USER'
                    ),
                    file(credentialsId: 'acuamont-env-file', variable: 'ENV_FILE')
                ]) {
                    bat 'powershell -NoProfile -Command "$key=\'%SSH_KEY%\'; $acl=Get-Acl $key; $acl.SetAccessRuleProtection($true,$false); $identity=[System.Security.Principal.WindowsIdentity]::GetCurrent().Name; $rule=New-Object System.Security.AccessControl.FileSystemAccessRule($identity,\'FullControl\',\'Allow\'); $acl.SetAccessRule($rule); Set-Acl $key $acl"'
                    bat 'scp -i %SSH_KEY% -o StrictHostKeyChecking=no %ENV_FILE% %SSH_USER%@%VPS_IP%:/home/acuamont/.env'
                    bat "ssh -i %SSH_KEY% -o StrictHostKeyChecking=no %SSH_USER%@%VPS_IP% \"cd /home/acuamont && docker compose pull && docker compose up -d\""
                }
            }
        }
    }

    post {
        success {
            bat '''
                curl.exe -s -X POST -H "Authorization: token %GITHUB_TOKEN_PSW%" -H "Accept: application/vnd.github.v3+json" https://api.github.com/repos/%REPO_OWNER%/%REPO_NAME%/statuses/%GIT_COMMIT% -d "{\\"state\\": \\"success\\", \\"target_url\\": \\"%BUILD_URL%\\", \\"description\\": \\"Tests exitosos\\", \\"context\\": \\"CI/CD / Jenkins\\"}"
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
                curl.exe -s -X POST -H "Authorization: token %GITHUB_TOKEN_PSW%" -H "Accept: application/vnd.github.v3+json" https://api.github.com/repos/%REPO_OWNER%/%REPO_NAME%/statuses/%GIT_COMMIT% -d "{\\"state\\": \\"failure\\", \\"target_url\\": \\"%BUILD_URL%\\", \\"description\\": \\"Tests fallaron\\", \\"context\\": \\"CI/CD / Jenkins\\"}"
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
                curl.exe -s -X POST -H "Authorization: token %GITHUB_TOKEN_PSW%" -H "Accept: application/vnd.github.v3+json" https://api.github.com/repos/%REPO_OWNER%/%REPO_NAME%/statuses/%GIT_COMMIT% -d "{\\"state\\": \\"error\\", \\"target_url\\": \\"%BUILD_URL%\\", \\"description\\": \\"Error en la ejecucion del pipeline\\", \\"context\\": \\"CI/CD / Jenkins\\"}"
            '''
        }
    }
}
