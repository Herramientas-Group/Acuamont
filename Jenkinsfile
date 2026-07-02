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
                        def reports = findFiles(glob: 'Backend/target/surefire-reports/TEST-*.xml')
                        def total = 0, passed = 0, failed = 0, skipped = 0
                        def table = ""

                        reports.each { file ->
                            def xml = new XmlSlurper().parse(file)
                            def name = xml.@name.text().split('\\.').last()
                            def t = xml.@tests.text().toInteger()
                            def f = xml.@failures.text().toInteger() + xml.@errors.text().toInteger()
                            def s = xml.@skipped.text().toInteger()
                            def p = t - f - s
                            total += t; passed += p; failed += f; skipped += s
                            def icon = f == 0 ? "✅" : "❌"
                            table += "| ${icon} ${name} | ${t} | ${p} | ${f} | ${s} |\n"
                        }

                        env.TESTS_TABLE   = table
                        env.TESTS_TOTAL   = total.toString()
                        env.TESTS_PASSED  = passed.toString()
                        env.TESTS_FAILED  = failed.toString()
                        env.TESTS_SKIPPED = skipped.toString()
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
                dir('Frontend') {
                    bat 'npm run test -- --run'
                }
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: 'Frontend/test-results/*.xml'
                }
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
                    def comment = """### CI Exitoso
- **Estado:** PAS\u00d3
- **Rama:** ${env.BRANCH_NAME}
- **Tests:** ${env.TESTS_PASSED ?: '0'} pasaron / ${env.TESTS_FAILED ?: '0'} fallaron / ${env.TESTS_SKIPPED ?: '0'} omitidos

| Clase de Test | Tests | Pasaron | Fallaron | Omitidos |
|---|---|---|---|---|
${env.TESTS_TABLE ?: ''}| **TOTAL** | **${env.TESTS_TOTAL ?: '0'}** | **${env.TESTS_PASSED ?: '0'}** | **${env.TESTS_FAILED ?: '0'}** | **${env.TESTS_SKIPPED ?: '0'}** |

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
                curl.exe -s -X POST -H "Authorization: token %GITHUB_TOKEN_PSW%" -H "Accept: application/vnd.github.v3+json" https://api.github.com/repos/%REPO_OWNER%/%REPO_NAME%/statuses/%GIT_COMMIT% -d "{\\"state\\": \\"failure\\", \\"target_url\\": \\"%BUILD_URL%\\", \\"description\\": \\"Tests fallaron\\", \\"context\\": \\"CI / Jenkins Backend\\"}"
            '''
            script {
                if (env.CHANGE_ID) {
                    def comment = """### Fallo en CI
- **Estado:** FALL\u00d3
- **Rama:** ${env.BRANCH_NAME}
- **Tests:** ${env.TESTS_PASSED ?: '0'} pasaron / ${env.TESTS_FAILED ?: '0'} fallaron / ${env.TESTS_SKIPPED ?: '0'} omitidos

| Clase de Test | Tests | Pasaron | Fallaron | Omitidos |
|---|---|---|---|---|
${env.TESTS_TABLE ?: ''}| **TOTAL** | **${env.TESTS_TOTAL ?: '0'}** | **${env.TESTS_PASSED ?: '0'}** | **${env.TESTS_FAILED ?: '0'}** | **${env.TESTS_SKIPPED ?: '0'}** |

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
                curl.exe -s -X POST -H "Authorization: token %GITHUB_TOKEN_PSW%" -H "Accept: application/vnd.github.v3+json" https://api.github.com/repos/%REPO_OWNER%/%REPO_NAME%/statuses/%GIT_COMMIT% -d "{\\"state\\": \\"error\\", \\"target_url\\": \\"%BUILD_URL%\\", \\"description\\": \\"Error en la ejecucion del pipeline\\", \\"context\\": \\"CI / Jenkins Backend\\"}"
            '''
        }
    }
}
