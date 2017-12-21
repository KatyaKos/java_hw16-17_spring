pipeline {
	agent any
	stages {
		stage('build') {
			steps {
				sh 'cd hw1'
				sh './gradlew build'
			}
		}
	}
}