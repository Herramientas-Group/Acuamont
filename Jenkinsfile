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
                    script {
                        def testResults = currentBuild.testResults
                        def total = 0, passed = 0, failed = 0, skipped = 0
                        def table = ""

                        if (testResults != null) {
                            total = testResults.totalCount
                            passed = testResults.passCount
                            failed = testResults.failCount
                            skipped = testResults.skipCount

                            def suites = testResults.getSuites()
                            suites.each { suite ->
                                def name = suite.getName()
                                def t = suite.getTests()
                                def f = suite.getFailures()
                                def s = suite.getSkipped()
                                def p = t - f - s
                                def icon = (f + s) == 0 ? "✅" : "❌"
                                table += "| ${icon} ${name} | ${t} | ${p} | ${f} | ${s} |\n"
                            }
                        }

                        env.TESTS_TABLE = table
                        env.TESTS_TOTAL = total.toString()
                        env.TESTS_PASSED = passed.toString()
                        env.TESTS_FAILED = failed.toString()
                        env.TESTS_SKIPPED = skipped.toString()
                    }
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
                    def comment = """### CI Exitoso
- **Estado:** PAS\u00d3
- **Rama:** ${env.BRANCH_NAME}
- **Tests:** ${env.TESTS_PASSED} pasaron / ${env.TESTS_FAILED} fallaron / ${env.TESTS_SKIPPED} omitidos

| Clase de Test | Tests | Pasaron | Fallaron | Omitidos |
|---|---|---|---|---|
${env.TESTS_TABLE}| **TOTAL** | **${env.TESTS_TOTAL}** | **${env.TESTS_PASSED}** | **${env.TESTS_FAILED}** | **${env.TESTS_SKIPPED}** |

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

        failure {
            bat '''
                curl.exe -s -X POST -H "Authorization: token %GITHUB_TOKEN_PSW%" -H "Accept: application/vnd.github.v3+json" https://api.github.com/repos/%REPO_OWNER%/%REPO_NAME%/statuses/%GIT_COMMIT% -d "{\\"state\\": \\"failure\\", \\"target_url\\": \\"%BUILD_URL%\\", \\"description\\": \\"Tests fallaron\\", \\"context\\": \\"CI / Jenkins Backend\\"}"
            '''
            script {
                if (env.CHANGE_ID) {
                    def comment = """### Fallo en CI
- **Estado:** FALL\u00d3
- **Rama:** ${env.BRANCH_NAME}
- **Tests:** ${env.TESTS_PASSED} pasaron / ${env.TESTS_FAILED} fallaron / ${env.TESTS_SKIPPED} omitidos

| Clase de Test | Tests | Pasaron | Fallaron | Omitidos |
|---|---|---|---|---|
${env.TESTS_TABLE}| **TOTAL** | **${env.TESTS_TOTAL}** | **${env.TESTS_PASSED}** | **${env.TESTS_FAILED}** | **${env.TESTS_SKIPPED}** |

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
    }
}
