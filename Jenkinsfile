pipeline {
	agent any
	stages {
		stage('build') {
			steps {
				sh 'cd hw1'
				sh 'chmod +x ./gradlew'
				sh './gradlew build'
			}
		}
	}
}