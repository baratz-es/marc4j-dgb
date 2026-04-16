pipeline {
  agent { label 'maven' }

  tools {
    git 'Default'  // Usa la instalación configurada en Jenkins
    jdk 'OPENJDK 17'
    maven 'apache-maven-3.9.9' //vesion de maven para el proyecto
  }
  options {
    ansiColor('xterm')
    timestamps()
    gitLabConnection('GitLab')
    disableConcurrentBuilds() // Evita ejecuciones concurrentes de la misma rama
    buildDiscarder logRotator(numToKeepStr: '5')
    realTimeJUnitReports()
  }
  triggers {
    snapshotDependencies()
    gitlab(
      triggerOnPush: true,
      triggerOnMergeRequest: false,
      branchFilterType: 'All',
      ciSkip: true
    )
  }
  post {
    failure {
      updateGitlabCommitStatus name: 'build', state: 'failed'
      script {
        notify.all 'failure'
      }
    }
    unstable {
      updateGitlabCommitStatus name: 'build', state: 'failed'
      script {
        notify.all 'unstable'
      }
    }
    fixed {
      updateGitlabCommitStatus name: 'build', state: 'success'
      script {
        notify.all 'fixed'
      }

      cleanWs()
    }
    success {
      updateGitlabCommitStatus name: 'build', state: 'success'
      cleanWs()
    }
    aborted {
      updateGitlabCommitStatus name: 'build', state: 'canceled'
      cleanWs()
    }
    notBuilt {
      updateGitlabCommitStatus name: 'build', state: 'skipped'
      cleanWs()
    }
  }
  environment {
    MAVEN_OPTS = '-Djava.net.preferIPv4Stack=true -XX:+TieredCompilation -XX:TieredStopAtLevel=1 -XX:+UseParallelGC'
    JOB_WITHOUT_BRANCH="${JOB_NAME.substring(0, JOB_NAME.lastIndexOf('/'))}"
  }
  stages {

    stage('Build') {
      steps {
          scmSkip(deleteBuild: true, skipPattern:'.*\\[ci skip\\].*')
          updateGitlabCommitStatus name: 'build', state: 'running'

          withMaven(
            globalMavenSettingsConfig: '3736bbee-3105-473d-87ea-07657f640551',
            mavenSettingsConfig: 'f0b67693-f8ff-4403-a48d-6953a3d00e7f',
            options: [pipelineGraphPublisher(lifecycleThreshold: 'install')]
          ) {
            sh "mvn clean compile -T4 -U"
          }
      }
    }
    stage('Unit Testing') {
      steps {
          updateGitlabCommitStatus name: 'build', state: 'running'

          lock(resource: "lock-${env.JOB_WITHOUT_BRANCH}") {
            echo "Bloqueo para el pipeline ${env.JOB_WITHOUT_BRANCH}"
            withMaven(
              globalMavenSettingsConfig: '3736bbee-3105-473d-87ea-07657f640551',
              mavenSettingsConfig: 'f0b67693-f8ff-4403-a48d-6953a3d00e7f',
              options: [pipelineGraphPublisher(lifecycleThreshold: 'install')]
            ) {
              sh "mvn verify -T4 -Dmaven.javadoc.skip=true"
            }
          }
      }
    }
    stage('Deploy') {
      steps {
          updateGitlabCommitStatus name: 'build', state: 'running'

          withMaven(
            globalMavenSettingsConfig: '3736bbee-3105-473d-87ea-07657f640551',
            mavenSettingsConfig: 'f0b67693-f8ff-4403-a48d-6953a3d00e7f',
            options: [pipelineGraphPublisher(lifecycleThreshold: 'install')]
          ) {
            sh "mvn deploy -T4 -DskipTests -Dmaven.main.skip=true -DaltSnapshotDeploymentRepository=nexus::http://mavenrepo.digibis.com:9999/nexus/content/repositories/snapshots -DaltReleaseDeploymentRepository=nexus::http://mavenrepo.digibis.com:9999/nexus/content/repositories/releases"
          }
      }
    }
  }
}

// vim: set ts=2 sw=2 tw=0 et :
