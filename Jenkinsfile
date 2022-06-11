String job_name = "${env.JOB_NAME}".split('/')[1] as String
String branch = "${env.JOB_NAME}".split('/')[0] as String
def projectName = "/var/jenkins_home/sol-misli-pipeline-configurations"
pipeline {
	agent {
		label 'mngkube05'
	}
	options { 
		disableConcurrentBuilds() 
	}
    environment {
    	SKIPTESTS = "true"
       }
	stages {
		stage('download deployment artifact') {
			steps {
				script {
					sh "git -C " + projectName + " fetch"
					sh "git -C " + projectName + " checkout test"
					sh "git -C " + projectName + " pull"
				}
			}
		}
		stage('build source code') {
			steps {
				sh 'docker run -i --rm -v $(pwd):/code -v /root/.m2:/root/.m2 maven:3-jdk-8 sh -c "cd /code; mvn clean package #-Dmaven.test.skip=${SKIPTESTS}"'
			}
		}
		stage('docker build') {
			steps {
				script {
					sh "cp " + projectName + "/${job_name}/Dockerfile ."
					sh "docker build . --build-arg APPLICATION=${job_name} -t ${env.REGISTRY_HOST}/${job_name}:test-v${env.BUILD_ID}"
				}
			}
		}
		stage('image push') {
			steps {
				sh "docker push ${env.REGISTRY_HOST}/${job_name}:test-v${env.BUILD_ID}"
			}
		}
		stage('image delete') {
			steps {
				sh "docker images | grep  ${env.REGISTRY_HOST}/${job_name} | grep test-v${env.BUILD_ID} | awk '{print \$3}' | xargs docker rmi"
			}
		}
		stage('create deployment artifact') {
			steps {
				script {
					sh "envsubst < " + projectName + "/${job_name}/deploy.yaml > .deploy.yaml"
					sh "envsubst < " + projectName + "/${job_name}/service.yaml > .service.yaml"
					sh "envsubst < " + projectName + "/${job_name}/configmap.yaml > .configmap.yaml"
					sh "envsubst < " + projectName + "/${job_name}/secret.yaml > .secret.yaml"
					sh "envsubst < " + projectName + "/${job_name}/ingress.yaml > .ingress.yaml"
         			sh "cp " + projectName + "/waitandupdateservice.sh .waitandupdateservice.sh; chmod +x .waitandupdateservice.sh"
				}
			}
		}
		stage('deploy k8s') {
			steps {
				sh "KUBECONFIG=/root/rke/test/kube_config_cluster.yml kubectl apply -f .deploy.yaml"
				sh "KUBECONFIG=/root/rke/test/kube_config_cluster.yml kubectl apply -f .secret.yaml"
				sh "KUBECONFIG=/root/rke/test/kube_config_cluster.yml kubectl apply -f .configmap.yaml"
				sh "KUBECONFIG=/root/rke/test/kube_config_cluster.yml kubectl apply -f .ingress.yaml"
				sh "KUBECONFIG=/root/rke/test/kube_config_cluster.yml BUILD_ID=${BUILD_ID} APP_NAME=${job_name}-deployment  ./.waitandupdateservice.sh"
			}
		}
		stage('Grafana Annotation'){
			steps{
				script{
					if (env.BRANCH_NAME == "master") {
						sh "cp " + projectName + "/grafanaBackendCurl.sh .grafanaBackendCurl.sh; chmod +x .grafanaBackendCurl.sh"
                        sh "BUILD_ID=${BUILD_ID} APP_NAME=${job_name}-deployment  ./.grafanaBackendCurl.sh"
                    }
				}
			}
		}
        stage ('push version tag')
        {
          when  { expression {env.BRANCH_NAME == 'test' }}
			steps {
               withCredentials([usernamePassword(credentialsId: 'bitbucket', usernameVariable: 'bitbucket_user', passwordVariable: 'bitbucket_pass')])
                 {
				    sh "git fetch --all"
				    sh "git tag -f test-v${BUILD_ID}"
				    sh "git push -f https://$bitbucket_user:$bitbucket_pass@bitbucket.org/misli-team/${job_name}.git test-v${BUILD_ID}"
                 }
			}
		}
	}
	post {
		success {
			office365ConnectorSend message: "BUILD: v${env.BUILD_ID}", status: "SUCCESS", webhookUrl: "${env.TEAMS_HOOK}"
		}
		failure {
			office365ConnectorSend message: "BUILD: v${env.BUILD_ID}", status: "FAILURE", webhookUrl: "${env.TEAMS_HOOK}"
		}
		unsuccessful {
			office365ConnectorSend message: "BUILD: v${env.BUILD_ID}", status: "UNSUCCESSFUL", webhookUrl: "${env.TEAMS_HOOK}"
		}
	}
}