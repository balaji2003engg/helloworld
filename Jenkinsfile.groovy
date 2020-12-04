node 
{
    stage("checkout")
    {
        
         git branch: 'main', changelog: false, poll: false, url: 'https://github.com/balaji2003engg/helloworld.git'
    }
    
    stage("build"){
        
      sh  "chmod 755 gradlew"
      sh "./gradlew clean build"
    }
            
    stage("Artifacory"){
        
         def server = Artifactory.newServer url: 'https://balaji2003engg.jfrog.io/artifactory', username: 'balaji2003engg@gmail.com', password: 'Ch683212@'
        def uploadSpec = """{
             "files": [
                 {
                     "pattern": "build/libs/HelloWorld-0.0.1-SNAPSHOT.war",
                     "target": "libs-snapshot-local/HelloWorld/HelloWorld-1.0.${BUILD_NUMBER}.war"
                 }
                     ]
}"""
server.upload spec: uploadSpec
    }
    
    stage("deploy using ansible tower")
    {
         ansibleTower(
            towerServer: 'Ansibletower',
            jobTemplate: 'HelloWorld',
            importTowerLogs: true,
            inventory: 'AWSEC2',
            jobTags: '',
            limit: '',
            removeColor: false,
            verbose: true,
            credential: '',
            extraVars: '''---
            env: dev
            version: 1.0.$BUILD_NUMBER'''
        )
    }
    
}
