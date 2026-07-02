pipeline {
    agent any

    tools {
        jdk 'Java21'
    }

    environment {
        GITHUB_TOKEN = credentials('github-credential')
        REPO_OWNER   = 'Herramientas-Group'
        REPO_NAME    = 'Acuamont'
        AZURE_VM_IP  = credentials('azure-vm-ip')
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

        stage('3. Backend: Dependencias') {
            when { anyOf { changeset "Backend/**"; changeset "pom.xml"; changeset "Jenkinsfile" } }
            steps {
                dir('Backend') {
                    bat 'mvnw.cmd dependency:resolve -B'
                }
            }
        }

        stage('4. Backend: Compilar') {
            when { anyOf { changeset "Backend/**"; changeset "pom.xml"; changeset "Jenkinsfile" } }
            steps {
                dir('Backend') {
                    bat 'mvnw.cmd compile -B'
                }
            }
        }

        stage('5. Backend: Ejecutar Tests') {
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
                    script {
                        bat '''
powershell -NoProfile -Command "$r = Get-ChildItem Backend/target/surefire-reports/TEST-*.xml | ForEach-Object { [xml]$c = Get-Content -Raw $_; [PSCustomObject]@{name=$c.testsuite.name.Split('.')[-1]; tests=[int]$c.testsuite.tests; failures=([int]$c.testsuite.failures + [int]$c.testsuite.errors); skipped=[int]$c.testsuite.skipped} }; ConvertTo-Json $r -Compress | Out-File -FilePath ci-test-results.json -Encoding ASCII"
'''
                        def reports = readJSON(file: 'ci-test-results.json')
                        if (!(reports instanceof List)) { reports = [reports] }

                        def total = 0, passed = 0, failed = 0, skipped = 0
                        def table = ""
                        def failedTable = ""
                        reports.each { r ->
                            def t = r.tests
                            def f = r.failures
                            def s = r.skipped
                            def p = t - f - s
                            total += t; passed += p; failed += f; skipped += s
                            def icon = f == 0 ? "✅" : "❌"
                            table += "| ${icon} ${r.name} | ${t} | ${p} | ${f} | ${s} |\n"
                            if (f > 0) {
                                failedTable += "| ${icon} ${r.name} | ${t} | ${p} | ${f} | ${s} |\n"
                            }
                        }

                        env.TESTS_TABLE       = table
                        env.TESTS_FAILED_TABLE = failedTable
                        env.TESTS_TOTAL       = total.toString()
                        env.TESTS_PASSED      = passed.toString()
                        env.TESTS_FAILED      = failed.toString()
                        env.TESTS_SKIPPED     = skipped.toString()
                    }
                }
            }
        }

        stage('6. Backend: Empaquetar') {
            when { anyOf { changeset "Backend/**"; changeset "pom.xml"; changeset "Jenkinsfile" } }
            steps {
                dir('Backend') {
                    bat 'mvnw.cmd package -DskipTests -B'
                }
            }
        }

        stage('7. Backend: Archivar') {
            when { anyOf { changeset "Backend/**"; changeset "pom.xml"; changeset "Jenkinsfile" } }
            steps {
                archiveArtifacts artifacts: 'Backend/target/*.jar', fingerprint: true
            }
        }

        stage('10. Backend: Deploy a Azure') {
            when { branch 'main' }
            steps {
                dir('Backend') {
                    withCredentials([sshUserPrivateKey(
                        credentialsId: 'azure-vm-prod-key',
                        keyFileVariable: 'SSH_KEY',
                        usernameVariable: 'SSH_USER'
                    )]) {
                        bat "icacls \"%SSH_KEY%\" /inheritance:r /grant:r \"%USERNAME%:F\""
                        echo 'Subiendo .jar a Azure VM...'
                        bat "scp -i %SSH_KEY% -o StrictHostKeyChecking=no target/*.jar %SSH_USER%@%AZURE_VM_IP%:/home/azureuser/acuamont/backend/acuamont-backend.jar"
                        echo 'Reiniciando servicio...'
                        bat "ssh -i %SSH_KEY% -o StrictHostKeyChecking=no %SSH_USER%@%AZURE_VM_IP% \"sudo systemctl restart acuamont-backend.service\""
                    }
                }
            }
        }

        stage('8. Frontend: Dependencias') {
            when { anyOf { changeset "Frontend/**"; changeset "package.json"; changeset "Jenkinsfile" } }
            steps {
                dir('Frontend') {
                    bat 'npm install'
                }
            }
        }

        stage('9. Frontend: Ejecutar Tests') {
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
                    script {
                        bat '''
powershell -NoProfile -Command "if (Test-Path Frontend/test-results/junit.xml) { [xml]$c = Get-Content -Raw Frontend/test-results/junit.xml; $suites = $c.testsuites.testsuite; if ($suites -isnot [array]) { $suites = @($suites) }; $r = $suites | ForEach-Object { [PSCustomObject]@{name=$_.name.Split('/')[-1].Split('.')[0]; tests=[int]$_.tests; failures=([int]$_.failures + [int]$_.errors); skipped=[int]$_.skipped} }; ConvertTo-Json $r -Compress | Out-File -FilePath ci-frontend-results.json -Encoding ASCII } else { '[]' | Out-File -FilePath ci-frontend-results.json -Encoding ASCII }"
'''
                        def fReports = readJSON(file: 'ci-frontend-results.json')
                        if (!(fReports instanceof List)) { fReports = [fReports] }
                        def fTotal = 0, fPassed = 0, fFailed = 0, fSkipped = 0
                        def fTable = ""
                        def fFailedTable = ""
                        fReports.each { r ->
                            def t = r.tests; def f = r.failures; def s = r.skipped; def p = t - f - s
                            fTotal += t; fPassed += p; fFailed += f; fSkipped += s
                            def icon = f == 0 ? "✅" : "❌"
                            fTable += "| ${icon} ${r.name} | ${t} | ${p} | ${f} | ${s} |\n"
                            if (f > 0) {
                                fFailedTable += "| ${icon} ${r.name} | ${t} | ${p} | ${f} | ${s} |\n"
                            }
                        }
                        env.FRONTEND_TESTS_TABLE       = fTable
                        env.FRONTEND_TESTS_FAILED_TABLE = fFailedTable
                        env.FRONTEND_TESTS_TOTAL       = fTotal.toString()
                        env.FRONTEND_TESTS_PASSED      = fPassed.toString()
                        env.FRONTEND_TESTS_FAILED      = fFailed.toString()
                        env.FRONTEND_TESTS_SKIPPED     = fSkipped.toString()
                    }
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
- **Estado:** PAS\u00d3
- **Rama:** ${env.BRANCH_NAME}
- **Backend:** ${env.TESTS_PASSED ?: '0'} pasaron / ${env.TESTS_FAILED ?: '0'} fallaron
- **Frontend:** ${env.FRONTEND_TESTS_PASSED ?: '0'} pasaron / ${env.FRONTEND_TESTS_FAILED ?: '0'} fallaron

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
                    def backendFailed = (env.TESTS_FAILED ?: '0') != '0'
                    def frontendFailed = (env.FRONTEND_TESTS_FAILED ?: '0') != '0'

                    def backendSection = ""
                    if (backendFailed) {
                        backendSection = """
**Tests fallidos - Backend:**
| Clase de Test | Tests | Pasaron | Fallaron | Omitidos |
|---|---|---|---|---|
${env.TESTS_FAILED_TABLE ?: ''}"""
                    }

                    def frontendSection = ""
                    if (frontendFailed) {
                        frontendSection = """
**Tests fallidos - Frontend:**
| Test | Tests | Pasaron | Fallaron | Omitidos |
|---|---|---|---|---|
${env.FRONTEND_TESTS_FAILED_TABLE ?: ''}"""
                    }

                    def comment = """### Fallo en CI
- **Estado:** FALL\u00d3
- **Rama:** ${env.BRANCH_NAME}
- **Backend:** ${env.TESTS_PASSED ?: '0'} pasaron / ${env.TESTS_FAILED ?: '0'} fallaron
- **Frontend:** ${env.FRONTEND_TESTS_PASSED ?: '0'} pasaron / ${env.FRONTEND_TESTS_FAILED ?: '0'} fallaron
${backendSection}${frontendSection}

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
